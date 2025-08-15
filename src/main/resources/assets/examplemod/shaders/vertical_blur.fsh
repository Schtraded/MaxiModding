#version 120

uniform sampler2D texture;
uniform vec2 resolution;

varying vec2 texCoords;

void main() {
    vec4 sum = vec4(0.0);
    int radius = 5;

    // Pre-calculated weights - same as horizontal
    float weight0 = 0.006136;
    float weight1 = 0.024477;
    float weight2 = 0.078815;
    float weight3 = 0.207161;
    float weight4 = 0.294639;
    float weight5 = 0.294639; // center weight

    // Apply weights manually for vertical
    sum += texture2D(texture, texCoords + vec2(0.0, -5.0 / resolution.y)) * weight0;
    sum += texture2D(texture, texCoords + vec2(0.0, -4.0 / resolution.y)) * weight1;
    sum += texture2D(texture, texCoords + vec2(0.0, -3.0 / resolution.y)) * weight2;
    sum += texture2D(texture, texCoords + vec2(0.0, -2.0 / resolution.y)) * weight3;
    sum += texture2D(texture, texCoords + vec2(0.0, -1.0 / resolution.y)) * weight4;
    sum += texture2D(texture, texCoords + vec2(0.0,  0.0 / resolution.y)) * weight5;
    sum += texture2D(texture, texCoords + vec2(0.0,  1.0 / resolution.y)) * weight4;
    sum += texture2D(texture, texCoords + vec2(0.0,  2.0 / resolution.y)) * weight3;
    sum += texture2D(texture, texCoords + vec2(0.0,  3.0 / resolution.y)) * weight2;
    sum += texture2D(texture, texCoords + vec2(0.0,  4.0 / resolution.y)) * weight1;
    sum += texture2D(texture, texCoords + vec2(0.0,  5.0 / resolution.y)) * weight0;

    gl_FragColor = sum;
}