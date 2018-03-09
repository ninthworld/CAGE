#version 430 core

layout(location=0) in vec3 in_position;
layout(location=1) in vec2 in_texCoord;
layout(location=2) in vec3 in_normal;
layout(location=3) in vec3 in_tangent;

out vec3 vs_normal;
out vec3 vs_tangent;
out vec3 vs_bitangent;
out vec2 vs_texCoord;

layout(binding=0, std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

layout(binding=1, std140) uniform Model {
    mat4 worldMatrix;
} model;

void main() {
	mat4 worldRotationMatrix = model.worldMatrix;
	worldRotationMatrix[3] = vec4(0.0, 0.0, 0.0, 1.0);
	
    vs_normal = normalize(vec3(worldRotationMatrix * vec4(in_normal, 1.0)));
    vs_tangent = normalize(vec3(worldRotationMatrix * vec4(in_tangent, 1.0)));
    vs_bitangent = normalize(cross(in_normal, in_tangent));
    vs_texCoord = in_texCoord;
    gl_Position = camera.projMatrix * camera.viewMatrix * model.worldMatrix * vec4(in_position, 1.0);
}