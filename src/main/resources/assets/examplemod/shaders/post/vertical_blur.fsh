#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 TexelSize; // x = 0, y = 1/height for vertical pass

varying vec2 TexCoord0;

void main()
{
    vec4 color = vec4(0.0);

    float offsets[5] = float[](0.0, 1.3846153846, 3.2307692308, -1.3846153846, -3.2307692308);
    float weights[5] = float[](0.2270270270, 0.3162162162, 0.0702702703, 0.3162162162, 0.0702702703);

    for(int i = 0; i < 5; i++) {
        color += texture2D(DiffuseSampler, TexCoord0 + vec2(0.0, offsets[i] * TexelSize.y)) * weights[i];
    }

    gl_FragColor = color;
}