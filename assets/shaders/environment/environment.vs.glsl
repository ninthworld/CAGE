#version 430 core

#include "..\noise.glsl"
#include "..\terrain\common.terrain.glsl"

#define MAX_INSTANCES 	100
#define SQUARED			32

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

uniform sampler2D terrainHeightTexture;
uniform sampler2D splatTexture;

#define GRASS_SCALE 0.5

void main() {
	int id = gl_InstanceID;

	mat4 camView = inverse(camera.viewMatrix);
    vec3 camPosition = (vec3(camView[3]) / camView[3].w) / 4.0f;
    
    vec2 relPos = vec2(float(id / SQUARED), float(id % SQUARED)) - vec2(SQUARED, SQUARED) / 2.0;
	vec2 instancePos = (vec2(floor(camPosition.x), floor(camPosition.z)) + relPos) * 4.0f;
	instancePos += vec2(cnoise((instancePos + (WORLD_SCALE / 2.0)) / WORLD_SCALE), 0.0) * 10.0;
		
	float distScale = length(relPos) / (SQUARED / 2.0);
	distScale = clamp(1.0 - pow(distScale, 4.0), 0.0, 1.0);
	
    gl_Position = vec4((vec3(instancePos.x, 0.0, instancePos.y) + in_position * GRASS_SCALE * distScale), 1.0);
	vec2 mapCoord = (gl_Position.xz + (WORLD_SCALE / 2.0)) / WORLD_SCALE;
	gl_Position.y += texture(terrainHeightTexture, mapCoord).r * WORLD_HEIGHT - (WORLD_HEIGHT / 2.0);
	
	vec3 splat = texture(splatTexture, mapCoord).rgb;
	
	if(splat.r == 0.0) {	
    	gl_Position.y = -1000.0;
	}
	
    vs_normal = normalize(vec3(vec4(in_normal, 1.0)));
    vs_texCoord = in_texCoord;
}