package com.example.examplemod.helper;

import java.util.ArrayList;
import java.util.List;

public class ColorFinderUtil {

    public static class ColorPosition {
        public final float x, y;
        public final boolean found;

        public ColorPosition(float x, float y, boolean found) {
            this.x = x;
            this.y = y;
            this.found = found;
        }

        @Override
        public String toString() {
            return found ? String.format("ColorPosition(%.2f, %.2f)", x, y) : "ColorPosition(not found)";
        }
    }

    /**
     * Find the Y position of a color in a rainbow gradient
     * @param targetColor The color to find
     * @param rectY The Y position of the rainbow rectangle
     * @param rectHeight The height of the rainbow rectangle
     * @return The Y position where the color appears, or -1 if not found
     */
    public static float getRainbowYPositionForColor(int targetColor, float rectY, float rectHeight) {
        // Remove alpha channel for comparison
        int targetRGB = targetColor & 0x00FFFFFF;

        float segmentHeight = rectHeight / 7.0f;

        // Define the gradient segments with their start and end colors
        int[][] segments = {
                {0xFF0000, 0xFF8000}, // Red to Orange
                {0xFF8000, 0xFFFF00}, // Orange to Yellow
                {0xFFFF00, 0x00FF00}, // Yellow to Green
                {0x00FF00, 0x00FFFF}, // Green to Cyan
                {0x00FFFF, 0x0000FF}, // Cyan to Blue
                {0x0000FF, 0xFF00FF}, // Blue to Magenta
                {0xFF00FF, 0xFF0000}  // Magenta to Red
        };

        // Check each segment
        for (int segment = 0; segment < segments.length; segment++) {
            int startColor = segments[segment][0];
            int endColor = segments[segment][1];

            // Extract RGB components for start color
            int startR = (startColor >> 16) & 0xFF;
            int startG = (startColor >> 8) & 0xFF;
            int startB = startColor & 0xFF;

            // Extract RGB components for end color
            int endR = (endColor >> 16) & 0xFF;
            int endG = (endColor >> 8) & 0xFF;
            int endB = endColor & 0xFF;

            // Extract RGB components for target color
            int targetR = (targetRGB >> 16) & 0xFF;
            int targetG = (targetRGB >> 8) & 0xFF;
            int targetB = targetRGB & 0xFF;

            // Check if color could be in this segment
            boolean couldBeInSegment = isValueInRange(targetR, startR, endR) &&
                    isValueInRange(targetG, startG, endG) &&
                    isValueInRange(targetB, startB, endB);

            if (couldBeInSegment) {
                // Find the interpolation factor (0.0 to 1.0) within this segment
                float t = findInterpolationFactor(startR, startG, startB,
                        endR, endG, endB,
                        targetR, targetG, targetB);

                if (t >= 0.0f && t <= 1.0f) {
                    // Calculate Y position within this segment
                    float segmentStartY = rectY + (segment * segmentHeight);
                    return segmentStartY + (t * segmentHeight);
                }
            }
        }

        return -1.0f; // Not found
    }

    /**
     * Find the position of a color in a bilinear gradient rectangle
     * @param targetColor The color to find
     * @param rectX X position of the rectangle
     * @param rectY Y position of the rectangle
     * @param rectWidth Width of the rectangle
     * @param rectHeight Height of the rectangle
     * @param topLeftColor Color at top-left corner
     * @param topRightColor Color at top-right corner
     * @param bottomLeftColor Color at bottom-left corner
     * @param bottomRightColor Color at bottom-right corner
     * @return ColorPosition with x, y coordinates and found status
     */
    public static ColorPosition getBilinearPositionForColor(int targetColor,
                                                            float rectX, float rectY,
                                                            float rectWidth, float rectHeight,
                                                            int topLeftColor, int topRightColor,
                                                            int bottomLeftColor, int bottomRightColor) {
        return getBilinearPositionForColor(targetColor, rectX, rectY, rectWidth, rectHeight,
                topLeftColor, topRightColor, bottomLeftColor, bottomRightColor,
                50, 5.0f); // Default grid resolution and tolerance
    }

