#version 120

uniform sampler2D texture;
uniform vec2 resolution;

varying vec2 texCoords;

void main() {
    vec4 sum = vec4(0.0);
    int radius = 5;

    // Pre-calculated weights - can't use arrays in GLSL 120
    float weight0 = 0.006136;
    float weight1 = 0.024477;
    float weight2 = 0.078815;
    float weight3 = 0.207161;
    float weight4 = 0.294639;
    float weight5 = 0.294639; // center weight

    // Apply weights manually
    sum += texture2D(texture, texCoords + vec2(-5.0 / resolution.x, 0.0)) * weight0;
    sum += texture2D(texture, texCoords + vec2(-4.0 / resolution.x, 0.0)) * weight1;
    sum += texture2D(texture, texCoords + vec2(-3.0 / resolution.x, 0.0)) * weight2;
    sum += texture2D(texture, texCoords + vec2(-2.0 / resolution.x, 0.0)) * weight3;
    sum += texture2D(texture, texCoords + vec2(-1.0 / resolution.x, 0.0)) * weight4;
    sum += texture2D(texture, texCoords + vec2( 0.0 / resolution.x, 0.0)) * weight5;
    sum += texture2D(texture, texCoords + vec2( 1.0 / resolution.x, 0.0)) * weight4;
    sum += texture2D(texture, texCoords + vec2( 2.0 / resolution.x, 0.0)) * weight3;
    sum += texture2D(texture, texCoords + vec2( 3.0 / resolution.x, 0.0)) * weight2;
    sum += texture2D(texture, texCoords + vec2( 4.0 / resolution.x, 0.0)) * weight1;
    sum += texture2D(texture, texCoords + vec2( 5.0 / resolution.x, 0.0)) * weight0;

    gl_FragColor = sum;
}