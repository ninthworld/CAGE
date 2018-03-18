#version 400 core

in vec3 vs_normal;
in vec3 vs_tangent;
in vec3 vs_bitangent;
in vec2 vs_texCoord;

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

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D highlightTexture;
uniform sampler2D emissiveTexture;
uniform sampler2D normalTexture;

void main() {

    // TODO: Implement Emissive Shading

    fs_diffuse = vec4(1.0, 1.0, 1.0, 1.0);
    if(material.useDiffuseTexture > 0.0) {
        fs_diffuse = vec4(texture(diffuseTexture, vs_texCoord).rgb, 1.0);
    }
    fs_diffuse *= material.diffuseColor;

    fs_specular = vec4(1.0, 1.0, 1.0, 1.0);
    if(material.useSpecularTexture > 0.0) {
        fs_specular = vec4(texture(specularTexture, vs_texCoord).rgb, 1.0);
    }
    fs_specular *= material.specularColor;

    if(material.useHighlightTexture > 0.0) {
        fs_specular.a = texture(highlightTexture, vs_texCoord).r / 128.0;
    }
    fs_specular *= material.shininess / 128.0;

    if(material.useNormalTexture > 0.0) {
        vec3 normal = texture(normalTexture, vs_texCoord).rgb * 2.0 - 1.0;
        mat3 TBN = mat3(vs_tangent, vs_bitangent, vs_normal);
        normal = normal * TBN;
        fs_normal = vec4((normal + 1.0) / 2.0, 1.0);
    }
    else {
        fs_normal = vec4((vs_normal + 1.0) / 2.0, 1.0);
    }
}