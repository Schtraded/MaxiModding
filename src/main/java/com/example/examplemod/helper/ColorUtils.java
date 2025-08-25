package com.example.examplemod.helper;

/**
 * Comprehensive color utility class with validation, conversion, and manipulation methods
 */
public class ColorUtils {

    // Color constants for common usage
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int TRANSPARENT = 0x00000000;
    public static final int RED = 0xFFFF0000;
    public static final int GREEN = 0xFF00FF00;
    public static final int BLUE = 0xFF0000FF;

    /**
     * Creates a color integer from RGBA values (0-255)
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @param alpha Alpha component (0-255)
     * @return ARGB color integer
     * @throws IllegalArgumentException if any component is out of range
     */
    public static int createColor(int red, int green, int blue, int alpha) {
        validateColorComponent(red, "Red");
        validateColorComponent(green, "Green");
        validateColorComponent(blue, "Blue");
        validateColorComponent(alpha, "Alpha");

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Creates a color integer from RGB values with full opacity (0-255)
     */
    public static int createColor(int red, int green, int blue) {
        return createColor(red, green, blue, 255);
    }

    /**
     * Creates a color integer from RGBA values (0.0-1.0)
     * @param red Red component (0.0-1.0)
     * @param green Green component (0.0-1.0)
     * @param blue Blue component (0.0-1.0)
     * @param alpha Alpha component (0.0-1.0)
     * @return ARGB color integer
     * @throws IllegalArgumentException if any component is out of range
     */
    public static int createColor(float red, float green, float blue, float alpha) {
        validateFloatComponent(red, "Red");
        validateFloatComponent(green, "Green");
        validateFloatComponent(blue, "Blue");
        validateFloatComponent(alpha, "Alpha");

        return createColor(
                Math.round(red * 255),
                Math.round(green * 255),
                Math.round(blue * 255),
                Math.round(alpha * 255)
        );
    }

    /**
     * Creates a color integer from RGB values with full opacity (0.0-1.0)
     */
    public static int createColor(float red, float green, float blue) {
        return createColor(red, green, blue, 1.0f);
    }

    /**
     * Creates a color from a hex string
     * Supports formats: #RGB, #RRGGBB, #ARGB, #AARRGGBB, RGB, RRGGBB, ARGB, AARRGGBB
     * @param hex Hex color string
     * @return ARGB color integer
     * @throws IllegalArgumentException if hex format is invalid
     */
    public static int createColorFromHex(String hex) {
        if (hex == null || hex.trim().isEmpty()) {
            throw new IllegalArgumentException("Hex color string cannot be null or empty");
        }

        // Normalize: remove '#' and whitespace
        hex = hex.trim().replace("#", "").toUpperCase();

        // Validate hex characters
        if (!hex.matches("[0-9A-F]+")) {
            throw new IllegalArgumentException("Hex color contains invalid characters: " + hex);
        }

        try {
            switch (hex.length()) {
                case 3: // RGB → RRGGBB
                    return createColorFromShortHex(hex, true);
                case 4: // ARGB → AARRGGBB
                    return createColorFromShortHex(hex, false);
                case 6: // RRGGBB
                    return (0xFF << 24) | Integer.parseInt(hex, 16);
                case 8: // AARRGGBB
                    return (int) Long.parseLong(hex, 16);
                default:
                    throw new IllegalArgumentException("Hex color must be 3, 4, 6, or 8 characters long, got: " + hex.length());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color format: " + hex, e);
        }
    }

    /**
     * Extracts the alpha component from a color (0-255)
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }

    /**
     * Extracts the red component from a color (0-255)
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    /**
     * Extracts the green component from a color (0-255)
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    /**
     * Extracts the blue component from a color (0-255)
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }

    /**
     * Sets the alpha component of a color
     * @param color Original color
     * @param alpha New alpha value (0-255)
     * @return Color with new alpha
     */
    public static int setAlpha(int color, int alpha) {
        validateColorComponent(alpha, "Alpha");
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    /**
     * Sets the alpha component of a color (0.0-1.0)
     */
    public static int setAlpha(int color, float alpha) {
        validateFloatComponent(alpha, "Alpha");
        return setAlpha(color, Math.round(alpha * 255));
    }

    /**
     * Converts color to hex string (AARRGGBB format)
     */
    public static String toHex(int color) {
        return String.format("#%08X", color);
    }

    /**
     * Converts color to hex string (RRGGBB format, ignoring alpha)
     */
    public static String toHexRGB(int color) {
        return String.format("#%06X", color & 0x00FFFFFF);
    }

    /**
     * Linearly interpolates between two colors
     * @param color1 Start color
     * @param color2 End color
     * @param factor Interpolation factor (0.0-1.0)
     * @return Interpolated color
     */
    public static int lerp(int color1, int color2, float factor) {
        validateFloatComponent(factor, "Factor");

        int a1 = getAlpha(color1), r1 = getRed(color1), g1 = getGreen(color1), b1 = getBlue(color1);
        int a2 = getAlpha(color2), r2 = getRed(color2), g2 = getGreen(color2), b2 = getBlue(color2);

        int a = Math.round(a1 + (a2 - a1) * factor);
        int r = Math.round(r1 + (r2 - r1) * factor);
        int g = Math.round(g1 + (g2 - g1) * factor);
        int b = Math.round(b1 + (b2 - b1) * factor);

        return createColor(r, g, b, a);
    }

    /**
     * Creates a darker version of the color
     * @param color Original color
     * @param factor Darkening factor (0.0-1.0, where 0.0 = black, 1.0 = unchanged)
     * @return Darker color
     */
    public static int darken(int color, float factor) {
        validateFloatComponent(factor, "Factor");

        int alpha = getAlpha(color);
        int red = Math.round(getRed(color) * factor);
        int green = Math.round(getGreen(color) * factor);
        int blue = Math.round(getBlue(color) * factor);

        return createColor(red, green, blue, alpha);
    }

    /**
     * Creates a lighter version of the color
     * @param color Original color
     * @param factor Lightening factor (0.0-1.0)
     * @return Lighter color
     */
    public static int lighten(int color, float factor) {
        validateFloatComponent(factor, "Factor");

        int alpha = getAlpha(color);
        int red = Math.round(getRed(color) + (255 - getRed(color)) * factor);
        int green = Math.round(getGreen(color) + (255 - getGreen(color)) * factor);
        int blue = Math.round(getBlue(color) + (255 - getBlue(color)) * factor);

        return createColor(red, green, blue, alpha);
    }

    /**
     * Creates a color with modified saturation
     * @param color Original color
     * @param saturation New saturation (0.0 = grayscale, 1.0 = original, >1.0 = more saturated)
     * @return Color with modified saturation
     */
    public static int setSaturation(int color, float saturation) {
        float[] hsb = rgbToHsb(getRed(color), getGreen(color), getBlue(color));
        hsb[1] = Math.max(0, Math.min(1, hsb[1] * saturation));
        int[] rgb = hsbToRgb(hsb[0], hsb[1], hsb[2]);
        return createColor(rgb[0], rgb[1], rgb[2], getAlpha(color));
    }

    /**
     * Converts RGB to HSB color space
     */
    public static float[] rgbToHsb(int red, int green, int blue) {
        float r = red / 255.0f;
        float g = green / 255.0f;
        float b = blue / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0;
        if (delta != 0) {
            if (max == r) h = ((g - b) / delta) % 6;
            else if (max == g) h = (b - r) / delta + 2;
            else h = (r - g) / delta + 4;
            h *= 60;
            if (h < 0) h += 360;
        }

        float s = max == 0 ? 0 : delta / max;
        float brightness = max;

        return new float[]{h / 360f, s, brightness};
    }

    /**
     * Converts HSB to RGB color space
     */
    public static int[] hsbToRgb(float hue, float saturation, float brightness) {
        hue = Math.max(0, Math.min(1, hue)) * 360;
        saturation = Math.max(0, Math.min(1, saturation));
        brightness = Math.max(0, Math.min(1, brightness));

        float c = brightness * saturation;
        float x = c * (1 - Math.abs(((hue / 60) % 2) - 1));
        float m = brightness - c;

        float r, g, b;
        if (hue < 60) { r = c; g = x; b = 0; }
        else if (hue < 120) { r = x; g = c; b = 0; }
        else if (hue < 180) { r = 0; g = c; b = x; }
        else if (hue < 240) { r = 0; g = x; b = c; }
        else if (hue < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }

        return new int[]{
                Math.round((r + m) * 255),
                Math.round((g + m) * 255),
                Math.round((b + m) * 255)
        };
    }

    // Helper methods
    private static void validateColorComponent(int component, String name) {
        if (component < 0 || component > 255) {
            throw new IllegalArgumentException(name + " component must be between 0 and 255, got: " + component);
        }
    }

    private static void validateFloatComponent(float component, String name) {
        if (component < 0.0f || component > 1.0f) {
            throw new IllegalArgumentException(name + " component must be between 0.0 and 1.0, got: " + component);
        }
    }

    private static int createColorFromShortHex(String hex, boolean isRGB) {
        StringBuilder expanded = new StringBuilder();
        for (char c : hex.toCharArray()) {
            expanded.append(c).append(c);
        }

        if (isRGB) {
            // RGB → add full alpha
            return (0xFF << 24) | Integer.parseInt(expanded.toString(), 16);
        } else {
            // ARGB → already has alpha
            return (int) Long.parseLong(expanded.toString(), 16);
        }
    }
}
