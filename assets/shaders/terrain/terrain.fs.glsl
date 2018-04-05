#version 400 core

#include "..\tiling.glsl"

in vec2 gs_texCoord;
in vec2 gs_texCoordNorm;
in vec3 gs_tangent;

layout(location=0) out vec4 fs_diffuse;
layout(location=1) out vec4 fs_specular;
layout(location=2) out vec4 fs_normal;

layout(std140) uniform Material {
    float useDiffuseTexture;
    float useSpecularTexture;
    float useHighlightTexture;
    float useEmissiveTexture;
    float useNormalTexture;
    float shininess;
    vec4 diffuseColor;
    vec4 specularColor;
    vec4 emissiveColor;
} material;

uniform sampler2D normalmapTexture;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D highlightTexture;
uniform sampler2D emissiveTexture;
uniform sampler2D normalTexture;

void main() {
    fs_diffuse = vec4(1.0, 1.0, 1.0, 1.0);
    if(material.useDiffuseTexture > 0.0) {
        fs_diffuse = textureNoTile(diffuseTexture, gs_texCoord);
    }
    fs_diffuse.rgb *= material.diffuseColor.rgb;

    fs_specular = vec4(1.0, 1.0, 1.0, 1.0);
    if(material.useSpecularTexture > 0.0) {
        fs_specular.r = textureNoTile(specularTexture, gs_texCoord).r;
    }
    fs_specular.r *= material.specularColor.r;

    if(material.useHighlightTexture > 0.0) {
        fs_specular.g = textureNoTile(highlightTexture, gs_texCoord).r / 128.0;
    }
    else {
        fs_specular.g = material.shininess / 128.0;
    }

    vec3 normal = normalize(texture(normalmapTexture, gs_texCoordNorm).rbg * 2.0 - 1.0);
    normal.z *= -1.0;

    if(material.useNormalTexture > 0.0) {
        normal *= -1.0;
        vec3 norm = textureNoTile(normalTexture, gs_texCoord).rgb * 2.0 - 1.0;
        mat3 TBN = mat3(gs_tangent, normalize(cross(normal, gs_tangent)), normal);
        norm = norm * TBN;
        fs_normal = vec4((norm + 1.0) / 2.0, 1.0);
    }
    else {
        fs_normal = vec4((normal + 1.0) / 2.0, 1.0);
    }
}