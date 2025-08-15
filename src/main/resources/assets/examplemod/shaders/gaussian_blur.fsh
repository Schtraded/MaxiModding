#version 120

uniform sampler2D texture;
uniform float radius;
uniform vec2 resolution;

varying vec2 texCoords;

float gaussian(float x, float sigma) {
    return exp(- (x * x) / (2.0 * sigma * sigma)) / (2.0 * 3.14159265 * sigma * sigma);
}

void main() {
    vec4 sum = vec4(0.0);
    float sigma = radius / 2.0;
    float total = 0.0;

    for (int x = -int(radius); x <= int(radius); x++) {
        for (int y = -int(radius); y <= int(radius); y++) {
            float weight = gaussian(float(x), sigma) * gaussian(float(y), sigma);
            vec2 offset = vec2(float(x) / resolution.x, float(y) / resolution.y);
            sum += texture2D(texture, texCoords + offset) * weight;
            total += weight;
        }
    }

    gl_FragColor = sum / total;
}
