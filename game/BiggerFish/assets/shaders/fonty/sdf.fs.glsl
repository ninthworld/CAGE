#version 430 core

in vec2 vs_texCoord;

out vec4 fs_color;

layout(std140) uniform Transform {
    vec2 translate;
    float scale;
    float sdf;
} transform;

uniform sampler2D u_texture;

float median(vec3 v) {
    return max(min(v.x, v.y), min(max(v.x, v.y), v.z));
}

#define COLOR vec3(1.0, 1.0, 1.0)

void main() {
    vec3 color = texture(u_texture, vs_texCoord).rgb;
    if(transform.sdf == 0.0) {
        fs_color = vec4(color, 1.0);
    }
    else {
        float dist = median(color);
        float alias = 0.05 * textureSize(u_texture, 0).x / transform.scale;
        float alpha = smoothstep(0.5 - alias, 0.5 + alias, dist);
        fs_color = vec4(COLOR, alpha);
    }
}