    /**
     * Find the position of a color in a bilinear gradient rectangle with custom parameters
     */
    public static ColorPosition getBilinearPositionForColor(int targetColor,
                                                            float rectX, float rectY,
                                                            float rectWidth, float rectHeight,
                                                            int topLeftColor, int topRightColor,
                                                            int bottomLeftColor, int bottomRightColor,
                                                            int gridResolution, float tolerance) {

        // Remove alpha channel for comparison
        int targetRGB = targetColor & 0x00FFFFFF;

        // Extract RGB components for all corners
        float[] topLeft = extractRGB(topLeftColor);
        float[] topRight = extractRGB(topRightColor);
        float[] bottomLeft = extractRGB(bottomLeftColor);
        float[] bottomRight = extractRGB(bottomRightColor);

        float[] target = extractRGB(targetRGB);

        // Search through the gradient space using a grid approach
        float bestDistance = Float.MAX_VALUE;
        float bestU = -1, bestV = -1;

        for (int i = 0; i <= gridResolution; i++) {
            for (int j = 0; j <= gridResolution; j++) {
                float u = (float) i / gridResolution; // 0 to 1 (left to right)
                float v = (float) j / gridResolution; // 0 to 1 (top to bottom)

                // Bilinear interpolation
                float[] interpolated = bilinearInterpolate(topLeft, topRight, bottomLeft, bottomRight, u, v);

                // Calculate distance to target color
                float distance = colorDistance(interpolated, target);

                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestU = u;
                    bestV = v;
                }
            }
        }

