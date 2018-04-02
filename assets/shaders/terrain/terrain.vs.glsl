#version 430 core

#define WORLD_SCALE 2048.0
#define WORLD_HEIGHT 256.0

layout(location=0) in vec2 in_position;

out vec2 vs_texCoord;
out vec2 vs_texCoordNorm;

layout(std140) uniform Entity {
    mat4 worldMatrix;
} entity;

uniform sampler2D heightmapTexture;

void main() {
    gl_Position = entity.worldMatrix * vec4(in_position.x, 0.0, in_position.y, 1.0);
    vs_texCoord = gl_Position.xz + (WORLD_SCALE / 2.0);
    vs_texCoordNorm = vs_texCoord / WORLD_SCALE;
    gl_Position.y = texture(heightmapTexture, vs_texCoordNorm).r * WORLD_HEIGHT - (WORLD_HEIGHT / 2.0);
}