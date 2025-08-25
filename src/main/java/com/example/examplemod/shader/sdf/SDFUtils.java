package com.example.examplemod.shader.sdf;

import com.example.examplemod.helper.DisplayUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15; // Add this import
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class SDFUtils {

    /**
     * Renders a rounded rectangle with individual corner control
     */
    public static void drawRoundedRect(float x, float y, float width, float height,
                                       float topLeft, float topRight, float bottomLeft, float bottomRight,
                                       int color) {

        if (!SDFShader.INSTANCE.created) return;

        // Save matrices
        FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
        FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrix);

        // Save current OpenGL state more comprehensively
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);


        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 1, 0, 1, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        //// Save critical state that might be affected
        //int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        //int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        //int currentArrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING); // Fixed
        //int currentElementBuffer = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING); // Fixed
//
        //// Save current color
        //FloatBuffer currentColor = BufferUtils.createFloatBuffer(16);
        //GL11.glGetFloat(GL11.GL_CURRENT_COLOR, currentColor);
//
        //// Save current blend function
        //int srcBlend = GL11.glGetInteger(GL11.GL_BLEND_SRC);
        //int dstBlend = GL11.glGetInteger(GL11.GL_BLEND_DST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Save matrices
        //FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
        //FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
        //GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        //GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrix);

            float screenWidth = DisplayUtils.scaledWidth;
            float screenHeight = DisplayUtils.scaledHeight;

            // Center of rectangle in normalized coordinates
            float centerX = x + width * 0.5f;
            float centerY = y + height * 0.5f;

            // Convert to normalized device coordinates
            //float normalizedX = (2.0f * centerX / screenWidth - 1.0f);
            //float normalizedY = -(2.0f * centerY / screenHeight - 1.0f); // Flip Y axis

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

            // Extract color components
            float alpha = ((color >> 24) & 0xFF) / 255.0f;
            float red = ((color >> 16) & 0xFF) / 255.0f;
            float green = ((color >> 8) & 0xFF) / 255.0f;
            float blue = (color & 0xFF) / 255.0f;

            // Configure and enable shader
            SDFShader shader = SDFShader.INSTANCE;
            shader.setPosition(normalizedX, normalizedY);
            shader.setSize(normalizedWidth, normalizedHeight);
            shader.setCorners(normalizedTopRight, normalizedBottomRight, normalizedBottomLeft, normalizedTopLeft);
            shader.setColor(red, green, blue, alpha);
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
            // CRITICAL: Restore state in proper order

            // First, restore matrices
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            projectionMatrix.rewind();
            GL11.glLoadMatrix(projectionMatrix);

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            modelviewMatrix.rewind();
            GL11.glLoadMatrix(modelviewMatrix);

            // Restore matrix mode to what it was (typically modelview)
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
     * Renders a rounded rectangle with uniform corner radius
     */
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        drawRoundedRect(x, y, width, height, radius, radius, radius, radius, color);
    }

    /**
     * Renders a regular rectangle (no rounded corners)
     */
    public static void drawRect(float x, float y, float width, float height, int color) {
        drawRoundedRect(x, y, width, height, 0, 0, 0, 0, color);
    }

    /**
     * Renders a perfect circle by creating a square with 50% corner radius
     */
    public static void drawCircle(float x, float y, float radius, int color) {
        float diameter = radius * 2;
        float squareX = x - radius;
        float squareY = y - radius;
        drawRoundedRect(squareX, squareY, diameter, diameter, diameter, color);
    }

    // Percentage-based methods
    public static void drawRoundedRectPercent(float xPercent, float yPercent,
                                              float widthPercent, float heightPercent,
                                              float radiusPercent, int color) {
        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        float x = xPercent * screenWidth;
        float y = yPercent * screenHeight;
        float width = widthPercent * screenWidth;
        float height = heightPercent * screenHeight;
        float radius = radiusPercent * screenHeight;

        drawRoundedRect(x, y, width, height, radius, color);
    }

    public static void drawRoundedRectPercent(float xPercent, float yPercent,
                                              float widthPercent, float heightPercent,
                                              float topLeftPercent, float topRightPercent,
                                              float bottomLeftPercent, float bottomRightPercent,
                                              int color) {
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

        drawRoundedRect(x, y, width, height, topLeft, topRight, bottomLeft, bottomRight, color);
    }

    public static void drawRectPercent(float xPercent, float yPercent,
                                       float widthPercent, float heightPercent, int color) {
        drawRoundedRectPercent(xPercent, yPercent, widthPercent, heightPercent, 0.0f, color);
    }

    public static void drawCirclePercent(float xPercent, float yPercent, float radiusPercent, int color) {
        float screenWidth = DisplayUtils.scaledWidth;
        float screenHeight = DisplayUtils.scaledHeight;

        float x = xPercent * screenWidth;
        float y = yPercent * screenHeight;
        float radius = radiusPercent * screenHeight;

        drawCircle(x, y, radius, color);
    }
}