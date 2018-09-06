#version 430
layout(local_size_x=32, local_size_y=32) in;

#define MAX_POINTS      256
#define MAX_CONTOURS    16

#define MAX_GLYPH_ROW   16
#define MAX_GLYPH       (MAX_GLYPH_ROW * MAX_GLYPH_ROW)
#define GLYPH_SIZE      64

struct Point {
    vec2 pos;
    int onCurve;
};

layout(rgba8, binding=0) uniform image2D out_image;

layout(std430, binding=1) buffer GlyphInfo {
    int numPoints[MAX_GLYPH];
    int numContours[MAX_GLYPH];
    int offsets[MAX_GLYPH];
    ivec2 min[MAX_GLYPH];
    ivec2 max[MAX_GLYPH];
} glyphInfo;

layout(std430, binding=2) buffer GlyphData {
    float data[];
} glyphData;

shared int glyphIndex;
shared int numPoints;
shared int numContours;
shared ivec2 glyphMin;
shared ivec2 glyphMax;
shared vec2 topLeft[3];
shared int contours[MAX_CONTOURS];
shared Point points[MAX_POINTS];

const float GLYPH_SCALE = 16.0 * 200.0 / GLYPH_SIZE;
const float MAX_DIST = GLYPH_SCALE * (GLYPH_SIZE / 8.0);
const float MIN_DIST = GLYPH_SCALE * (GLYPH_SIZE / -8.0);

float sNormDistance(vec2 point);
float distanceToLine(vec2 A, vec2 B, vec2 p);
bool checkLineLineIntersect(vec2 p1, vec2 q1, vec2 p2, vec2 q2);
int checkLineBezierIntersect(vec2 A, vec2 B, vec2 C, vec2 P1, vec2 P2);
float sdBezier(vec2 A, vec2 B, vec2 C, vec2 p);

void main() {
    ivec2 globalPos = ivec2(gl_GlobalInvocationID.xy);
    vec2 localPos = vec2(globalPos % ivec2(GLYPH_SIZE));

    if(gl_LocalInvocationIndex == 0) {
        ivec2 indexPos = ivec2(floor(vec2(globalPos) / GLYPH_SIZE));
        glyphIndex = indexPos.y * MAX_GLYPH_ROW + indexPos.x;

        numPoints = glyphInfo.numPoints[glyphIndex];
        numContours = glyphInfo.numContours[glyphIndex];

        if(numPoints >= MAX_POINTS) numPoints = MAX_POINTS - 1;
        if(numContours >= MAX_CONTOURS) numContours = MAX_CONTOURS - 1;

        glyphMin = glyphInfo.min[glyphIndex];
        glyphMax = glyphInfo.max[glyphIndex];

        topLeft[0] = (vec2(-GLYPH_SIZE / 2.0, GLYPH_SIZE + GLYPH_SIZE / 4.0) + vec2(0.01)) * GLYPH_SCALE + vec2((glyphMax.x - glyphMin.x) / 2.0, 0.0);
        topLeft[1] = topLeft[0] + vec2(-GLYPH_SCALE, GLYPH_SCALE);
        topLeft[2] = topLeft[0] + vec2(GLYPH_SCALE, -GLYPH_SCALE);

        int offset = glyphInfo.offsets[glyphIndex];
        for(int i=0; i<numContours; ++i) {
            contours[i] = int(glyphData.data[offset + i]);
        }
        offset += glyphInfo.numContours[glyphIndex];
        for(int i=0; i<numPoints; ++i) {
            points[i] = Point(vec2(glyphData.data[offset + i * 3], glyphData.data[offset + i * 3 + 1]), int(glyphData.data[offset + i * 3 + 2]));
        }
    }
    barrier();

    vec2 point = (vec2(localPos.x - GLYPH_SIZE / 2.0, GLYPH_SIZE - localPos.y - GLYPH_SIZE / 4.0) + vec2(0.005)) * GLYPH_SCALE + vec2((glyphMax.x - glyphMin.x) / 2.0, 0.0);
    vec3 color = vec3(sNormDistance(point));

    imageStore(out_image, globalPos, vec4(color, 1.0));
}

