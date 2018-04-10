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

#endif