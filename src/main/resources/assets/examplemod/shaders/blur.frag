uniform sampler2D texture;
uniform vec2 resolution;
uniform float radius;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec4 sum = vec4(0.0);

    float count = 0.0;
    for (float x = -radius; x <= radius; x++) {
        for (float y = -radius; y <= radius; y++) {
            vec2 offset = vec2(x, y) / resolution;
            sum += texture2D(texture, uv + offset);
            count += 1.0;
        }
    }

    gl_FragColor = sum / count;
}