        // Refine the result with a smaller search around the best point
        float refinement = 1.0f / gridResolution;
        for (int iter = 0; iter < 3; iter++) {
            float stepSize = refinement / 10.0f;

            for (float du = -refinement; du <= refinement; du += stepSize) {
                for (float dv = -refinement; dv <= refinement; dv += stepSize) {
                    float u = Math.max(0, Math.min(1, bestU + du));
                    float v = Math.max(0, Math.min(1, bestV + dv));

                    float[] interpolated = bilinearInterpolate(topLeft, topRight, bottomLeft, bottomRight, u, v);
                    float distance = colorDistance(interpolated, target);

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestU = u;
                        bestV = v;
                    }
                }
            }
            refinement *= 0.1f;
        }

        // Check if we found a reasonable match
        boolean found = bestDistance <= tolerance / 255.0f;

        if (found) {
            // Convert normalized coordinates back to screen coordinates
            float screenX = rectX + (bestU * rectWidth);
            float screenY = rectY + (bestV * rectHeight);
            return new ColorPosition(screenX, screenY, true);
        }

        return new ColorPosition(0, 0, false);
    }

    /**
     * Find all positions where a color appears in a bilinear gradient (with tolerance)
     */
    public static List<ColorPosition> getAllBilinearPositionsForColor(int targetColor,
                                                                      float rectX, float rectY,
                                                                      float rectWidth, float rectHeight,
                                                                      int topLeftColor, int topRightColor,
                                                                      int bottomLeftColor, int bottomRightColor,
                                                                      float tolerance) {

        List<ColorPosition> positions = new ArrayList<>();
        int targetRGB = targetColor & 0x00FFFFFF;

        float[] topLeft = extractRGB(topLeftColor);
        float[] topRight = extractRGB(topRightColor);
        float[] bottomLeft = extractRGB(bottomLeftColor);
        float[] bottomRight = extractRGB(bottomRightColor);
        float[] target = extractRGB(targetRGB);

        int gridResolution = 100;

        for (int i = 0; i <= gridResolution; i++) {
            for (int j = 0; j <= gridResolution; j++) {
                float u = (float) i / gridResolution;
                float v = (float) j / gridResolution;

                float[] interpolated = bilinearInterpolate(topLeft, topRight, bottomLeft, bottomRight, u, v);
                float distance = colorDistance(interpolated, target);

                if (distance <= tolerance / 255.0f) { // Convert tolerance to 0-1 range
                    float screenX = rectX + (u * rectWidth);
                    float screenY = rectY + (v * rectHeight);
                    positions.add(new ColorPosition(screenX, screenY, true));
                }
            }
        }

        return positions;
    }

    // Helper methods
    private static boolean isValueInRange(int value, int start, int end) {
        int min = Math.min(start, end);
        int max = Math.max(start, end);
        return value >= min && value <= max;
    }

    private static float findInterpolationFactor(int startR, int startG, int startB,
                                                 int endR, int endG, int endB,
                                                 int targetR, int targetG, int targetB) {
        // Try to find t such that: target = start + t * (end - start)
        // We'll use the component with the largest difference for best accuracy

        int diffR = Math.abs(endR - startR);
        int diffG = Math.abs(endG - startG);
        int diffB = Math.abs(endB - startB);

        float t = 0.0f;

        if (diffR >= diffG && diffR >= diffB && diffR > 0) {
            // Use red component
            t = (float)(targetR - startR) / (endR - startR);
        } else if (diffG >= diffB && diffG > 0) {
            // Use green component
            t = (float)(targetG - startG) / (endG - startG);
        } else if (diffB > 0) {
            // Use blue component
            t = (float)(targetB - startB) / (endB - startB);
        }

        // Verify the interpolation is correct by checking all components
        int interpolatedR = startR + Math.round(t * (endR - startR));
        int interpolatedG = startG + Math.round(t * (endG - startG));
        int interpolatedB = startB + Math.round(t * (endB - startB));

        // Allow small tolerance for rounding errors
        int tolerance = 2;
        if (Math.abs(interpolatedR - targetR) <= tolerance &&
                Math.abs(interpolatedG - targetG) <= tolerance &&
                Math.abs(interpolatedB - targetB) <= tolerance) {
            return t;
        }

        return -1.0f; // Invalid interpolation
    }

    private static float[] extractRGB(int color) {
        return new float[] {
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
    }

    private static float[] bilinearInterpolate(float[] topLeft, float[] topRight,
                                               float[] bottomLeft, float[] bottomRight,
                                               float u, float v) {
        // u: 0=left, 1=right
        // v: 0=top, 1=bottom

        // Interpolate top edge
        float[] top = new float[3];
        for (int i = 0; i < 3; i++) {
            top[i] = topLeft[i] * (1 - u) + topRight[i] * u;
        }

        // Interpolate bottom edge
        float[] bottom = new float[3];
        for (int i = 0; i < 3; i++) {
            bottom[i] = bottomLeft[i] * (1 - u) + bottomRight[i] * u;
        }

        // Interpolate between top and bottom
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = top[i] * (1 - v) + bottom[i] * v;
        }

        return result;
    }

    private static float colorDistance(float[] color1, float[] color2) {
        // Euclidean distance in RGB space
        float dr = color1[0] - color2[0];
        float dg = color1[1] - color2[1];
        float db = color1[2] - color2[2];
        return (float) Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * Utility method to convert RGB values to hex color
     */
    public static int rgbToHex(int r, int g, int b) {
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /**
     * Utility method to extract RGB components from hex color
     */
    public static int[] hexToRGB(int color) {
        return new int[] {
                (color >> 16) & 0xFF, // Red
                (color >> 8) & 0xFF,  // Green
                color & 0xFF          // Blue
        };
    }

    // Usage examples and testing
    public static class Examples {

        public static void testRainbowGradient() {
            System.out.println("=== Rainbow Gradient Test ===");

            float rectY = 100.0f;
            float rectHeight = 200.0f;

            // Test some rainbow colors
            int[] testColors = {
                    0xFFFF0000, // Red
                    0xFFFF8000, // Orange
                    0xFFFFFF00, // Yellow
                    0xFF00FF00, // Green
                    0xFF00FFFF, // Cyan
                    0xFF0000FF, // Blue
                    0xFFFF00FF  // Magenta
            };

            for (int color : testColors) {
                float yPos = ColorFinderUtil.getRainbowYPositionForColor(color, rectY, rectHeight);
                System.out.printf("Color 0x%08X found at Y: %.2f%n", color, yPos);
            }
        }

        public static void testBilinearGradient() {
            System.out.println("\n=== Bilinear Gradient Test ===");

            float rectX = 100.0f, rectY = 50.0f;
            float rectWidth = 200.0f, rectHeight = 150.0f;

            int topLeft = 0xFFFF0000;    // Red
            int topRight = 0xFF0000FF;   // Blue
            int bottomLeft = 0xFF00FF00; // Green
            int bottomRight = 0xFFFFFF00; // Yellow

            // Test corner colors
            int[] cornerColors = {topLeft, topRight, bottomLeft, bottomRight};
            String[] cornerNames = {"TopLeft", "TopRight", "BottomLeft", "BottomRight"};

            for (int i = 0; i < cornerColors.length; i++) {
                ColorPosition pos = ColorFinderUtil.getBilinearPositionForColor(
                        cornerColors[i], rectX, rectY, rectWidth, rectHeight,
                        topLeft, topRight, bottomLeft, bottomRight);

                System.out.printf("%s (0x%08X): %s%n", cornerNames[i], cornerColors[i], pos);
            }

            // Test a mixed color
            int mixedColor = 0xFF808080; // Gray
            ColorPosition pos = ColorFinderUtil.getBilinearPositionForColor(
                    mixedColor, rectX, rectY, rectWidth, rectHeight,
                    topLeft, topRight, bottomLeft, bottomRight);

            System.out.printf("Mixed color (0x%08X): %s%n", mixedColor, pos);
        }
    }
}