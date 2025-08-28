#version 120

// Uniforms
uniform vec2 u_position;        // Top-left position (0-1 normalized)
uniform vec2 u_size;            // Width and height (0-1 normalized)
uniform vec4 u_topLeftColor;
uniform vec4 u_topRightColor;
uniform vec4 u_bottomLeftColor;
uniform vec4 u_bottomRightColor;

// Varying from vertex shader
varying vec2 v_texCoord;

void main() {
    // Convert texture coordinates to screen space (0-1)
    vec2 screenPos = vec2(v_texCoord.x, 1.0 - v_texCoord.y);;

    // Calculate rectangle bounds
    vec2 rectMin = u_position;
    vec2 rectMax = u_position + u_size;

    // Check if pixel is inside rectangle
    if (screenPos.x < rectMin.x || screenPos.x > rectMax.x ||
    screenPos.y < rectMin.y || screenPos.y > rectMax.y) {
        discard; // Outside rectangle, don't render
    }

    // Calculate normalized coordinates within the rectangle (0 to 1)
    vec2 rectCoord = (screenPos - rectMin) / u_size;

    // Bilinear interpolation between the four corner colors
    // First interpolate top and bottom edges
    vec4 topColor = mix(u_topLeftColor, u_topRightColor, rectCoord.x);
    vec4 bottomColor = mix(u_bottomLeftColor, u_bottomRightColor, rectCoord.x);

    // Then interpolate vertically (note: Y is flipped in screen coordinates)
    vec4 finalColor = mix(topColor, bottomColor, rectCoord.y);

    // Output final color
    gl_FragColor = finalColor;
}