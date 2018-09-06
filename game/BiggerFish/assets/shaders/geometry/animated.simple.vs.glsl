#version 430 core

#define MAX_WEIGHTS 3

layout(location=0) in vec3 in_position;
layout(location=1) in vec2 in_texCoord;
layout(location=2) in vec3 in_normal;
layout(location=3) in vec3 in_weights;
layout(location=4) in vec3 in_jointIndices;

layout(std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

layout(std140) uniform Entity {
 mat4 worldMatrix;
} entity;

layout(std140) buffer Bone {
 mat4 jointTransforms[];
};

void main() {
    vec4 position = vec4(0.0, 0.0, 0.0, 0.0);

    for(int i=0; i<MAX_WEIGHTS; ++i) {
        int index = int(in_jointIndices[i]);
        mat4 jointTransform = jointTransforms[index];
        vec4 pos = jointTransform * vec4(in_position, 1.0);
        position += pos * in_weights[i];
    }

    gl_Position = camera.projMatrix * camera.viewMatrix * entity.worldMatrix * position;
}