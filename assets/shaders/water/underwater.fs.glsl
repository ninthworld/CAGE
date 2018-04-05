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
        color = mix(color, mix(vec3(0.0, 0.1, 0.2), vec3(0.0, 0.5, 0.7), clamp((1.0 + position.y / WATER_LEVEL) / 2.0, 0.0, 1.0)), clamp(dist / 500.0, 0.0, 1.0));

        if(depth < 1.0) {
            float normalMul = clamp(dot(normal, -skybox.sunPosition.xyz), 0.0, 1.0);
            float radiusMul = clamp(1.0 - dist / (position.y * 2.0), 0.0, 1.0);
            float depthMul = clamp(position.y / WATER_LEVEL, 0.0, 1.0);

            float caOffset = 0.2;
            vec2 distortion[3];
            distortion[0] = getDistortedTexCoord(dudvTexture, position.xz + vec2(caOffset, 0.0), 0.03, 0.006, 1.0, skybox.time);
            distortion[1] = getDistortedTexCoord(dudvTexture, position.xz + vec2(0.0, caOffset), 0.03, 0.006, 1.0, skybox.time);
            distortion[2] = getDistortedTexCoord(dudvTexture, position.xz + vec2(caOffset, caOffset), 0.03, 0.006, 1.0, skybox.time);

            float value[3];
            for(int i=0; i<3; ++i) {
                value[i] = (distortion[i].x + distortion[i].y) / 2.0;
                value[i] = clamp(abs(value[i]), 0.0, 1.0);
                value[i] = pow(1.0 - value[i], 16.0) * 0.2;
            }


            color += vec3(value[0], value[1], value[2]) * normalMul * radiusMul * depthMul;
        }
    }

	fs_color = vec4(color, 1.0);
}