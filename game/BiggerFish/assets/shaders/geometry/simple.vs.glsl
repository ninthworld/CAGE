#version 430 core

layout(location=0) in vec3 in_position;

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
    gl_Position = camera.projMatrix * camera.viewMatrix * entity.worldMatrix * vec4(in_position, 1.0);
}