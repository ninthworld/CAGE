#version 430 core

layout(location=0) in vec2 in_position;

out vec2 vs_texCoord;
out vec4 vs_projTexCoord;
out vec3 vs_position;

layout(std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

layout(std140) uniform Entity {
    mat4 worldMatrix;
} entity;

void main() {
    gl_Position = entity.worldMatrix * vec4(in_position.x, 0.0, in_position.y, 1.0);
    gl_Position.y = entity.worldMatrix[3][1];

    vs_texCoord = gl_Position.xz + (entity.worldMatrix[0][0] / 2.0);
    vs_position = gl_Position.xyz;
    gl_Position = camera.projMatrix * camera.viewMatrix * gl_Position;
    vs_projTexCoord = gl_Position;
}