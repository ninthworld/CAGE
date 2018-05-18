#version 430 core

#define MAX_WEIGHTS 3

layout(location=0) in vec3 in_position;
layout(location=1) in vec2 in_texCoord;
layout(location=2) in vec3 in_normal;
layout(location=3) in vec3 in_weights;
layout(location=4) in vec3 in_jointIndices;

out vec3 vs_normal;
out vec2 vs_texCoord;

layout(std140) buffer Entity {
    mat4 worldMatrix[];
};

layout(std140) buffer Bone {
    mat4 jointTransforms[];
};

void main() {
    int id = gl_InstanceID;

	mat4 worldRotationMatrix = worldMatrix[id];
	worldRotationMatrix[3] = vec4(0.0, 0.0, 0.0, 1.0);

	vec4 position = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 normal = vec4(0.0, 0.0, 0.0, 0.0);

	for(int i=0; i<MAX_WEIGHTS; ++i) {
	    int index = int(in_jointIndices[i]);
		mat4 jointTransform = jointTransforms[index];
		vec4 pos = jointTransform * vec4(in_position, 1.0);
		position += pos * in_weights[i];
		vec4 norm = jointTransform * vec4(in_normal, 0.0);
		normal += norm * in_weights[i];
	}

    vs_normal = normalize(vec3(worldRotationMatrix * normal));
    vs_texCoord = in_texCoord;
    gl_Position = worldMatrix[id] * position;
}