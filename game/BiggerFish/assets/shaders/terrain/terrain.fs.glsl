#version 400 core

#include "..\tiling.glsl"

in vec3 gs_position;
in vec2 gs_texCoord;
in vec2 gs_texCoordNorm;
in vec3 gs_tangent;

layout(location=0) out vec4 fs_diffuse;
layout(location=1) out vec4 fs_specular;
layout(location=2) out vec4 fs_normal;

uniform sampler2D terrainNormalTexture;

uniform sampler2D diffuseTexture0;
uniform sampler2D specularTexture0;
uniform sampler2D normalTexture0;
uniform sampler2D diffuseTexture1;
uniform sampler2D specularTexture1;
uniform sampler2D normalTexture1;

void main() {
    vec3 normal = normalize(texture(terrainNormalTexture, gs_texCoordNorm).rbg * 2.0 - 1.0);
    normal.xy *= -1.0;

    vec3 blending = abs(vec3(pow(normal.x, 2.0), pow(normal.y, 2.0), pow(normal.z, 2.0)));
    blending = normalize(max(blending, 0.00001));
    float b = (blending.x + blending.y + blending.z);
    blending /= vec3(b, b, b);

    const float topScale = 0.25;
    const float sideScale = 0.1;

    // Diffuse
    vec3 diffuseY = texture(diffuseTexture0, gs_position.xz * topScale).rgb;
    vec3 diffuseZ = texture(diffuseTexture1, gs_position.xy * sideScale).rgb;
    vec3 diffuseX = texture(diffuseTexture1, gs_position.yz * sideScale).rgb;
    fs_diffuse = vec4(diffuseX * blending.x + diffuseY * blending.y + diffuseZ * blending.z, 1.0);

    // Specular
    float specularY = texture(specularTexture0, gs_position.xz * topScale).r;
    float specularZ = texture(specularTexture1, gs_position.xy * sideScale).r;
    float specularX = texture(specularTexture1, gs_position.yz * sideScale).r;
    fs_specular = vec4(specularX * blending.x + specularY * blending.y + specularZ * blending.z, 32.0 / 128.0, 1.0, 1.0);

    // Normal
    vec3 nY = texture(normalTexture0, gs_position.xz * topScale).rgb * 2.0 - 1.0;
    vec3 nZ = texture(normalTexture1, gs_position.xy * sideScale).rgb * 2.0 - 1.0;
    vec3 nX = texture(normalTexture1, gs_position.yz * sideScale).rgb * 2.0 - 1.0;

    vec3 norm = nX * blending.x + nY * blending.y + nZ * blending.z;

    mat3 TBN = mat3(gs_tangent, normalize(cross(normal, gs_tangent)), normal);
    norm = norm * TBN;
    fs_normal = vec4((norm + 1.0) / 2.0, 1.0);
}