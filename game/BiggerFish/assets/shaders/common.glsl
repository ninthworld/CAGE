#ifndef COMMON_GLSL
#define COMMON_GLSL

#define PI              3.14159265359

float getLinearDepth(float depth) {
    const float near = 0.1;
    const float far = 1000.0;
    return (2.0 * near) / (far + near - depth * (far - near));
}

vec3 getPositionFromDepth(float depth, vec2 texCoord, mat4 invProj, mat4 invView) {
	vec4 ssPos = invProj * vec4(texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
	vec4 wsPos = invView * ssPos;
	return wsPos.xyz / wsPos.w;
}

float getLuma(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}

// Code from https://gamedev.stackexchange.com/questions/59797/glsl-shader-change-hue-saturation-brightness
// Created by sam hocevar (https://gamedev.stackexchange.com/users/5864/sam-hocevar)

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

#endif