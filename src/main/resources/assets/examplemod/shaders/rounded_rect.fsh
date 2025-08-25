#version 120

uniform vec2 resolution;
uniform vec2 position;      // Top-left corner position (0-1 range)
uniform vec2 size;          // Width and height (0-1 range)
uniform vec4 cornerRadii;   // Top-left, top-right, bottom-right, bottom-left radii
uniform vec4 color;
uniform float borderWidth;
uniform vec4 borderColor;
uniform float smoothness;

// Signed distance function for a rounded rectangle
float sdRoundedRect(vec2 p, vec2 rectSize, vec4 radii) {
    // Choose the correct radius for each quadrant
    vec2 r = p.x > 0.0 ? (p.y > 0.0 ? radii.zy : radii.xw) : (p.y > 0.0 ? radii.wz : radii.xy);
    r.x = p.x > 0.0 ? r.x : r.y;

    // Calculate distance to rounded corner
    vec2 q = abs(p) - rectSize + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    // Convert fragment coordinates to UV space (0.0 to 1.0)
    vec2 uv = gl_FragCoord.xy / resolution.xy;

    // Calculate rectangle bounds
    vec2 rectCenter = position + size * 0.5;
    vec2 rectHalfSize = size * 0.5;

    // Transform UV to be relative to rectangle center
    vec2 p = uv - rectCenter;

    // Calculate signed distance to rounded rectangle
    float dist = sdRoundedRect(p, rectHalfSize, cornerRadii);

    // Create smooth edges for outer and inner (border) boundaries
    float outerMask = 1.0 - smoothstep(-smoothness, smoothness, dist);
    float innerMask = 1.0 - smoothstep(-smoothness, smoothness, dist + borderWidth);

    // Calculate border mask (outer shape minus inner shape)
    float borderMask = outerMask - innerMask;

    // Mix fill and border colors
    vec4 finalColor = mix(color, borderColor, borderMask);

    // Apply the outer mask to alpha
    finalColor.a *= outerMask;

    gl_FragColor = finalColor;
}