float sNormDistance(vec2 point) {
    float outerDist = MAX_DIST;
    float innerDist = MIN_DIST;

    ivec3 crosses = ivec3(0);
    for(int c = 0; c < numContours; ++c) {
        int length = contours[c] + 1;
        int offset = 0;
        if(c - 1 >= 0) offset = contours[c - 1] + 1;
        for(int i = offset; i < length; ++i) {
            Point point1 = points[i];
            Point point2 = points[offset + (i - offset + 1) % (length - offset)];

            vec2 pointA = point1.pos;
            vec2 pointB = point2.pos;

            if(point1.onCurve == 1 && point2.onCurve == 1) {
                float d = min(MAX_DIST, max(MIN_DIST, distanceToLine(pointA, pointB, point)));
                if(d > 0.0) outerDist = min(outerDist, d);
                else if(d < 0.0) innerDist = max(innerDist, d);

                if(checkLineLineIntersect(pointA, pointB, point, topLeft[0])) crosses.x++;
                if(checkLineLineIntersect(pointA, pointB, point, topLeft[1])) crosses.y++;
                if(checkLineLineIntersect(pointA, pointB, point, topLeft[2])) crosses.z++;
            }
            else {
                Point point3 = points[offset + (i - offset + 2) % (length - offset)];
                vec2 curveCtrl = pointB;

                if(point1.onCurve == 0 && point2.onCurve == 1) continue;
                if(point1.onCurve == 0 && point2.onCurve == 0) pointA = (pointA + point2.pos) / 2.0;
                if(point2.onCurve == 0 && point3.onCurve == 1) pointB = point3.pos;
                if(point2.onCurve == 0 && point3.onCurve == 0) pointB = (pointB + point3.pos) / 2.0;

                float d = min(MAX_DIST, max(MIN_DIST, sdBezier(pointA, curveCtrl, pointB, point)));
                if(d > 0.0) outerDist = min(outerDist, d);
                else if(d < 0.0) innerDist = max(innerDist, d);

                crosses.x += checkLineBezierIntersect(pointA, curveCtrl, pointB, point, topLeft[0]);
                crosses.y += checkLineBezierIntersect(pointA, curveCtrl, pointB, point, topLeft[1]);
                crosses.z += checkLineBezierIntersect(pointA, curveCtrl, pointB, point, topLeft[2]);
            }
        }
    }

    float dist = outerDist;
    if((crosses.x % 2) + (crosses.y % 2) + (crosses.z % 2) >= 2) dist = innerDist;

    return (min(1.0, max(-1.0, -dist / MAX_DIST)) + 1.0) / 2.0;
}

float distanceToLine(vec2 A, vec2 B, vec2 p) {
    vec2 ab = B - A;
    vec2 ap = p - A;
    vec2 bp = p - B;
    float cross = ab.x * ap.y - ab.y * ap.x;
    float d = cross / length(ab);
    float dotAB_BP = dot(normalize(ab), normalize(bp));
    float dotAB_AP = dot(normalize(ab), normalize(ap));
    if(dotAB_BP > 0.0) d = length(bp) * sign(cross);
    if(dotAB_AP < 0.0) d = length(ap) * sign(cross);
    return d;
}

int orientation(vec2 p, vec2 q, vec2 r) {
    float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
    if(val == 0.0) return 0;
    else if(val > 0.0) return 1;
    else return 2;
}

bool onSegment(vec2 p, vec2 q, vec2 r) {
    return q.x <= max(p.x, r.x) && q.x >= min(p.x, r.x) && q.y <= max(p.y, r.y) && q.y >= min(p.y, r.y);
}

bool checkLineLineIntersect(vec2 p1, vec2 q1, vec2 p2, vec2 q2) {
    int o1 = orientation(p1, q1, p2);
    int o2 = orientation(p1, q1, q2);
    int o3 = orientation(p2, q2, p1);
    int o4 = orientation(p2, q2, q1);
    return (o1 != o2 && o3 != o4) ||
            (o1 == 0 && onSegment(p1, p2, q1)) ||
            (o2 == 0 && onSegment(p1, q2, q1)) ||
            (o3 == 0 && onSegment(p2, p1, q2)) ||
            (o4 == 0 && onSegment(p2, q1, q2));
}

vec2 quadraticBezier(vec2 a, vec2 b, vec2 c, float t) {
    return pow(1.0 - t, 2) * a + 2.0 * t * (1.0 - t) * b + pow(t, 2) * c;
}

