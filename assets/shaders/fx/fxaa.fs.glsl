// Created by Chris Swenson
// Inspired by
//  https://github.com/McNopper/OpenGL/blob/master/Example42/shader/fxaa.frag.glsl
//  http://blog.simonrodriguez.fr/articles/30-07-2016_implementing_fxaa.html

#version 430 core

#include "..\common.glsl"

#define THRESHOLD       0.3
#define MUL_REDUCE      1.0 / 8.0
#define MIN_REDUCE      1.0 / 128.0
#define MAX_SPAN        8.0

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D diffuseTexture;

layout(std140) uniform Window {
    vec2 windowSize;
    vec2 texelSize;
} window;

void main() {
	vec3 diffuse = texture(diffuseTexture, vs_texCoord).rgb;

    float lumaTL = getLuma(textureOffset(diffuseTexture, vs_texCoord, ivec2(-1, 1)).rgb);
    float lumaTR = getLuma(textureOffset(diffuseTexture, vs_texCoord, ivec2(1, 1)).rgb);
    float lumaBL = getLuma(textureOffset(diffuseTexture, vs_texCoord, ivec2(-1, -1)).rgb);
    float lumaBR = getLuma(textureOffset(diffuseTexture, vs_texCoord, ivec2(1, -1)).rgb);
    float lumaC = getLuma(diffuse);

    float lumaMin = min(lumaC, min(min(lumaTL, lumaTR), min(lumaBL, lumaBR)));
    float lumaMax = max(lumaC, max(max(lumaTL, lumaTR), max(lumaBL, lumaBR)));

    if(lumaMax - lumaMin < lumaMax * THRESHOLD) {
        fs_color = vec4(diffuse, 1.0);
        return;
    }

    vec2 sampleDir = vec2(-((lumaTL + lumaTR) - (lumaBL + lumaBR)), ((lumaTL + lumaBL) - (lumaTR + lumaBR)));

    float sampleDirReduce = max((lumaTL + lumaTR + lumaBL + lumaBR) * 0.25 * MUL_REDUCE, MIN_REDUCE);

    float minSampleDirFactor = 1.0 / (min(abs(sampleDir.x), abs(sampleDir.y)) + sampleDirReduce);

    sampleDir = clamp(sampleDir * minSampleDirFactor, -vec2(MAX_SPAN, MAX_SPAN), vec2(MAX_SPAN, MAX_SPAN)) * window.texelSize;

    vec3 colorSampleNeg = texture(diffuseTexture, vs_texCoord + sampleDir * (1.0 / 3.0 - 0.5)).rgb;
    vec3 colorSamplePos = texture(diffuseTexture, vs_texCoord + sampleDir * (2.0 / 3.0 - 0.5)).rgb;
    vec3 colorTwoTab = (colorSamplePos + colorSampleNeg) * 0.5;

    vec3 colorSampleNegOuter = texture(diffuseTexture, vs_texCoord + sampleDir * -0.5).rgb;
    vec3 colorSamplePosOuter = texture(diffuseTexture, vs_texCoord + sampleDir * 0.5).rgb;
    vec3 colorFourTab = (colorSamplePosOuter + colorSampleNegOuter) * 0.25 + colorTwoTab * 0.5;

    float lumaFourTab = getLuma(colorFourTab);

    if(lumaFourTab < lumaMin || lumaFourTab > lumaMax) {
        fs_color = vec4(colorTwoTab, 1.0);
    }
    else {
        fs_color = vec4(colorFourTab, 1.0);
    }
}