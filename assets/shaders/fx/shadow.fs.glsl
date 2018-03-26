#version 430 core

#define MAX_SOURCES     100.0
#define MAX_SHADOWS     4
#define SHADOW_DIST0    8.0
#define SHADOW_DIST1    16.0
#define SHADOW_DIST2    32.0
#define SHADOW_DIST3    128.0

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D depthTexture;
uniform sampler2D shadowTexture[MAX_SHADOWS];

layout(std140) uniform Shadow {
    mat4 viewProjMatrix[MAX_SHADOWS];
} shadow;

layout(std140) uniform Camera {
    mat4 projMatrix;
    mat4 viewMatrix;
    mat4 invProjMatrix;
    mat4 invViewMatrix;
} camera;

mat4 biasMatrix = mat4(
	0.5, 0.0, 0.0, 0.0,
	0.0, 0.5, 0.0, 0.0,
	0.0, 0.0, 0.5, 0.0,
	0.5, 0.5, 0.5, 1.0);

vec3 worldPosFromDepth(float depth) {
	vec4 ssPos = camera.invProjMatrix * vec4(vs_texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
	vec4 wsPos = camera.invViewMatrix * ssPos;
	return wsPos.xyz / wsPos.w;
}

void main() {
	float depth = texture(depthTexture, vs_texCoord).r;
	vec3 position = worldPosFromDepth(depth);
	mat4 camView = inverse(camera.viewMatrix);
	vec3 camPosition = vec3(camView[3]) / camView[3].w;
	float camDistance = distance(position, camPosition);

    vec4 shadowColor = vec4(1.0, 1.0, 1.0, 1.0);
	if(depth < 1.0) {
        float sampleOffset = 0.0005;
	    float visibility = 0.0;
        if (camDistance < SHADOW_DIST0) {
            vec4 shadowCoord = biasMatrix * shadow.viewProjMatrix[0] * vec4(position, 1.0);
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    if (texture(shadowTexture[0], shadowCoord.xy + vec2(i, j) * sampleOffset).r < shadowCoord.z - 0.005) {
                        visibility += 1.0;
                    }
                }
            }
        }
        else if (camDistance < SHADOW_DIST1) {
            vec4 shadowCoord = biasMatrix * shadow.viewProjMatrix[1] * vec4(position, 1.0);
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    if (texture(shadowTexture[1], shadowCoord.xy + vec2(i, j) * sampleOffset).r < shadowCoord.z - 0.005) {
                        visibility += 1.0;
                    }
                }
            }
        }
        else if (camDistance < SHADOW_DIST2) {
            vec4 shadowCoord = biasMatrix * shadow.viewProjMatrix[2] * vec4(position, 1.0);
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    if (texture(shadowTexture[2], shadowCoord.xy + vec2(i, j) * sampleOffset).r < shadowCoord.z - 0.005) {
                        visibility += 1.0;
                    }
                }
            }
        }
        else if (camDistance < SHADOW_DIST3) {
            vec4 shadowCoord = biasMatrix * shadow.viewProjMatrix[3] * vec4(position, 1.0);
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    if (texture(shadowTexture[3], shadowCoord.xy + vec2(i, j) * sampleOffset).r < shadowCoord.z - 0.005) {
                        visibility += 1.0;
                    }
                }
            }
        }
        visibility = 1.0 - (visibility / 9.0);
        fs_color = vec4(visibility, visibility, visibility, 1.0);
	}
	else {
	    fs_color = vec4(1.0, 1.0, 1.0, 1.0);
	}

	fs_color /= MAX_SOURCES;
}