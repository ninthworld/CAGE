#version 430 core

#define MAX_LIGHTS 3

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

#define AMBIENT_TYPE        0.0
#define POINT_TYPE          1.0
#define DIRECTIONAL_TYPE    2.0
struct Light_t {
	vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
    vec4 worldPosition;
    float type;
    float range;
    float attLinear;
    float attQuadratic;
};

//layout(std140) uniform Light {
//     Light_t light[MAX_LIGHTS];
//};
layout(std140) buffer Light {
    Light_t lights[];
};

layout(std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

#define SUN_POS vec3(0.0, 1.0, 0.0)
vec3 atmosphere(vec3 r, vec3 r0, vec3 pSun, float iSun, float rPlanet, float rAtmos, vec3 kRlh, float kMie, float shRlh, float shMie, float g);

vec3 worldPosFromDepth(float depth) {
	vec4 ssPos = camera.invProjMatrix * vec4(vs_texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
	vec4 wsPos = camera.invViewMatrix * ssPos;
	return wsPos.xyz / wsPos.w;
}

void main() {
	vec3 diffuse = texture(diffuseTexture, vs_texCoord).rgb;
	vec3 specular = texture(specularTexture, vs_texCoord).rgb;
	float power = texture(specularTexture, vs_texCoord).a * 128.0;
	vec3 normal = texture(normalTexture, vs_texCoord).rgb * 2.0 - 1.0;
	float depth = texture(depthTexture, vs_texCoord).r;
	vec3 position = worldPosFromDepth(depth);
	vec3 camPosition = vec3(camera.viewMatrix * vec4(0.0, 0.0, 0.0, 1.0));

	vec3 color = vec3(0.0, 0.0, 0.0);
	if(depth < 1.0) {
		for(int i = 0; i < lights.length(); ++i) {
		    Light_t light = lights[i];
            if(light.type == AMBIENT_TYPE) {
                // Ambient
                color += diffuse * light.ambientColor.rgb;
            }
            else {
                vec3 relLightDir = vec3(0.0, 0.0, 0.0);
                if(light.type == POINT_TYPE) {
                    relLightDir = -normalize(position - light.worldPosition.xyz);
                }
                else if(light.type == DIRECTIONAL_TYPE) {
                    relLightDir = normalize(light.worldPosition.xyz);
                }

                // Diffuse
                float dist = distance(light.worldPosition.xyz, position);
                float cosTheta = dot(normal, relLightDir);
                float att = 1.0;
                if(light.range > 0.0 && (light.attLinear > 0.0 || light.attQuadratic > 0.0)) {
                    att = clamp(1.0 - dist/light.range, 0.0, 1.0);
                    if(light.attQuadratic > 0.0) {
                        att *= att;
                    }
                }
                color += clamp(diffuse * light.diffuseColor.rgb * cosTheta * att, 0.0, 1.0);

                // Specular
                vec3 vertexToCam = normalize(camPosition - position);
                vec3 lightReflect = normalize(reflect(relLightDir, normal));
                float factor = dot(vertexToCam, lightReflect);
                if(factor > 0.0) {
                factor = pow(factor, power);
                    color += clamp(att * specular * light.specularColor.rgb * factor, 0.0, 1.0);
                }
            }
		}
	}
	else {
		color = atmosphere(
		    -normalize(camPosition - position),
            vec3(0, 6372e3, 0),
            SUN_POS, 22.0,
            6371e3, 6471e3,
            vec3(5.5e-6, 13.0e-6, 22.4e-6),
            21e-6, 8e3, 1.2e3, 0.758);
	}

	fs_color = vec4(color, 1.0);
}

/* GLSL-Atmosphere by wwwtyro
 * Source: https://github.com/wwwtyro/glsl-atmosphere
 * Licensed under The Unlicense
 */

#define PI 3.141592
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