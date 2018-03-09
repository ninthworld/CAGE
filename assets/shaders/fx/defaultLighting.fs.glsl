#version 430 core

#define MAX_LIGHTS 2

layout(location=0) in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

layout(binding=0) uniform sampler2D ambientTexture;
layout(binding=1) uniform sampler2D diffuseTexture;
layout(binding=2) uniform sampler2D specularTexture;
layout(binding=3) uniform sampler2D normalTexture;
layout(binding=4) uniform sampler2D depthTexture;

struct Light_t {
	vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
    vec4 worldPosition;
    float range;
    float attenuation;
    float attLinear;
    float attQuadratic;    	
};
    
layout(binding=5, std140) uniform Light {
     Light_t light[MAX_LIGHTS];
};

layout(binding=6, std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

vec3 worldPosFromDepth(float depth) {
	vec4 ssPos = camera.invProjMatrix * vec4(vs_texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
	vec4 wsPos = camera.invViewMatrix * ssPos;
	return wsPos.xyz / wsPos.w;
}

void main() {
	
	vec3 ambient = texture(ambientTexture, vs_texCoord).rgb;
	vec3 diffuse = texture(diffuseTexture, vs_texCoord).rgb;
	vec3 specular = texture(specularTexture, vs_texCoord).rgb;
	float power = texture(specularTexture, vs_texCoord).a;
	vec3 normal = texture(normalTexture, vs_texCoord).rgb * 2.0 - 1.0;
	float depth = texture(depthTexture, vs_texCoord).r;
	vec3 position = worldPosFromDepth(depth);

	vec3 color = vec3(0.0, 0.0, 0.0);
	if(depth < 1.0) {
		for(int i = 0; i < MAX_LIGHTS; ++i) {
			float dist = distance(light[i].worldPosition.xyz, position);
			float cosTheta = dot(normal, normalize(light[i].worldPosition.xyz));
			
			if(light[i].attConstant == 0.0) {
				cosTheta *= 1.0f - clamp(distance(position, light[i].worldPosition.xyz) / light[i].range, 0.0, 1.0);
				if(light[i].attQuadratic > 0.0) {
					cosTheta *= cosTheta;
				}
			}
			color += mix(ambient + light[i].ambientColor.rgb, diffuse + light[i].diffuseColor.rgb, clamp(cosTheta, 0.0, 1.0));
		}
	}
	else {
		color = diffuse;
	}
	
	fs_color = vec4(color, 1.0);
}