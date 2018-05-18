#version 430 core

#include "..\common.glsl"
#include "common.water.glsl"

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D dudvTexture;
uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

layout(std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

layout(std140) uniform Skybox {
    vec4 skyColor;
    vec4 sunPosition;
    float useSkybox;
    float useSkydome;
    float useAtmosphere;
    float time;
} skybox;

void main() {
	float depth = texture(depthTexture, vs_texCoord).r;
    vec3 position = getPositionFromDepth(depth, vs_texCoord, camera.invProjMatrix, camera.invViewMatrix);
    mat4 camView = inverse(camera.viewMatrix);
    vec3 camPosition = vec3(camView[3]) / camView[3].w;

	vec3 color = texture(diffuseTexture, vs_texCoord).rgb;
    vec3 normal = texture(normalTexture, vs_texCoord).rgb * 2.0 - 1.0;

    if(camPosition.y < WATER_LEVEL) {
        float dist = distance(position, camPosition);
        color = mix(color, mix(vec3(0.0, 0.1, 0.2), vec3(0.0, 0.5, 0.7), clamp((1.0 + position.y / WATER_LEVEL) / 2.0, 0.0, 1.0)), clamp(dist / 300.0, 0.0, 1.0));

        if(depth < 1.0) {
            float normalMul = clamp(dot(normal, -skybox.sunPosition.xyz), 0.0, 1.0);
            float radiusMul = 1.0 - clamp(dist / 256.0, 0.0, 1.0);
            float depthMul = clamp(position.y / WATER_LEVEL, 0.0, 1.0);

            vec2 distortion0 = getDistortedTexCoord(dudvTexture, position.xz, 0.03, 0.006, 1.0, skybox.time);
            float value0 = (distortion0.x + distortion0.y) / 2.0;
            value0 = clamp(abs(value0), 0.0, 1.0);
            value0 = pow(1.0 - value0, 16.0) * 0.08;

            vec2 distortion1 = getDistortedTexCoord(dudvTexture, position.xz + vec2(0.5, 0.5), 0.04, 0.004, 1.0, -skybox.time);
            float value1 = (distortion1.x + distortion1.y) / 2.0;
            value1 = clamp(abs(value1), 0.0, 1.0);
            value1 = pow(1.0 - value1, 16.0) * 0.08;

            float value = value0 + value1;

            color += vec3(value, value, value) * normalMul * radiusMul * depthMul;
        }
    }

	fs_color = vec4(color, 1.0);
}