#version 400 core

#define PI 3.141592

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

vec3 atmosphere(vec3 r, vec3 r0, vec3 pSun, float iSun, float rPlanet, float rAtmos, vec3 kRlh, float kMie, float shRlh, float shMie, float g);

void main() {

    float waterLevel = vs_position.y;
    float delta = skybox.time * 2.0;

    vec2 distortedTexCoord = texture(dudvTexture, vec2(vs_texCoord.x + delta, vs_texCoord.y) * 0.01).rg * 0.5;
    distortedTexCoord = (vs_texCoord + vec2(distortedTexCoord.x, distortedTexCoord.y + delta)) * 0.01;
    vec2 distortion = (texture(dudvTexture, distortedTexCoord).rg * 2.0 - 1.0) * 0.1;

    vec2 projTexCoord = (vs_projTexCoord.xy / vs_projTexCoord.w) / 2.0 + 0.5;
    projTexCoord += distortion;
    projTexCoord = clamp(projTexCoord, 0.001, 0.999);

    mat4 camView = inverse(camera.viewMatrix);
    vec3 camPosition = vec3(camView[3]) / camView[3].w;

    vec3 newCamPosition = vec3(camPosition.x, (2 * waterLevel - camPosition.y), camPosition.z) + vec3(distortion.x, 0.0, distortion.y) * 16.0;

    vec3 refractColor = texture(refractTexture, projTexCoord).rgb;
    vec3 reflectColor = vec3(0.0, 0.0, 0.0);

    vec3 direction = vec3(0.0, 0.0, 0.0);
    if(newCamPosition.y < waterLevel) {
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

    if(camPosition.y < waterLevel) {
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

    if(camPosition.y < waterLevel) {
        color = mix(color, mix(vec3(0.0, 0.1, 0.2), vec3(0.0, 0.5, 0.7), clamp((1.0 + vs_position.y / waterLevel) / 2.0, 0.0, 1.0)), clamp(distance(vs_position, camPosition) / 500.0, 0.0, 1.0));
    }

    fs_color = vec4(color, 1.0);
}

/**
 * GLSL-Atmosphere by wwwtyro
 * Source: https://github.com/wwwtyro/glsl-atmosphere
 * Licensed under The Unlicense
 */

#define iSteps 16
#define jSteps 8

vec2 rsi(vec3 r0, vec3 rd, float sr) {
    float a = dot(rd, rd); float b = 2.0 * dot(rd, r0);
    float c = dot(r0, r0) - (sr * sr); float d = (b*b) - 4.0*a*c;
    if (d < 0.0) return vec2(1e5,-1e5);
    return vec2((-b - sqrt(d))/(2.0*a), (-b + sqrt(d))/(2.0*a));
}

vec3 atmosphere(vec3 r, vec3 r0, vec3 pSun, float iSun, float rPlanet, float rAtmos, vec3 kRlh, float kMie, float shRlh, float shMie, float g) {
    pSun = normalize(pSun); r = normalize(r); vec2 p = rsi(r0, r, rAtmos);
    if (p.x > p.y) return vec3(0,0,0);
    p.y = min(p.y, rsi(r0, r, rPlanet).x);
    float iStepSize = (p.y - p.x) / float(iSteps); float iTime = 0.0;
    vec3 totalRlh = vec3(0,0,0); vec3 totalMie = vec3(0,0,0); float iOdRlh = 0.0; float iOdMie = 0.0;
    float mu = dot(r, pSun); float mumu = mu * mu; float gg = g * g;
    float pRlh = 3.0 / (16.0 * PI) * (1.0 + mumu);
    float pMie = 3.0 / (8.0 * PI) * ((1.0 - gg) * (mumu + 1.0)) / (pow(1.0 + gg - 2.0 * mu * g, 1.5) * (2.0 + gg));
    for (int i = 0; i < iSteps; i++) {
        vec3 iPos = r0 + r * (iTime + iStepSize * 0.5); float iHeight = length(iPos) - rPlanet;
        float odStepRlh = exp(-iHeight / shRlh) * iStepSize; float odStepMie = exp(-iHeight / shMie) * iStepSize;
        iOdRlh += odStepRlh; iOdMie += odStepMie;
        float jStepSize = rsi(iPos, pSun, rAtmos).y / float(jSteps);
        float jTime = 0.0; float jOdRlh = 0.0; float jOdMie = 0.0;
        for (int j = 0; j < jSteps; j++) {
            vec3 jPos = iPos + pSun * (jTime + jStepSize * 0.5);
            float jHeight = length(jPos) - rPlanet;
            jOdRlh += exp(-jHeight / shRlh) * jStepSize; jOdMie += exp(-jHeight / shMie) * jStepSize;
            jTime += jStepSize;
        }
        vec3 attn = exp(-(kMie * (iOdMie + jOdMie) + kRlh * (iOdRlh + jOdRlh)));
        totalRlh += odStepRlh * attn; totalMie += odStepMie * attn; iTime += iStepSize;
    }
    return iSun * (pRlh * kRlh * totalRlh + pMie * kMie * totalMie);
}