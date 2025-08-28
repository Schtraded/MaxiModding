package com.example.examplemod.shader.sdf;

import com.example.examplemod.helper.DisplayUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class SDFUtils {

    /**
     * Renders a rounded rectangle with individual corner control and border
     */
    public static void drawRoundedRectWithBorder(float x, float y, float width, float height,
                                                 float topLeft, float topRight, float bottomLeft, float bottomRight,
                                                 int fillColor, float borderWidth, int borderColor) {

        if (!SDFShader.INSTANCE.created) return;

        // Save OpenGL state
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Save matrices
        FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
        FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrix);

        // Set up orthographic projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 1, 0, 1, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // Configure OpenGL state
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        // Center of rectangle in normalized coordinates
        float centerX = x + width * 0.5f;
        float centerY = y + height * 0.5f;

        // Convert to normalized device coordinates
        float normalizedX = (2.0f * centerX - screenWidth) / screenHeight;
        float normalizedY = -((2.0f * centerY - screenHeight) / screenHeight);

        // Convert size to normalized coordinates
        float normalizedWidth = width / screenHeight;
        float normalizedHeight = height / screenHeight;

        // Convert corner radii to normalized space
        float normalizedTopLeft = topLeft / screenHeight;
        float normalizedTopRight = topRight / screenHeight;
        float normalizedBottomLeft = bottomLeft / screenHeight;
        float normalizedBottomRight = bottomRight / screenHeight;

        // Convert border width to normalized space
        float normalizedBorderWidth = borderWidth / screenHeight;

        // Extract fill color components
        float fillAlpha = ((fillColor >> 24) & 0xFF) / 255.0f;
        float fillRed = ((fillColor >> 16) & 0xFF) / 255.0f;
        float fillGreen = ((fillColor >> 8) & 0xFF) / 255.0f;
        float fillBlue = (fillColor & 0xFF) / 255.0f;

        // Extract border color components
        float borderAlpha = ((borderColor >> 24) & 0xFF) / 255.0f;
        float borderRed = ((borderColor >> 16) & 0xFF) / 255.0f;
        float borderGreen = ((borderColor >> 8) & 0xFF) / 255.0f;
        float borderBlue = (borderColor & 0xFF) / 255.0f;

        // Configure and enable shader
        SDFShader shader = SDFShader.INSTANCE;
        shader.setPosition(normalizedX, normalizedY);
        shader.setSize(normalizedWidth, normalizedHeight);
        shader.setCorners(normalizedTopRight, normalizedBottomRight, normalizedBottomLeft, normalizedTopLeft);
        shader.setColor(fillRed, fillGreen, fillBlue, fillAlpha);
        shader.setBorderColor(borderRed, borderGreen, borderBlue, borderAlpha);
        shader.setBorderWidth(normalizedBorderWidth);
        shader.setBackgroundColor(0.0f, 0.0f, 0.0f, 0.0f); // Transparent background

        shader.enable();

        // Render fullscreen quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f, 1.0f);
        GL11.glEnd();

        shader.disable();

        // Restore OpenGL state
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        projectionMatrix.rewind();
        GL11.glLoadMatrix(projectionMatrix);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        modelviewMatrix.rewind();
        GL11.glLoadMatrix(modelviewMatrix);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);

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
     * Renders a rounded rectangle with individual corner control (no border - existing method)
     */
    public static void drawRoundedRect(float x, float y, float width, float height,
                                       float topLeft, float topRight, float bottomLeft, float bottomRight,
                                       int color) {
        drawRoundedRectWithBorder(x, y, width, height, topLeft, topRight, bottomLeft, bottomRight, color, 0.0f, 0);
    }

    /**
     * Renders a rounded rectangle with uniform corner radius and border
     */
    public static void drawRoundedRectWithBorder(float x, float y, float width, float height,
                                                 float radius, int fillColor, float borderWidth, int borderColor) {
        drawRoundedRectWithBorder(x, y, width, height, radius, radius, radius, radius, fillColor, borderWidth, borderColor);
    }

    /**
     * Renders a rounded rectangle with uniform corner radius (no border - existing method)
     */
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        drawRoundedRect(x, y, width, height, radius, radius, radius, radius, color);
    }

    /**
     * Renders a regular rectangle with border
     */
    public static void drawRectWithBorder(float x, float y, float width, float height,
                                          int fillColor, float borderWidth, int borderColor) {
        drawRoundedRectWithBorder(x, y, width, height, 0, 0, 0, 0, fillColor, borderWidth, borderColor);
    }

    /**
     * Renders a regular rectangle (no rounded corners, no border - existing method)
     */
    public static void drawRect(float x, float y, float width, float height, int color) {
        drawRoundedRect(x, y, width, height, 0, 0, 0, 0, color);
    }

    /**
     * Renders a perfect circle with border
     */
    public static void drawCircleWithBorder(float x, float y, float radius, int fillColor, float borderWidth, int borderColor) {
        float diameter = radius * 2;
        float squareX = x - radius;
        float squareY = y - radius;
        drawRoundedRectWithBorder(squareX, squareY, diameter, diameter, diameter, fillColor, borderWidth, borderColor);
    }

    /**
     * Renders a perfect circle (no border - existing method)
     */
    public static void drawCircle(float x, float y, float radius, int color) {
        drawCircleWithBorder(x, y, radius, color, 0.0f, 0);
    }

    // Percentage-based methods with border support
    public static void drawRoundedRectPercentWithBorder(float xPercent, float yPercent,
                                                        float widthPercent, float heightPercent,
                                                        float radiusPercent, int fillColor,
                                                        float borderWidthPercent, int borderColor) {
        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        float x = xPercent * screenWidth;
        float y = yPercent * screenHeight;
        float width = widthPercent * screenWidth;
        float height = heightPercent * screenHeight;
        float radius = radiusPercent * screenHeight;
        float borderWidth = borderWidthPercent * screenHeight;

        drawRoundedRectWithBorder(x, y, width, height, radius, fillColor, borderWidth, borderColor);
    }

    public static void drawRoundedRectPercentWithBorder(float xPercent, float yPercent,
                                                        float widthPercent, float heightPercent,
                                                        float topLeftPercent, float topRightPercent,
                                                        float bottomLeftPercent, float bottomRightPercent,
                                                        int fillColor, float borderWidthPercent, int borderColor) {
        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        float x = xPercent * screenWidth;
        float y = yPercent * screenHeight;
        float width = widthPercent * screenWidth;
        float height = heightPercent * screenHeight;
        float topLeft = topLeftPercent * screenHeight;
        float topRight = topRightPercent * screenHeight;
        float bottomLeft = bottomLeftPercent * screenHeight;
        float bottomRight = bottomRightPercent * screenHeight;
        float borderWidth = borderWidthPercent * screenHeight;

        drawRoundedRectWithBorder(x, y, width, height, topLeft, topRight, bottomLeft, bottomRight, fillColor, borderWidth, borderColor);
    }

    // Existing percentage-based methods (no border)
    public static void drawRoundedRectPercent(float xPercent, float yPercent,
                                              float widthPercent, float heightPercent,
                                              float radiusPercent, int color) {
        drawRoundedRectPercentWithBorder(xPercent, yPercent, widthPercent, heightPercent, radiusPercent, color, 0.0f, 0);
    }

    public static void drawRoundedRectPercent(float xPercent, float yPercent,
                                              float widthPercent, float heightPercent,
                                              float topLeftPercent, float topRightPercent,
                                              float bottomLeftPercent, float bottomRightPercent,
                                              int color) {
        drawRoundedRectPercentWithBorder(xPercent, yPercent, widthPercent, heightPercent,
                topLeftPercent, topRightPercent, bottomLeftPercent, bottomRightPercent,
                color, 0.0f, 0);
    }

    public static void drawRectPercent(float xPercent, float yPercent,
                                       float widthPercent, float heightPercent, int color) {
        drawRoundedRectPercent(xPercent, yPercent, widthPercent, heightPercent, 0.0f, color);
    }

    public static void drawCirclePercentWithBorder(float xPercent, float yPercent, float radiusPercent,
                                                   int fillColor, float borderWidthPercent, int borderColor) {
        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        float x = xPercent * screenWidth;
        float y = yPercent * screenHeight;
        float radius = radiusPercent * screenHeight;
        float borderWidth = borderWidthPercent * screenHeight;

        drawCircleWithBorder(x, y, radius, fillColor, borderWidth, borderColor);
    }

    public static void drawCirclePercent(float xPercent, float yPercent, float radiusPercent, int color) {
        drawCirclePercentWithBorder(xPercent, yPercent, radiusPercent, color, 0.0f, 0);
    }
}