#version 120

uniform vec2 iResolution;
uniform float iTime;
uniform vec2 uPosition;
uniform vec2 uSize;
uniform vec4 uCorners; // top-right, bottom-right, bottom-left, top-left
uniform vec4 uColor;
uniform vec4 uBackgroundColor;

// SDF for rounded rectangle with individual corner radii
// Based on Inigo Quilez's implementation
// p: point to test
// b: half-size of rectangle (width/2, height/2)
// r: corner radii (top-right, bottom-right, bottom-left, top-left)
float sdRoundBox(in vec2 p, in vec2 b, in vec4 r) {
    // Select the correct corner radius based on quadrant
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;

    // Calculate distance
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    // Get fragment coordinates in normalized space
    vec2 fragCoord = gl_FragCoord.xy;

    // Convert to centered coordinates (-1 to 1)
    vec2 p = (2.0 * fragCoord - iResolution.xy) / iResolution.y;

    // Adjust for position offset
    p -= uPosition;

    // Calculate half-size for the SDF function
    vec2 halfSize = uSize;

    // Ensure corner radii don't exceed the rectangle size
    vec4 corners = uCorners;
    corners = min(corners, min(halfSize.x, halfSize.y));

    // Calculate the signed distance
    float d = sdRoundBox(p, halfSize, corners);

    // Clean fill: inside (d <= 0) gets the main color, outside gets background
    vec3 col;
    float alpha;

    if (d <= 0.0) {
        // Inside the shape - use main color
        col = uColor.rgb;
        alpha = uColor.a;
    } else {
        // Outside the shape - use background color
        col = uBackgroundColor.rgb;
        alpha = uBackgroundColor.a;
    }

    // Smooth anti-aliasing at the edge
    float edge = smoothstep(0.0, 2.0 / iResolution.y, -d);

    // If background is transparent, fade out the shape at edges
    if (uBackgroundColor.a < 0.01) {
        alpha = uColor.a * edge;
        col = uColor.rgb;
    } else {
        // Blend between background and shape color at the edge
        col = mix(uBackgroundColor.rgb, uColor.rgb, edge);
        alpha = mix(uBackgroundColor.a, uColor.a, edge);
    }

    gl_FragColor = vec4(col, alpha);
}