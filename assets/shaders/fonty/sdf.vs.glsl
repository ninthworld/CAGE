#version 430 core

layout(location=0) in vec2 in_position;

out vec2 vs_texCoord;

layout(std140) uniform Window {
    vec2 windowSize;
    vec2 texelSize;
} window;

layout(std140) uniform Transform {
    vec2 translate;
    float scale;
    float sdf;
} transform;

void main() {
    vs_texCoord = (in_position + 1.0) / 2.0;
    gl_Position = vec4(vec2(-1.0, 1.0) + 2.0 * vec2(1.0, -1.0) * (transform.translate + transform.scale * vs_texCoord) * window.texelSize, 0.0, 1.0);
}