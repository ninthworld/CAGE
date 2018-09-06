package fonty;

import cage.core.graphics.GraphicsDevice;
import cage.core.graphics.texture.Texture2D;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;
import java.nio.ByteBuffer;

public class SDF {

    private GraphicsDevice device;
    private TrueTypeFont font;

    public SDF(GraphicsDevice device, TrueTypeFont font) {
        this.device = device;
        this.font = font;
    }

    public Texture2D generateFontSDF(int glyphSize) {
        int width = glyphSize * 16;
        int height = glyphSize * 16;
        Texture2D texture = device.createTexture2D(width, height);
        ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);
        for(int y=0; y<16; ++y) {
            for(int x=0; x<16; ++x) {
                TrueTypeFont.Glyph glyph = font.readGlyph(y * 16 + x);
                for(int ly=0; ly<glyphSize; ++ly) {
                    for(int lx=0; lx<glyphSize; ++lx) {
                        Vector3f signedDist = new Vector3f(getSignedDistance(lx, ly, glyphSize, glyphSize, glyph));
                        int index = (y * 16 * glyphSize * glyphSize + x * glyphSize + ly * width + lx) * 4;
                        data.put(index+0, (byte)(signedDist.x * 255));
                        data.put(index+1, (byte)(signedDist.y * 255));
                        data.put(index+2, (byte)(signedDist.z * 255));
                        data.put(index+3, (byte)255);
                    }
                }
            }
        }
        texture.writeData(data);
        return texture;
    }

    public Texture2D generateGlyphSDF(int width, int height, char c) {
        TrueTypeFont.Glyph glyph = font.readGlyph(c - 30);
        Texture2D texture = device.createTexture2D(width, height);
        ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);
        for(int h=0; h<height; ++h) {
            for(int w=0; w<width; ++w) {
                Vector3f signedDist = new Vector3f(getSignedDistance(w, h, width, height, glyph));
                data.put((byte)(signedDist.x * 255)).put((byte)(signedDist.y * 255)).put((byte)(signedDist.z * 255)).put((byte)255);
            }
        }
        data.rewind();
        texture.writeData(data);
        return texture;
    }

    private static Vector3fc getSignedDistance(float x, float y, float width, float height, TrueTypeFont.Glyph glyph) {
        float GLYPH_SCALE = 16.0f * 200.0f / width;
        Vector2fc point = new Vector2f(x - width / 2.0f, height - y - height / 4.0f).add(0.01f, 0.01f).mul(GLYPH_SCALE).add((glyph.xMax - glyph.xMin) / 2.0f, 0.0f);
        Vector2fc topLeft = new Vector2f(-width/2.0f, height + height / 4.0f).add(0.01f, 0.01f).mul(GLYPH_SCALE).add((glyph.xMax - glyph.xMin) / 2.0f, 0.0f);
        float MAX_DIST = GLYPH_SCALE * (width / 8.0f);
        float MIN_DIST = GLYPH_SCALE * (width / -8.0f);
        float outerDist = MAX_DIST;
        float innerDist = MIN_DIST;

        int intA = 0, intB = 0, intC = 0;
        for(int c=0; c<glyph.numberOfContours; ++c) {
            int offset = (c - 1 >= 0 ? glyph.contourEnds[c - 1] + 1 : 0);
            int length = glyph.contourEnds[c] + 1;
            for (int i=offset; i < length; ++i) {
                TrueTypeFont.Point point1 = glyph.points[i];
                TrueTypeFont.Point point2 = glyph.points[offset + (i - offset + 1) % (length - offset)];

                Vector2f pointA = new Vector2f(point1.x, point1.y);
                Vector2f pointB = new Vector2f(point2.x, point2.y);

                if (point1.onCurve && point2.onCurve) {
                    float d = Math.min(MAX_DIST, Math.max(MIN_DIST, getDistanceToLine(point, pointA, pointB)));
                    if (d > 0.0f) {
                        outerDist = Math.min(outerDist, d);
                    } else if (d < 0.0f) {
                        innerDist = Math.max(innerDist, d);
                    }

                    if (checkLineLineIntersect(pointA, pointB, point, topLeft)) intA++;
                    if (checkLineLineIntersect(pointA, pointB, point, topLeft.add(-GLYPH_SCALE, GLYPH_SCALE, new Vector2f()))) intB++;
                    if (checkLineLineIntersect(pointA, pointB, point, topLeft.add(GLYPH_SCALE, -GLYPH_SCALE, new Vector2f()))) intC++;
                }
                else {
                    TrueTypeFont.Point point3 = glyph.points[offset + (i - offset + 2) % (length - offset)];
                    Vector2f curveCtrl = new Vector2f(point2.x, point2.y);

                    if (!point1.onCurve && point2.onCurve) {
                        continue;
                    }

                    if (!point1.onCurve && !point2.onCurve) {
                        pointA.add(point2.x, point2.y).mul(0.5f);
                    }

                    if (!point2.onCurve && point3.onCurve) {
                        pointB.set(point3.x, point3.y);
                    }

                    if (!point2.onCurve && !point3.onCurve) {
                        pointB.add(point3.x, point3.y).mul(0.5f);
                    }

                    float d = Math.min(MAX_DIST, Math.max(MIN_DIST, sdBezier(pointA, curveCtrl, pointB, point)));
                    if(d > 0.0f) {
                        outerDist = Math.min(outerDist, d);
                    }
                    else if(d < 0.0f) {
                        innerDist = Math.max(innerDist, d);
                    }

                    intA += checkLineBezierIntersectPartial(pointA, curveCtrl, pointB, topLeft, point);
                    intB += checkLineBezierIntersectPartial(pointA, curveCtrl, pointB, topLeft.add(-GLYPH_SCALE, GLYPH_SCALE, new Vector2f()), point);
                    intC += checkLineBezierIntersectPartial(pointA, curveCtrl, pointB, topLeft.add(GLYPH_SCALE, -GLYPH_SCALE, new Vector2f()), point);
                }
            }
        }

        float dist = outerDist;
        if((intA % 2) + (intB % 2) + (intC % 2) >= 2) {
            dist = innerDist;
        }

        dist = (Math.min(1.0f, Math.max(-1.0f, -dist / MAX_DIST)) + 1.0f) / 2.0f;
        return new Vector3f(dist);
    }

    private static final float invRoot2 = 1.0f / (float)Math.sqrt(2.0f);
    private static final float root3Over2 = (float)Math.sqrt(3.0f) / 2.0f;
    private static float getDistanceToLine(Vector2fc point, Vector2fc linePointA, Vector2fc linePointB) {
        Vector2f ab = linePointB.sub(linePointA, new Vector2f());
        Vector2f ap = point.sub(linePointA, new Vector2f());
        Vector2f bp = point.sub(linePointB, new Vector2f());
        float cross = ab.x * ap.y - ab.y * ap.x;
        float d = cross / ab.length();
        float dotAB_BP = ab.normalize(new Vector2f()).dot(bp.normalize(new Vector2f()));
        float dotAB_AP = ab.normalize(new Vector2f()).dot(ap.normalize(new Vector2f()));
        if (dotAB_BP > 0.0f) {
            d = bp.length() * Math.signum(cross);
        }
        if (dotAB_AP < 0.0f) {
            d = ap.length() * Math.signum(cross);
        }
        return d;
    }

    private static boolean checkLineLineIntersect(Vector2fc p1, Vector2fc q1, Vector2fc p2, Vector2fc q2) {
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

    private static int orientation(Vector2fc p, Vector2fc q, Vector2fc r) {
        float val = (q.y() - p.y()) * (r.x() - q.x()) - (q.x() - p.x()) * (r.y() - q.y());
        return (val == 0.0f ? 0 : val > 0 ? 1 : 2);
    }

    private static boolean onSegment(Vector2fc p, Vector2fc q, Vector2fc r) {
        return q.x() <= Math.max(p.x(), r.x()) &&
                q.x() >= Math.min(p.x(), r.x()) &&
                q.y() <= Math.max(p.y(), r.y()) &&
                q.y() >= Math.min(p.y(), r.y());
    }

    private static int checkLineBezierIntersectPartial(Vector2fc A, Vector2fc B, Vector2fc C, Vector2fc P1, Vector2fc P2) {
        Vector2f nA = A.sub(P1, new Vector2f());
        Vector2f nB = B.sub(P1, new Vector2f());
        Vector2f nC = C.sub(P1, new Vector2f());
        Vector2f nP = P2.sub(P1, new Vector2f());

        float angle = (float)Math.atan2(nP.y, nP.x);
        float cos = -(float)Math.cos(angle);
        float sin = (float)Math.sin(angle);

        nA.set(nA.x * cos - nA.y * sin, nA.x * sin + nA.y * cos);
        nB.set(nB.x * cos - nB.y * sin, nB.x * sin + nB.y * cos);
        nC.set(nC.x * cos - nC.y * sin, nC.x * sin + nC.y * cos);
        nP.set(nP.x * cos - nP.y * sin, nP.x * sin + nP.y * cos);

        float a = nA.y;
        float b = nB.y;
        float c = nC.y;

        if(a - 2 * b + c != 0.0f) {
            float t1 = (a - b - (float) Math.sqrt(b * b - c * a)) / (a - 2 * b + c);
            float t2 = (a - b + (float) Math.sqrt(b * b - c * a)) / (a - 2 * b + c);
            int count = 0;
            if (t1 >= 0.0f && t1 <= 1.0f) {
                Vector2f curve = quadraticBezier(nA, nB, nC, t1);
                if(curve.x >= Math.min(0.0f, nP.x) && curve.x <= Math.max(0.0f, nP.x)) count++;
            }
            if (t2 >= 0.0f && t2 <= 1.0f) {
                Vector2f curve = quadraticBezier(nA, nB, nC, t2);
                if(curve.x >= Math.min(0.0f, nP.x) && curve.x <= Math.max(0.0f, nP.x)) count++;
            }
            return count;
        }

        return 0;
    }

    private static Vector2f quadraticBezier(Vector2fc a, Vector2fc b, Vector2fc c, float t) {
        return new Vector2f(
                (float)Math.pow(1.0f - t, 2) * a.x() + 2.0f * t * (1.0f - t) * b.x() + (float)Math.pow(t, 2) * c.x(),
                (float)Math.pow(1.0f - t, 2) * a.y() + 2.0f * t * (1.0f - t) * b.y() + (float)Math.pow(t, 2) * c.y());
    }

    private static float testCross(Vector2fc a, Vector2fc b, Vector2fc p) {
        return Math.signum((b.y() - a.y()) * (p.x() - a.x()) - (b.x() - a.x()) * (p.y() - a.y()));
    }

    // Determine which side we're on (using barycentric parameterization)
    private static float signBezier(Vector2fc A, Vector2fc B, Vector2fc C, Vector2fc p) {
        Vector2f a = C.sub(A, new Vector2f()), b = B.sub(A, new Vector2f()), c = p.sub(A, new Vector2f());
        Vector2f bary = new Vector2f(c.x*b.y-b.x*c.y,a.x*c.y-c.x*a.y).mul(1.0f / (a.x*b.y-b.x*a.y));
        Vector2f d = new Vector2f(bary.y * 0.5f, 0.0f).add(new Vector2f(1.0f - bary.x - bary.y));
        return mix(Math.signum(d.x * d.x - d.y), mix(-1.0f, 1.0f,
                step(testCross(A, B, p) * testCross(B, C, p), 0.0f)),
                step((d.x - d.y), 0.0f)) * testCross(A, C, B);
    }

    // Solve cubic equation for roots
    private static Vector3fc solveCubic(float a, float b, float c) {
        float p = b - a*a / 3.0f, p3 = p*p*p;
        float q = a * (2.0f*a*a - 9.0f*b) / 27.0f + c;
        float d = q*q + 4.0f*p3 / 27.0f;
        float offset = -a / 3.0f;
        if(d >= 0.0f) {
            float z = (float)Math.sqrt(d);
            Vector2f x = new Vector2f(z, -z).sub(q, q).mul(1.0f / 2.0f);
            Vector2f uv = new Vector2f(Math.signum(x.x()) * (float)Math.pow(Math.abs(x.x()), 1.0f/3.0f), Math.signum(x.y()) * (float)Math.pow(Math.abs(x.y()), 1.0f/3.0f));
            return new Vector3f(offset + uv.x + uv.y);
        }
        float v = (float)Math.acos(-(float)Math.sqrt(-27.0f / p3) * q / 2.0f) / 3.0f;
        float m = (float)Math.cos(v), n = (float)Math.sin(v)*1.732050808f;
        return new Vector3f(m + m, -n - m, n - m).mul((float)Math.sqrt(-p / 3.0f)).add(offset, offset, offset);
    }

    // Find the signed distance from a point to a bezier curve
    private static float sdBezier(Vector2fc A, Vector2fc B, Vector2fc C, Vector2fc p) {
        Vector2f BB = new Vector2f(mix(B.x() + 1e-4f, B.x(), Math.abs(Math.signum(B.x() * 2.0f - A.x() - C.x()))), mix(B.y() + 1e-4f, B.y(), Math.abs(Math.signum(B.y() * 2.0f - A.y() - C.y()))));
        Vector2f a = BB.sub(A, new Vector2f()), b = A.sub(BB.mul(2.0f, new Vector2f()), new Vector2f()).add(C), c = a.mul(2.0f, new Vector2f()), d = A.sub(p, new Vector2f());
        Vector3f k = new Vector3f(3.0f*a.dot(b),2.0f*a.dot(a)+d.dot(b),d.dot(a)).mul(1.0f / b.dot(b));
        Vector3f t = new Vector3f(solveCubic(k.x, k.y, k.z));
        t.x = clamp(t.x, 0.0f, 1.0f);
        t.y = clamp(t.y, 0.0f, 1.0f);
        t.z = clamp(t.z, 0.0f, 1.0f);
        Vector2f pos = c.add(b.mul(t.x, new Vector2f()), new Vector2f()).mul(t.x).add(A);
        float dis = pos.sub(p, new Vector2f()).length();
        pos = c.add(b.mul(t.y, new Vector2f()), new Vector2f()).mul(t.y).add(A);
        dis = Math.min(dis, pos.sub(p, new Vector2f()).length());
        pos = c.add(b.mul(t.z, new Vector2f()), new Vector2f()).mul(t.z).add(A);
        dis = Math.min(dis, pos.sub(p, new Vector2f()).length());
        return dis * -signBezier(A, BB, C, p);
    }

    private static float mix(float x, float y, float a) {
        return x * (1.0f - a) + y * a;
    }

    private static float clamp(float x, float min, float max) {
        return Math.min(max, Math.max(min, x));
    }

    private static float step(float edge, float x) {
        return (x < edge ? 0.0f : 1.0f);
    }
}
