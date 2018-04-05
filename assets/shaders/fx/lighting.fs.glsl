#version 430 core
#extension GL_NV_shadow_samplers_cube : enable

#include "..\common.glsl"
#include "..\atmosphere.glsl"

#define MAX_SOURCES 100.0

#define AMBIENT_TYPE        0.0
#define POINT_TYPE          1.0
#define DIRECTIONAL_TYPE    2.0

#define ATT_LINEAR          1.0
#define ATT_QUADRATIC       2.0

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

uniform sampler2D shadowTexture;
uniform sampler2D skydomeTexture;
uniform samplerCube skyboxTexture;

struct Light_t {
    vec4 diffuseColor;
    vec4 specularColor;
    vec4 worldPosition;
    float type;
    float range;
    float attenuation;
    float castShadow;
};

layout(std140) buffer Light {
    Light_t lights[];
};

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
	float depth = texture(depthTexture, vs_texCoord).r;
    vec3 position = getPositionFromDepth(depth, vs_texCoord, camera.invProjMatrix, camera.invViewMatrix);
    mat4 camView = inverse(camera.viewMatrix);
    vec3 camPosition = vec3(camView[3]) / camView[3].w;

    vec3 normal = texture(normalTexture, vs_texCoord).rgb * 2.0 - 1.0;

	vec3 color = vec3(0.0, 0.0, 0.0);
	if(depth < 1.0) {
	    vec3 diffuse = texture(diffuseTexture, vs_texCoord).rgb;
        float specular = texture(specularTexture, vs_texCoord).r;
        float power = texture(specularTexture, vs_texCoord).g * 128.0;

	    // Material Lighting
	    float numShadows = 0.0;
		for(int i = 0; i < lights.length(); ++i) {
		    Light_t light = lights[i];
            if(light.type == AMBIENT_TYPE) {
                // Ambient
                color += diffuse * light.diffuseColor.rgb;
            }
            else {
                numShadows += light.castShadow;
                vec3 relLightDir = vec3(0.0, 0.0, 0.0);
                if(light.type == POINT_TYPE) {
                    relLightDir = -normalize(position - light.worldPosition.xyz);
                }
                else if(light.type == DIRECTIONAL_TYPE) {
                    relLightDir = -normalize(light.worldPosition.xyz);
                }

                // Diffuse
                float dist = distance(light.worldPosition.xyz, position);
                float cosTheta = dot(normal, relLightDir);
                float att = 1.0;
                if(light.range > 0.0 && light.attenuation >= ATT_LINEAR) {
                    att = clamp(1.0 - dist/light.range, 0.0, 1.0);
                    if(light.attenuation == ATT_QUADRATIC) {
                        att *= att;
                    }
                }
                color += clamp(diffuse * light.diffuseColor.rgb * cosTheta * att, 0.0, 1.0);

                // Specular
                vec3 vertexToCam = -normalize(camPosition - position);
                vec3 lightReflect = normalize(reflect(relLightDir, normal));
                float factor = dot(vertexToCam, lightReflect);
                if(factor > 0.0) {
                	factor = pow(factor, power);
                    color += clamp(att * specular * light.specularColor.rgb * factor, 0.0, 1.0);
                }
            }
		}
		if(numShadows > 0.0) {
            float shadow = (texture(shadowTexture, vs_texCoord).r / numShadows) * MAX_SOURCES;
            color *= clamp(shadow, 0.3, 1.0);
        }
	}
	else {
	    vec3 direction = -normalize(camPosition - position);
	    if(skybox.useSkybox > 0.0) {
	        color = textureCube(skyboxTexture, direction).rgb;
	    }
	    else if(skybox.useSkydome > 0.0) {
            float angle = atan(direction.z, direction.x);
            vec2 uv = vec2((angle + PI) / (2.0 * PI), 1.0 - direction.y);
            color = texture(skydomeTexture, uv).rgb;
	    }
	    else if(skybox.useAtmosphere > 0.0) {
            color = atmosphere(
                direction,
                vec3(0, 6372e3, 0),
                -skybox.sunPosition.xyz, 22.0,
                6371e3, 6471e3,
                vec3(5.5e-6, 13.0e-6, 22.4e-6),
                21e-6, 8e3, 1.2e3, 0.758);
        }
        else {
            color = skybox.skyColor.rgb;
        }
	}

	fs_color = vec4(color, 1.0);
}