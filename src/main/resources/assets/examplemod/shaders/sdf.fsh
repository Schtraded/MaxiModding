#version 120

uniform vec2 iResolution;
uniform float iTime;
uniform vec2 uPosition;
uniform vec2 uSize;
uniform vec4 uCorners; // top-right, bottom-right, bottom-left, top-left
uniform vec4 uColor;
uniform vec4 uBackgroundColor;
uniform vec4 uBorderColor;
uniform float uBorderWidth;

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

    // Calculate border distances
    float outerDistance = d;
    float innerDistance = d + uBorderWidth;

    vec3 col;
    float alpha;

    // Anti-aliasing factor
    float aa = 2.0 / iResolution.y;

    if (uBorderWidth > 0.0) {
        // With border logic
        if (outerDistance <= 0.0) {
            // We're inside the outer shape
            if (innerDistance <= 0.0) {
                // Inside the inner shape (fill area)
                col = uColor.rgb;
                alpha = uColor.a;
            } else {
                // In the border area
                col = uBorderColor.rgb;
                alpha = uBorderColor.a;
            }

            // Apply anti-aliasing at outer edge
            float outerEdge = smoothstep(0.0, aa, -outerDistance);
            alpha *= outerEdge;

            // Apply anti-aliasing at inner edge (border to fill transition)
            if (innerDistance > -aa && innerDistance <= 0.0) {
                float innerEdge = smoothstep(0.0, aa, -innerDistance);
                col = mix(uBorderColor.rgb, uColor.rgb, innerEdge);
                alpha = mix(uBorderColor.a, uColor.a, innerEdge) * outerEdge;
            }
        } else {
            // Outside the shape
            col = uBackgroundColor.rgb;
            alpha = uBackgroundColor.a;
        }
    } else {
        // No border - simplified logic
        if (outerDistance <= 0.0) {
            // Inside the shape
            col = uColor.rgb;
            alpha = uColor.a;

            // Apply anti-aliasing
            float edge = smoothstep(0.0, aa, -outerDistance);
            alpha *= edge;
        } else {
            // Outside the shape
            col = uBackgroundColor.rgb;
            alpha = uBackgroundColor.a;
        }
    }

    gl_FragColor = vec4(col, alpha);
}