#version 120

//// Attributes
//attribute vec2 gl_Vertex;

// Uniforms
uniform vec2 u_position;        // Top-left position (0-1 normalized)
uniform vec2 u_size;            // Width and height (0-1 normalized)
uniform vec4 u_topLeftColor;
uniform vec4 u_topRightColor;
uniform vec4 u_bottomLeftColor;
uniform vec4 u_bottomRightColor;

// Varying to fragment shader
varying vec2 v_texCoord;
varying vec4 v_topLeftColor;
varying vec4 v_topRightColor;
varying vec4 v_bottomLeftColor;
varying vec4 v_bottomRightColor;


void main() {
    // Pass texture coordinates and colors to fragment shader
    v_texCoord = gl_MultiTexCoord0.xy;
    v_topLeftColor = u_topLeftColor;
    v_topRightColor = u_topRightColor;
    v_bottomLeftColor = u_bottomLeftColor;
    v_bottomRightColor = u_bottomRightColor;

    // Output vertex position
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

//void main() {
//    // Pass texture coordinates and colors to fragment shader
//    v_texCoord = gl_Vertex.xy;
//    v_topLeftColor = u_topLeftColor;
//    v_topRightColor = u_topRightColor;
//    v_bottomLeftColor = u_bottomLeftColor;
//    v_bottomRightColor = u_bottomRightColor;
//
//    // Output vertex position (fullscreen quad)
//    gl_Position = vec4(gl_Vertex.xy * 2.0 - 1.0, 0.0, 1.0);
//}