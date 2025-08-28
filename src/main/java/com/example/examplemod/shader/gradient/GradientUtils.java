package com.example.examplemod.shader.gradient;

import com.example.examplemod.helper.DisplayUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class GradientUtils {

    /**
     * Renders a gradient rectangle with custom colors for each corner
     */
    public static void drawGradientRect(float x, float y, float width, float height,
                                        int topLeftColor, int topRightColor,
                                        int bottomLeftColor, int bottomRightColor) {

        if (!GradientShader.INSTANCE.created) return;

        // Save matrices and state
        FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
        FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrix);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Setup orthographic projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 1, 0, 1, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // Setup OpenGL state
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        // Convert screen coordinates to normalized coordinates (0-1)
        float normalizedX = x / screenWidth;
        float normalizedY = y / screenHeight;
        float normalizedWidth = width / screenWidth;
        float normalizedHeight = height / screenHeight;

        // Configure shader
        GradientShader shader = GradientShader.INSTANCE;
        shader.setPosition(normalizedX, normalizedY);
        shader.setSize(normalizedWidth, normalizedHeight);
        shader.setTopLeftColor(topLeftColor);
        shader.setTopRightColor(topRightColor);
        shader.setBottomLeftColor(bottomLeftColor);
        shader.setBottomRightColor(bottomRightColor);

        // Enable shader
        shader.enable();

        // Render fullscreen quad - the shader will handle positioning
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f, 1.0f);
        GL11.glEnd();

        shader.disable();

        // Restore matrices
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        projectionMatrix.rewind();
        GL11.glLoadMatrix(projectionMatrix);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        modelviewMatrix.rewind();
        GL11.glLoadMatrix(modelviewMatrix);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Restore OpenGL state
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL20.glUseProgram(0);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Convenience method - your original request: white top-left, black bottom corners, custom top-right
     */
    public static void drawGradientRect(float x, float y, float width, float height, int topRightColor) {
        drawGradientRect(x, y, width, height,
                0xFFFFFFFF, // White top-left
                topRightColor, // Custom top-right
                0xFF000000, // Black bottom-left
                0xFF000000); // Black bottom-right
    }

    /**
     * Percentage-based method with all four corners
     */
    public static void drawGradientRectPercent(float xPercent, float yPercent,
                                               float widthPercent, float heightPercent,
                                               int topLeftColor, int topRightColor,
                                               int bottomLeftColor, int bottomRightColor) {
        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        float x = xPercent * screenWidth;
        float y = yPercent * screenHeight;
        float width = widthPercent * screenWidth;
        float height = heightPercent * screenHeight;

        drawGradientRect(x, y, width, height, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
    }

    /**
     * Percentage-based method - original request format
     */
    public static void drawGradientRectPercent(float xPercent, float yPercent,
                                               float widthPercent, float heightPercent,
                                               int topRightColor) {
        drawGradientRectPercent(xPercent, yPercent, widthPercent, heightPercent,
                0xFFFFFFFF, topRightColor, 0xFF000000, 0xFF000000);
    }

    /**
     * Creates a rainbow gradient rectangle (vertical rainbow like in your image)
     */
    public static void drawRainbowRect(float x, float y, float width, float height) {
        drawGradientRect(x, y, width, height,
                0xFFFF0000, // Red top-left
                0xFFFF0000, // Red top-right
                0xFF00FF00, // Green bottom-left
                0xFF00FF00  // Green bottom-right
        );
    }

    /**
     * Creates a full spectrum rainbow gradient rectangle (more colors)
     */
    public static void drawFullRainbowRect(float x, float y, float width, float height) {
        drawGradientRect(x, y, width, height,
                0xFFFF00FF, // Magenta top-left
                0xFFFF0000, // Red top-right
                0xFF00FFFF, // Cyan bottom-left
                0xFF00FF00  // Green bottom-right
        );
    }

    /**
     * Creates a rainbow gradient with HSV color space simulation
     * Uses multiple segments to create a smoother rainbow effect
     */
    public static void drawSmoothRainbowRect(float x, float y, float width, float height) {
        float segmentHeight = height / 7.0f;

        // Red to Orange
        drawGradientRect(x, y, width, segmentHeight,
                0xFFFF0000, 0xFFFF0000, // Red top
                0xFFFF8000, 0xFFFF8000  // Orange bottom
        );

        // Orange to Yellow
        drawGradientRect(x, y + segmentHeight, width, segmentHeight,
                0xFFFF8000, 0xFFFF8000, // Orange top
                0xFFFFFF00, 0xFFFFFF00  // Yellow bottom
        );

        // Yellow to Green
        drawGradientRect(x, y + segmentHeight * 2, width, segmentHeight,
                0xFFFFFF00, 0xFFFFFF00, // Yellow top
                0xFF00FF00, 0xFF00FF00  // Green bottom
        );

        // Green to Cyan
        drawGradientRect(x, y + segmentHeight * 3, width, segmentHeight,
                0xFF00FF00, 0xFF00FF00, // Green top
                0xFF00FFFF, 0xFF00FFFF  // Cyan bottom
        );

        // Cyan to Blue
        drawGradientRect(x, y + segmentHeight * 4, width, segmentHeight,
                0xFF00FFFF, 0xFF00FFFF, // Cyan top
                0xFF0000FF, 0xFF0000FF  // Blue bottom
        );

        // Blue to Magenta
        drawGradientRect(x, y + segmentHeight * 5, width, segmentHeight,
                0xFF0000FF, 0xFF0000FF, // Blue top
                0xFFFF00FF, 0xFFFF00FF  // Magenta bottom
        );

        // Blue to Magenta
        drawGradientRect(x, y + segmentHeight * 6, width, segmentHeight,
                0xFFFF00FF, 0xFFFF00FF, // Blue top
                0xFFFF0000, 0xFFFF0000  // Magenta bottom
        );
    }

    /**
     * Percentage-based rainbow methods
     */
    public static void drawRainbowRectPercent(float xPercent, float yPercent,
                                              float widthPercent, float heightPercent) {
        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        float x = xPercent * screenWidth;
        float y = yPercent * screenHeight;
        float width = widthPercent * screenWidth;
        float height = heightPercent * screenHeight;

        drawSmoothRainbowRect(x, y, width, height);
    }

    /**
     * Creates a hue-based rainbow color at a specific position (0.0 to 1.0)
     */
    public static int getHueColor(float hue) {
        float h = hue * 6.0f;
        int i = (int)h;
        float f = h - i;

        int r = 0, g = 0, b = 0;

        switch (i % 6) {
            case 0: r = 255; g = (int)(255 * f); b = 0; break;          // Red to Yellow
            case 1: r = (int)(255 * (1 - f)); g = 255; b = 0; break;    // Yellow to Green
            case 2: r = 0; g = 255; b = (int)(255 * f); break;          // Green to Cyan
            case 3: r = 0; g = (int)(255 * (1 - f)); b = 255; break;    // Cyan to Blue
            case 4: r = (int)(255 * f); g = 0; b = 255; break;          // Blue to Magenta
            case 5: r = 255; g = 0; b = (int)(255 * (1 - f)); break;    // Magenta to Red
        }

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    //public static float getYPositionForColor(int targetColor, float rectY, float rectHeight) {
    //    // Remove alpha channel for comparison
    //    int targetRGB = targetColor & 0x00FFFFFF;
//
    //    float segmentHeight = rectHeight / 7.0f;
//
    //    // Define the gradient segments with their start and end colors
    //    int[][] segments = {
    //            {0xFF0000, 0xFF8000}, // Red to Orange
    //            {0xFF8000, 0xFFFF00}, // Orange to Yellow
    //            {0xFFFF00, 0x00FF00}, // Yellow to Green
    //            {0x00FF00, 0x00FFFF}, // Green to Cyan
    //            {0x00FFFF, 0x0000FF}, // Cyan to Blue
    //            {0x0000FF, 0xFF00FF}, // Blue to Magenta
    //            {0xFF00FF, 0xFF0000}  // Magenta to Red
    //    };
//
    //    // Check each segment
    //    for (int segment = 0; segment < segments.length; segment++) {
    //        int startColor = segments[segment][0];
    //        int endColor = segments[segment][1];
//
    //        // Extract RGB components for start color
    //        int startR = (startColor >> 16) & 0xFF;
    //        int startG = (startColor >> 8) & 0xFF;
    //        int startB = startColor & 0xFF;
//
    //        // Extract RGB components for end color
    //        int endR = (endColor >> 16) & 0xFF;
    //        int endG = (endColor >> 8) & 0xFF;
    //        int endB = endColor & 0xFF;
//
    //        // Extract RGB components for target color
    //        int targetR = (targetRGB >> 16) & 0xFF;
    //        int targetG = (targetRGB >> 8) & 0xFF;
    //        int targetB = targetRGB & 0xFF;
//
    //        // Check if color could be in this segment by examining if target values
    //        // are between start and end values for each component
    //        boolean couldBeInSegment = isValueInRange(targetR, startR, endR) &&
    //                isValueInRange(targetG, startG, endG) &&
    //                isValueInRange(targetB, startB, endB);
//
    //        if (couldBeInSegment) {
    //            // Find the interpolation factor (0.0 to 1.0) within this segment
    //            float t = findInterpolationFactor(startR, startG, startB,
    //                    endR, endG, endB,
    //                    targetR, targetG, targetB);
//
    //            if (t >= 0.0f && t <= 1.0f) {
    //                // Calculate Y position within this segment
    //                float segmentStartY = rectY + (segment * segmentHeight);
    //                return segmentStartY + (t * segmentHeight);
    //            }
    //        }
    //    }
//
    //    // If not found, return -1 or throw an exception
    //    return -1.0f;
    //}
//
    //private static boolean isValueInRange(int value, int start, int end) {
    //    int min = Math.min(start, end);
    //    int max = Math.max(start, end);
    //    return value >= min && value <= max;
    //}
//
    //private static float findInterpolationFactor(int startR, int startG, int startB,
    //                                             int endR, int endG, int endB,
    //                                             int targetR, int targetG, int targetB) {
    //    // Try to find t such that: target = start + t * (end - start)
    //    // We'll use the component with the largest difference for best accuracy
//
    //    int diffR = Math.abs(endR - startR);
    //    int diffG = Math.abs(endG - startG);
    //    int diffB = Math.abs(endB - startB);
//
    //    float t = 0.0f;
//
    //    if (diffR >= diffG && diffR >= diffB && diffR > 0) {
    //        // Use red component
    //        t = (float)(targetR - startR) / (endR - startR);
    //    } else if (diffG >= diffB && diffG > 0) {
    //        // Use green component
    //        t = (float)(targetG - startG) / (endG - startG);
    //    } else if (diffB > 0) {
    //        // Use blue component
    //        t = (float)(targetB - startB) / (endB - startB);
    //    }
//
    //    // Verify the interpolation is correct by checking all components
    //    int interpolatedR = startR + Math.round(t * (endR - startR));
    //    int interpolatedG = startG + Math.round(t * (endG - startG));
    //    int interpolatedB = startB + Math.round(t * (endB - startB));
//
    //    // Allow small tolerance for rounding errors
    //    int tolerance = 2;
    //    if (Math.abs(interpolatedR - targetR) <= tolerance &&
    //            Math.abs(interpolatedG - targetG) <= tolerance &&
    //            Math.abs(interpolatedB - targetB) <= tolerance) {
    //        return t;
    //    }
//
    //    return -1.0f; // Invalid interpolation
    //}
}