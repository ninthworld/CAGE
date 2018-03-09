#version 430 core

in vec3 vs_normal;
in vec3 vs_tangent;
in vec3 vs_bitangent;
in vec2 vs_texCoord;

layout(location=0) out vec4 fs_ambient;
layout(location=1) out vec4 fs_diffuse;
layout(location=2) out vec4 fs_specular;
layout(location=3) out vec4 fs_normal;

layout(binding=2, std140) uniform Material {
    float useDiffuseTexture;
    float useSpecularTexture;
    float useNormalTexture;
    float specularExp;
    vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
} material;

layout(binding=3) uniform sampler2D diffuseTexture;
layout(binding=4) uniform sampler2D specularTexture;
layout(binding=5) uniform sampler2D highlightTexture;
layout(binding=6) uniform sampler2D normalTexture;

void main() {
	
    fs_ambient = vec4(material.ambientColor.rgb, 1.0);

    if(material.useDiffuseTexture > 0.0) {
        fs_diffuse = vec4(texture(diffuseTexture, vs_texCoord).rgb, 1.0);
    }
    else {
        fs_diffuse = vec4(material.diffuseColor.rgb, 1.0);
    }

    if(material.useSpecularTexture > 0.0) {
        fs_specular = vec4(texture(specularTexture, vs_texCoord).rgb, texture(highlightTexture, vs_texCoord).r);
    }
    else {
        fs_specular = vec4(material.specularColor.rgb, material.specularExp);
    }

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