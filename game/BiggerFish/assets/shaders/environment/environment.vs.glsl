#version 430 core

#include "..\common.glsl"
#include "..\terrain\common.terrain.glsl"

#define MAX_INSTANCES 	100
#define SQUARED			64
#define SPACING         2.0f

layout(location=0) in vec3 in_position;
layout(location=1) in vec2 in_texCoord;
layout(location=2) in vec3 in_normal;

out vec3 vs_normal;
out vec2 vs_texCoord;

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

uniform sampler2D terrainHeightTexture;
uniform sampler2D terrainNormalTexture;
uniform sampler2D noiseTexture;

#define GRASS_SCALE 1.0

void main() {
	int id = gl_InstanceID;

	mat4 camView = inverse(camera.viewMatrix);
    vec3 camPosition = (vec3(camView[3]) / camView[3].w) / SPACING;

    vec2 relPos = vec2(float(id / SQUARED), float(id % SQUARED)) - vec2(SQUARED, SQUARED) / 2.0;
	vec2 instancePos = (vec2(floor(camPosition.x), floor(camPosition.z)) + relPos) * SPACING;

	vec3 noise = texture(noiseTexture, instancePos / 128.0).rgb;
	instancePos += noise.xy * 16.0;
		
	float distScale = length(relPos) / (SQUARED / 2.0);
	distScale = clamp(1.0 - pow(distScale, 4.0), 0.0, 1.0);

    vec3 translate = vec3(instancePos.x, 0.0, instancePos.y);
    float scale = GRASS_SCALE * distScale;

	vec2 mapCoord = (translate.xz + (WORLD_SCALE / 2.0)) / WORLD_SCALE;
	translate.y += texture(terrainHeightTexture, mapCoord).r * WORLD_HEIGHT - (WORLD_HEIGHT / 2.0);

	vec3 normal = texture(terrainNormalTexture, mapCoord).rbg * 2.0 - 1.0;

	if(dot(normal, vec3(0.0, 1.0, 0.0)) < 0.75) {
    	translate.y = -1000.0;
	}

    float angle = noise.z * PI * 2.0;
    mat4 transform = mat4(
        scale * cos(angle), 0.0, sin(angle), 0.0,
        0.0, scale, 0.0, 0.0,
        -sin(angle), 0.0, scale * cos(angle), 0.0,
        translate, 1.0);

    vec3 distort = vec3(sin(skybox.time * 0.4) * 0.25, 0.0, cos(skybox.time * 0.8) * 0.25);
    vec3 position = in_position + distort * in_position.y;

    gl_Position = transform * vec4(position, 1.0);

    vs_normal = normalize(vec3(vec4(in_normal, 1.0)));
    vs_texCoord = in_texCoord;
}