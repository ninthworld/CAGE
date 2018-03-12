#version 430 core

#define MAX_LIGHTS 3

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

struct Light_t {
	vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
    vec4 worldPosition;
    float range;
    float attConstant;
    float attLinear;
    float attQuadratic;
};

layout(std140) uniform Light {
     Light_t light[MAX_LIGHTS];
};

layout(std140) uniform Camera {
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
	vec3 diffuse = texture(diffuseTexture, vs_texCoord).rgb;
	vec3 specular = texture(specularTexture, vs_texCoord).rgb;
	float power = texture(specularTexture, vs_texCoord).a * 128.0;
	vec3 normal = texture(normalTexture, vs_texCoord).rgb * 2.0 - 1.0;
	float depth = texture(depthTexture, vs_texCoord).r;
	vec3 position = worldPosFromDepth(depth);

	vec3 camPosition = -vec3(camera.viewMatrix * vec4(0.0, 0.0, 0.0, 1.0));

	vec3 color = vec3(0.0, 0.0, 0.0);
	if(depth < 1.0) {
		for(int i = 0; i < MAX_LIGHTS; ++i) {
            // Ambient
            color += diffuse * light[i].ambientColor.rgb;

            // Diffuse
			float dist = distance(light[i].worldPosition.xyz, position);
			float cosTheta = dot(normal, normalize(light[i].worldPosition.xyz));
			float att = 1.0;
			if(light[i].range > 0.0 && (light[i].attLinear > 0.0 || light[i].attQuadratic > 0.0)) {
			    att = clamp(1.0 - dist/light[i].range, 0.0, 1.0);
				if(light[i].attQuadratic > 0.0) {
					att *= att;
				}
			}
            color += clamp(diffuse * light[i].diffuseColor.rgb * cosTheta * att, 0.0, 1.0);

            // Specular
            vec3 vertexToCam = normalize(camPosition - position);
            vec3 lightReflect = normalize(reflect(-normalize(light[i].worldPosition.xyz), normal));
            float factor = dot(vertexToCam, lightReflect);
            if(factor > 0.0) {
            factor = pow(factor, power);
                color += clamp(att * specular * light[i].specularColor.rgb * factor, 0.0, 1.0);
            }
		}
	}
	else {
		color = diffuse;
	}

	fs_color = vec4(color, 1.0);
}