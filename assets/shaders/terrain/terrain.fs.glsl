#version 400 core

in vec2 gs_texCoord;
in vec2 gs_texCoordNorm;
in vec3 gs_tangent;

layout(location=0) out vec4 fs_diffuse;
layout(location=1) out vec4 fs_specular;
layout(location=2) out vec4 fs_normal;

layout(std140) uniform Material {
    float useDiffuseTexture;
    float useSpecularTexture;
    float useHighlightTexture;
    float useEmissiveTexture;
    float useNormalTexture;
    float shininess;
    vec4 diffuseColor;
    vec4 specularColor;
    vec4 emissiveColor;
} material;

uniform sampler2D normalmapTexture;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D highlightTexture;
uniform sampler2D emissiveTexture;
uniform sampler2D normalTexture;

vec4 hash4(vec2 p);
vec4 textureNoTile(sampler2D samp, in vec2 uv);

void main() {

    // TODO: Implement Emissive Shading

    fs_diffuse = vec4(1.0, 1.0, 1.0, 1.0);
    if(material.useDiffuseTexture > 0.0) {
        fs_diffuse = textureNoTile(diffuseTexture, gs_texCoord); //vec4(texture(diffuseTexture, gs_texCoord).rgb, 1.0);
    }
    fs_diffuse.rgb *= material.diffuseColor.rgb;

    fs_specular = vec4(1.0, 1.0, 1.0, 1.0);
    if(material.useSpecularTexture > 0.0) {
        fs_specular.r = textureNoTile(specularTexture, gs_texCoord).r; //texture(specularTexture, gs_texCoord).r;
    }
    fs_specular.r *= material.specularColor.r;

    if(material.useHighlightTexture > 0.0) {
        fs_specular.g = textureNoTile(highlightTexture, gs_texCoord).r / 128.0; //texture(highlightTexture, gs_texCoord).r / 128.0;
    }
    else {
        fs_specular.g = material.shininess / 128.0;
    }

    vec3 normal = normalize(texture(normalmapTexture, gs_texCoordNorm).rbg * 2.0 - 1.0);
    normal.z *= -1.0;

    if(material.useNormalTexture > 0.0) {
        normal *= -1.0;
        vec3 norm = textureNoTile(normalTexture, gs_texCoord).rgb * 2.0 - 1.0; //texture(normalTexture, gs_texCoord).rgb * 2.0 - 1.0;
        mat3 TBN = mat3(gs_tangent, normalize(cross(normal, gs_tangent)), normal);
        norm = norm * TBN;
        fs_normal = vec4((norm + 1.0) / 2.0, 1.0);
    }
    else {
        fs_normal = vec4((normal + 1.0) / 2.0, 1.0);
    }
}

// The MIT License
// Copyright © 2015 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


// One simple way to avoid texture tile repetition, at the cost of 4 times the amount of
// texture lookups (still much better than https://www.shadertoy.com/view/4tsGzf)
//
// More info: http://www.iquilezles.org/www/articles/texturerepetition/texturerepetition.htm

vec4 hash4(vec2 p) {
    return fract(sin(vec4( 1.0+dot(p,vec2(37.0,17.0)),
                           2.0+dot(p,vec2(11.0,47.0)),
                           3.0+dot(p,vec2(41.0,29.0)),
                           4.0+dot(p,vec2(23.0,31.0))))*103.0);
}

vec4 textureNoTile( sampler2D samp, in vec2 uv ) {
    vec2 iuv = floor( uv );
    vec2 fuv = fract( uv );

    vec4 ofa = hash4( iuv + vec2(0.0,0.0) );
    vec4 ofb = hash4( iuv + vec2(1.0,0.0) );
    vec4 ofc = hash4( iuv + vec2(0.0,1.0) );
    vec4 ofd = hash4( iuv + vec2(1.0,1.0) );

    vec2 ddx = dFdx( uv );
    vec2 ddy = dFdy( uv );

    // transform per-tile uvs
    ofa.zw = sign(ofa.zw-0.5);
    ofb.zw = sign(ofb.zw-0.5);
    ofc.zw = sign(ofc.zw-0.5);
    ofd.zw = sign(ofd.zw-0.5);

    // uv's, and derivarives (for correct mipmapping)
    vec2 uva = uv*ofa.zw + ofa.xy; vec2 ddxa = ddx*ofa.zw; vec2 ddya = ddy*ofa.zw;
    vec2 uvb = uv*ofb.zw + ofb.xy; vec2 ddxb = ddx*ofb.zw; vec2 ddyb = ddy*ofb.zw;
    vec2 uvc = uv*ofc.zw + ofc.xy; vec2 ddxc = ddx*ofc.zw; vec2 ddyc = ddy*ofc.zw;
    vec2 uvd = uv*ofd.zw + ofd.xy; vec2 ddxd = ddx*ofd.zw; vec2 ddyd = ddy*ofd.zw;

    // fetch and blend
    vec2 b = smoothstep(0.25,0.75,fuv);

    return mix( mix( textureGrad( samp, uva, ddxa, ddya ),
                    textureGrad( samp, uvb, ddxb, ddyb ), b.x ),
             mix( textureGrad( samp, uvc, ddxc, ddyc ),
                  textureGrad( samp, uvd, ddxd, ddyd ), b.x), b.y );
}