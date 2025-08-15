#version 120
varying vec2 blurTextureCoords[11];
uniform sampler2D originalTexture;

void main() {
	vec4 color = vec4(0.0);
	color += texture2D(originalTexture, blurTextureCoords[0]) * 0.0093;
	color += texture2D(originalTexture, blurTextureCoords[1]) * 0.028002;
	color += texture2D(originalTexture, blurTextureCoords[2]) * 0.065984;
	color += texture2D(originalTexture, blurTextureCoords[3]) * 0.121703;
	color += texture2D(originalTexture, blurTextureCoords[4]) * 0.175713;
	color += texture2D(originalTexture, blurTextureCoords[5]) * 0.198596;
	color += texture2D(originalTexture, blurTextureCoords[6]) * 0.175713;
	color += texture2D(originalTexture, blurTextureCoords[7]) * 0.121703;
	color += texture2D(originalTexture, blurTextureCoords[8]) * 0.065984;
	color += texture2D(originalTexture, blurTextureCoords[9]) * 0.028002;
	color += texture2D(originalTexture, blurTextureCoords[10]) * 0.0093;

	gl_FragColor = color;
}