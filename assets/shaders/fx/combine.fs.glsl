#version 430 core

in vec2 vs_texCoord;

layout(location=0) out vec4 fs_color;

uniform sampler2D colorTexture1;
uniform sampler2D colorTexture2;
uniform sampler2D depthTexture1;
uniform sampler2D depthTexture2;

void main() {
    if(texture(depthTexture1, vs_texCoord).r < texture(depthTexture2, vs_texCoord).r) {
	    fs_color = texture(colorTexture1, vs_texCoord);
	}
	else {
	    fs_color = texture(colorTexture2, vs_texCoord);
	}
}