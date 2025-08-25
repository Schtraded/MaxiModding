#version 120

uniform vec2 resolution;
uniform vec2 center;
uniform float radius;
uniform vec4 color;
uniform float borderWidth;
uniform vec4 borderColor;
uniform float smoothness;

void main() {
    // Convert fragment coordinates to normalized UV coordinates (0.0 to 1.0)
    vec2 uv = gl_FragCoord.xy / resolution.xy;

    // Calculate distance from fragment to circle center
    float dist = distance(uv, center);

    // Calculate the outer and inner radius for border
    float outerRadius = radius;
    float innerRadius = radius - borderWidth;

    // Create smooth circles using smoothstep for anti-aliasing
    float outerCircle = 1.0 - smoothstep(outerRadius - smoothness, outerRadius + smoothness, dist);
    float innerCircle = 1.0 - smoothstep(innerRadius - smoothness, innerRadius + smoothness, dist);

    // Calculate border mask (outer circle minus inner circle)
    float borderMask = outerCircle - innerCircle;

    // Mix colors based on whether we're in the border or fill area
    vec4 finalColor = mix(color, borderColor, borderMask);

    // Set final alpha based on the outer circle
    finalColor.a *= outerCircle;

    gl_FragColor = finalColor;
}