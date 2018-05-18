#ifndef COMMON_WATER_GLSL
#define COMMON_WATER_GLSL

#define WATER_LEVEL     128.0 + 16.0

vec2 getDistortedTexCoord(sampler2D dudv, vec2 texCoord, float freq0, float freq1, float amp, float delta) {
    float time = delta * 2.0;
    vec2 d0 = texture(dudv, (texCoord + vec2(time, 0.0)) * freq0).rg * amp;
    vec2 d1 = (texCoord + d0 + vec2(0.0, time)) * freq1;
    return (texture(dudv, d1).rg * 2.0 - 1.0) * amp;
}

#endif