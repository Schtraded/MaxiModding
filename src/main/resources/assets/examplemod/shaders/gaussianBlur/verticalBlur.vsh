uniform sampler2D image;
uniform float texOffset; // 1.0 / texture width
uniform float weight[5]; // Gaussian weights

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec4 color = texture2D(image, uv) * weight[0];
    for (int i = 1; i < 5; i++) {
        color += texture2D(image, uv + vec2(0.0, texOffset*i)) * weight[i];
        color += texture2D(image, uv - vec2(0.0, texOffset*i)) * weight[i];
    }
    gl_FragColor = color;
}