#version 400 core
#extension GL_NV_shadow_samplers_cube : enable

#include "..\atmosphere.glsl"
#include "common.water.glsl"

in vec2 vs_texCoord;
in vec4 vs_projTexCoord;
in vec3 vs_position;

layout(location=0) out vec4 fs_color;

uniform sampler2D dudvTexture;
uniform sampler2D refractTexture;
uniform sampler2D skydomeTexture;
uniform samplerCube skyboxTexture;

layout(std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

layout(std140) uniform Skybox {
    vec4 skyColor;
    vec4 sunPosition;
    float useSkybox;
    float useSkydome;
    float useAtmosphere;
    float time;
} skybox;

void main() {

    vec2 distortion = getDistortedTexCoord(dudvTexture, vs_texCoord, 0.01, 0.008, 0.1, skybox.time);

    vec2 projTexCoord = (vs_projTexCoord.xy / vs_projTexCoord.w) / 2.0 + 0.5;
    projTexCoord += distortion;
    projTexCoord = clamp(projTexCoord, 0.001, 0.999);

    mat4 camView = inverse(camera.viewMatrix);
    vec3 camPosition = vec3(camView[3]) / camView[3].w;

    vec3 newCamPosition = vec3(camPosition.x, (2 * WATER_LEVEL - camPosition.y), camPosition.z) + vec3(distortion.x, 0.0, distortion.y) * 16.0;

    vec3 refractColor = texture(refractTexture, projTexCoord).rgb;
    vec3 reflectColor = vec3(0.0, 0.0, 0.0);

    vec3 direction = vec3(0.0, 0.0, 0.0);
    if(newCamPosition.y < WATER_LEVEL) {
        direction = -normalize(newCamPosition - vs_position);
        reflectColor += vec3(0.0, 0.1, 0.3);
    }
    else {
        direction = -normalize(camPosition - vs_position);
    }

    if(skybox.useSkybox > 0.0) {
        reflectColor = textureCube(skyboxTexture, direction).rgb;
    }
    else if(skybox.useSkydome > 0.0) {
        float angle = atan(direction.z, direction.x);
        vec2 uv = vec2((angle + PI) / (2.0 * PI), 1.0 - direction.y);
        reflectColor = texture(skydomeTexture, uv).rgb;
    }
    else if(skybox.useAtmosphere > 0.0) {
        reflectColor = atmosphere(
            direction,
            vec3(0, 6372e3, 0),
            -skybox.sunPosition.xyz, 22.0,
            6371e3, 6471e3,
            vec3(5.5e-6, 13.0e-6, 22.4e-6),
            21e-6, 8e3, 1.2e3, 0.758);
    }
    else {
        reflectColor = skybox.skyColor.rgb;
    }

    if(camPosition.y < WATER_LEVEL) {
        refractColor = reflectColor;
    }

    vec3 lightDir = -normalize(skybox.sunPosition.xyz);
    vec3 vertexToCam = -normalize(camPosition - vs_position);

    float fresnelFactor = clamp(dot(-vertexToCam, vec3(0.0, 1.0, 0.0)), 0.0, 1.0);

    vec3 color = mix(reflectColor, refractColor, fresnelFactor);

    vec3 normal = normalize(vec3(distortion.x, 1.0, distortion.y));
    float cosTheta = dot(normal, lightDir);
    color *= clamp(cosTheta, 0.3, 1.0);

    vec3 lightReflect = normalize(reflect(lightDir, normal));
    float factor = dot(vertexToCam, lightReflect);
    if(factor > 0.0) {
        factor = pow(factor, 64.0);
        color += factor * 0.25;
    }

    if(camPosition.y < WATER_LEVEL) {
        color = mix(color, mix(vec3(0.0, 0.1, 0.2), vec3(0.0, 0.5, 0.7), clamp((1.0 + vs_position.y / WATER_LEVEL) / 2.0, 0.0, 1.0)), clamp(distance(vs_position, camPosition) / 500.0, 0.0, 1.0));
    }

    fs_color = vec4(color, 1.0);
}