int checkLineBezierIntersect(vec2 A, vec2 B, vec2 C, vec2 P1, vec2 P2) {
    vec2 nA = A - P1;
    vec2 nB = B - P1;
    vec2 nC = C - P1;
    vec2 nP = P2 - P1;

    float angle = atan(nP.y, nP.x);
    float cos = -cos(angle);
    float sin = sin(angle);

    nA = vec2(nA.x * cos - nA.y * sin, nA.x * sin + nA.y * cos);
    nB = vec2(nB.x * cos - nB.y * sin, nB.x * sin + nB.y * cos);
    nC = vec2(nC.x * cos - nC.y * sin, nC.x * sin + nC.y * cos);
    nP = vec2(nP.x * cos - nP.y * sin, nP.x * sin + nP.y * cos);

    float a = nA.y;
    float b = nB.y;
    float c = nC.y;

    if(a - 2 * b + c != 0.0) {
        float t1 = (a - b - sqrt(b * b - c * a)) / (a - 2 * b + c);
        float t2 = (a - b + sqrt(b * b - c * a)) / (a - 2 * b + c);
        int count = 0;
        if (t1 >= 0.0 && t1 <= 1.0) {
            vec2 curve = quadraticBezier(nA, nB, nC, t1);
            if(curve.x >= min(0.0, nP.x) && curve.x <= max(0.0, nP.x)) count++;
        }
        if (t2 >= 0.0f && t2 <= 1.0) {
            vec2 curve = quadraticBezier(nA, nB, nC, t2);
            if(curve.x >= min(0.0, nP.x) && curve.x <= max(0.0, nP.x)) count++;
        }
        return count;
    }

    return 0;
}

// Signed Distance to a Quadratic Bezier Curve
// - Adam Simmons (@adamjsimmons) 2015
//
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
//
// Inspired by http://www.pouet.net/topic.php?which=9119
// and various shaders by iq, T21, and demofox
//
// I needed the -signed- distance to a quadratic bezier
// curve but couldn't find any examples online that
// were both fast and precise. This is my solution.
//
// v1 - Initial release
// v2 - Faster and more robust sign computation
//

// Test if point p crosses line (a, b), returns sign of result
float testCross(vec2 a, vec2 b, vec2 p) {
    return sign((b.y-a.y) * (p.x-a.x) - (b.x-a.x) * (p.y-a.y));
}

// Determine which side we're on (using barycentric parameterization)
float signBezier(vec2 A, vec2 B, vec2 C, vec2 p)
{
    vec2 a = C - A, b = B - A, c = p - A;
    vec2 bary = vec2(c.x*b.y-b.x*c.y,a.x*c.y-c.x*a.y) / (a.x*b.y-b.x*a.y);
    vec2 d = vec2(bary.y * 0.5, 0.0) + 1.0 - bary.x - bary.y;
    return mix(sign(d.x * d.x - d.y), mix(-1.0, 1.0,
        step(testCross(A, B, p) * testCross(B, C, p), 0.0)),
        step((d.x - d.y), 0.0)) * testCross(A, C, B);
}

// Solve cubic equation for roots
vec3 solveCubic(float a, float b, float c)
{
    float p = b - a*a / 3.0, p3 = p*p*p;
    float q = a * (2.0*a*a - 9.0*b) / 27.0 + c;
    float d = q*q + 4.0*p3 / 27.0;
    float offset = -a / 3.0;
    if(d >= 0.0) {
        float z = sqrt(d);
        vec2 x = (vec2(z, -z) - q) / 2.0;
        vec2 uv = sign(x)*pow(abs(x), vec2(1.0/3.0));
        return vec3(offset + uv.x + uv.y);
    }
    float v = acos(-sqrt(-27.0 / p3) * q / 2.0) / 3.0;
    float m = cos(v), n = sin(v)*1.732050808;
    return vec3(m + m, -n - m, n - m) * sqrt(-p / 3.0) + offset;
}

// Find the signed distance from a point to a bezier curve
float sdBezier(vec2 A, vec2 B, vec2 C, vec2 p)
{
    B = mix(B + vec2(1e-4), B, abs(sign(B * 2.0 - A - C)));
    vec2 a = B - A, b = A - B * 2.0 + C, c = a * 2.0, d = A - p;
    vec3 k = vec3(3.*dot(a,b),2.*dot(a,a)+dot(d,b),dot(d,a)) / dot(b,b);
    vec3 t = clamp(solveCubic(k.x, k.y, k.z), 0.0, 1.0);
    vec2 pos = A + (c + b*t.x)*t.x;
    float dis = length(pos - p);
    pos = A + (c + b*t.y)*t.y;
    dis = min(dis, length(pos - p));
    pos = A + (c + b*t.z)*t.z;
    dis = min(dis, length(pos - p));
    return dis * -signBezier(A, B, C, p);
}