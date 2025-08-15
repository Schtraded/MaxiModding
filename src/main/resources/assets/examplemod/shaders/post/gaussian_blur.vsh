#version 120

attribute vec4 position;
attribute vec2 texCoord;

varying vec2 TexCoord0;

void main()
{
    TexCoord0 = texCoord;
    gl_Position = position;
}
