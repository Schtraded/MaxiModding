package com.example.examplemod.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class QuadDrawingUtils {

    /**
     * Draws a rect between four points using OpenGL
     * @param x1 Starting X coordinate
     * @param y1 Starting Y coordinate
     * @param x2 Second X coordinate
     * @param y2 Second Y coordinate
     * @param x3 Third X coordinate
     * @param y3 Third Y coordinate
     * @param x4 Ending X coordinate
     * @param y4 Ending Y coordinate
     * @param color Color in ARGB format (0xAARRGGBB)
     */
    public static void drawQuad(
            float x1, float y1,
            float x2, float y2,
            float x3, float y3,
            float x4, float y4,
            int color) {
        // Extract color components
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        // Save current GL state
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Proper state setup for transparent rendering
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        // Don't disable depth test completely - instead manage it properly
        GL11.glDepthMask(false); // Disable depth writing but keep depth testing

        GL11.glColor4f(red, green, blue, alpha);

        // Draw the rect
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x3, y3);
        GL11.glVertex2f(x4, y4);
        GL11.glEnd();

        // Restore GL state completely
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Draws a horizontal rect
     */
    public static void drawRect(float x, float y, float width, float height, int color) {
        drawQuad(
                x, y,
                x, y + height,
                x + width, y + height,
                x + width, y,
                color
        );
    }

    /**
     * Draws an anti-aliased rect (smoother but potentially slower)
     */
    //public static void drawSmoothRect(float x1, float y1, float x2, float y2, float width, int color) {
    //    // Extract color components
    //    float alpha = ((color >> 24) & 0xFF) / 255.0f;
    //    float red = ((color >> 16) & 0xFF) / 255.0f;
    //    float green = ((color >> 8) & 0xFF) / 255.0f;
    //    float blue = (color & 0xFF) / 255.0f;
//
    //    // Enable anti-aliasing and blending
    //    GL11.glEnable(GL11.GL_BLEND);
    //    GL11.glDisable(GL11.GL_TEXTURE_2D);
    //    GL11.glEnable(GL11.GL_LINE_SMOOTH);
    //    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    //    GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
//
    //    // Set line width and color
    //    GL11.glLineWidth(width);
    //    GL11.glColor4f(red, green, blue, alpha);
//
    //    // Draw the line
    //    GL11.glBegin(GL11.GL_LINES);
    //    GL11.glVertex2f(x1, y1);
    //    GL11.glVertex2f(x2, y2);
    //    GL11.glEnd();
//
    //    // Reset state
    //    GL11.glDisable(GL11.GL_LINE_SMOOTH);
    //    GL11.glEnable(GL11.GL_TEXTURE_2D);
    //    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    //    GL11.glLineWidth(1.0f);
    //}
}


