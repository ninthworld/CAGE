#version 430 core

#include "..\common.glsl"
#include "common.shadow.glsl"

#define SHADOW_MAP_SIZE	2048.0
#define MAX_SHADOWS     4
#define MAX_SOURCES     100.0
#define SHADOW_DIST0    8.0
#define SHADOW_DIST1    16.0
#define SHADOW_DIST2    32.0
#define SHADOW_DIST3    64.0

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D depthTexture;
uniform sampler2D shadowTexture[MAX_SHADOWS];

layout(std140) uniform Shadow {
    mat4 viewProjMatrix[MAX_SHADOWS];
} shadow;

layout(std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

void main() {
	float depth = texture(depthTexture, vs_texCoord).r;
    vec3 position = getPositionFromDepth(depth, vs_texCoord, camera.invProjMatrix, camera.invViewMatrix);
	mat4 camView = inverse(camera.viewMatrix);
	vec3 camPosition = vec3(camView[3]) / camView[3].w;
	float camDistance = distance(position, camPosition);

    float visibility = 1.0;
	if(depth < 1.0) {
        float sampleOffset = 0.0005;
        if (camDistance < SHADOW_DIST0) {
            vec4 shadowCoord = BIAS_MATRIX * shadow.viewProjMatrix[0] * vec4(position, 1.0);
            visibility = PCF(shadowTexture[0], vec2(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE), shadowCoord.xy, shadowCoord.z - 0.005);
        }
        else if (camDistance < SHADOW_DIST1) {
            vec4 shadowCoord = BIAS_MATRIX * shadow.viewProjMatrix[1] * vec4(position, 1.0);
            visibility = PCF(shadowTexture[1], vec2(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE), shadowCoord.xy, shadowCoord.z - 0.005);
        }
        else if (camDistance < SHADOW_DIST2) {
            vec4 shadowCoord = BIAS_MATRIX * shadow.viewProjMatrix[2] * vec4(position, 1.0);
            visibility = PCF(shadowTexture[2], vec2(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE), shadowCoord.xy, shadowCoord.z - 0.005);
        }
        else if (camDistance < SHADOW_DIST3) {
            vec4 shadowCoord = BIAS_MATRIX * shadow.viewProjMatrix[3] * vec4(position, 1.0);
            visibility = PCF(shadowTexture[3], vec2(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE), shadowCoord.xy, shadowCoord.z - 0.005);
        }
	}
	
    fs_color = vec4(visibility, visibility, visibility, 1.0);  

	fs_color /= MAX_SOURCES;
}