#version 430 core

layout(location=0) in vec3 in_position;
layout(location=1) in vec2 in_texCoord;
layout(location=2) in vec3 in_normal;

out vec3 vs_normal;
out vec2 vs_texCoord;

layout(std140) uniform Entity {
    mat4 worldMatrix;
} entity;

void main() {
	mat4 worldRotationMatrix = entity.worldMatrix;
	worldRotationMatrix[3] = vec4(0.0, 0.0, 0.0, 1.0);
	
    vs_normal = normalize(vec3(worldRotationMatrix * vec4(in_normal, 1.0)));
    vs_texCoord = in_texCoord;
    gl_Position = entity.worldMatrix * vec4(in_position, 1.0);
}