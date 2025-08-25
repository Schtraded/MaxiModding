package com.example.examplemod.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class TriangleRenderer {

    /**
     * Draws a filled triangle using three points
     * @param x1 First vertex X coordinate
     * @param y1 First vertex Y coordinate
     * @param x2 Second vertex X coordinate
     * @param y2 Second vertex Y coordinate
     * @param x3 Third vertex X coordinate
     * @param y3 Third vertex Y coordinate
     * @param color Color in ARGB format (0xAARRGGBB)
     */
    public static void drawFilledTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        // Extract color components
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        // Save current state
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(x1, y1, 0.0D).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(x2, y2, 0.0D).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(x3, y3, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        // Restore state
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Draws a triangle outline using lines
     * @param x1 First vertex X coordinate
     * @param y1 First vertex Y coordinate
     * @param x2 Second vertex X coordinate
     * @param y2 Second vertex Y coordinate
     * @param x3 Third vertex X coordinate
     * @param y3 Third vertex Y coordinate
     * @param width Line width
     * @param color Color in ARGB format (0xAARRGGBB)
     */
    public static void drawTriangleOutline(float x1, float y1, float x2, float y2, float x3, float y3, float width, int color) {
        // Use your existing line drawing utility
        LineDrawingUtils.drawLine(x1, y1, x2, y2, width, color);
        LineDrawingUtils.drawLine(x2, y2, x3, y3, width, color);
        LineDrawingUtils.drawLine(x3, y3, x1, y1, width, color);
    }

    /**
     * Draws both filled triangle and outline
     */
    public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int fillColor, int outlineColor, float outlineWidth) {
        drawFilledTriangle(x1, y1, x2, y2, x3, y3, fillColor);
        drawTriangleOutline(x1, y1, x2, y2, x3, y3, outlineWidth, outlineColor);
    }

    /**
     * Creates an equilateral triangle centered at a point
     * @param centerX Center X coordinate
     * @param centerY Center Y coordinate
     * @param size Size (distance from center to vertex)
     * @param rotation Rotation in degrees (0 = pointing up)
     * @param color Color in ARGB format
     */
    public static void drawEquilateralTriangle(float centerX, float centerY, float size, float rotation, int color) {
        double rad = Math.toRadians(rotation);

        // Calculate the three vertices of an equilateral triangle
        float x1 = centerX + (float)(size * Math.cos(rad));
        float y1 = centerY + (float)(size * Math.sin(rad));

        float x2 = centerX + (float)(size * Math.cos(rad + 2 * Math.PI / 3));
        float y2 = centerY + (float)(size * Math.sin(rad + 2 * Math.PI / 3));

        float x3 = centerX + (float)(size * Math.cos(rad + 4 * Math.PI / 3));
        float y3 = centerY + (float)(size * Math.sin(rad + 4 * Math.PI / 3));

        drawFilledTriangle(x1, y1, x2, y2, x3, y3, color);
    }

    /**
     * Creates a right triangle (useful for arrows, UI elements)
     * @param x Base X coordinate (bottom-left corner)
     * @param y Base Y coordinate (bottom-left corner)
     * @param width Triangle width
     * @param height Triangle height
     * @param color Color in ARGB format
     */
    public static void drawRightTriangle(float x, float y, float width, float height, int color) {
        drawFilledTriangle(x, y, x + width, y, x, y - height, color);
    }

    /**
     * Creates an isosceles triangle pointing up
     * @param centerX Center X coordinate of the base
     * @param baseY Y coordinate of the base
     * @param baseWidth Width of the base
     * @param height Height of the triangle
     * @param color Color in ARGB format
     */
    public static void drawUpTriangle(float centerX, float baseY, float baseWidth, float height, int color) {
        float halfBase = baseWidth / 2.0f;
        drawFilledTriangle(
                centerX - halfBase, baseY,        // Bottom left
                centerX + halfBase, baseY,        // Bottom right
                centerX, baseY - height,          // Top point
                color
        );
    }

    /**
     * Creates an isosceles triangle pointing down
     */
    public static void drawDownTriangle(float centerX, float topY, float baseWidth, float height, int color) {
        float halfBase = baseWidth / 2.0f;
        drawFilledTriangle(
                centerX - halfBase, topY,         // Top left
                centerX + halfBase, topY,         // Top right
                centerX, topY + height,           // Bottom point
                color
        );
    }

    /**
     * Creates an isosceles triangle pointing left
     */
    public static void drawLeftTriangle(float rightX, float centerY, float baseHeight, float width, int color) {
        float halfBase = baseHeight / 2.0f;
        drawFilledTriangle(
                rightX, centerY - halfBase,       // Top right
                rightX, centerY + halfBase,       // Bottom right
                rightX - width, centerY,          // Left point
                color
        );
    }

    /**
     * Creates an isosceles triangle pointing right
     */
    //public static void drawRightTriangle(float leftX, float centerY, float baseHeight, float width, int color) {
    //    float halfBase = baseHeight / 2.0f;
    //    drawFilledTriangle(
    //            leftX, centerY - halfBase,        // Top left
    //            leftX, centerY + halfBase,        // Bottom left
    //            leftX + width, centerY,           // Right point
    //            color
    //    );
    //}

    /**
     * Alternative filled triangle method using direct OpenGL (if Tessellator has issues)
     */
    public static void drawFilledTriangleGL(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        // Extract color components
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        // Save current matrix state
        GL11.glPushMatrix();

        // Enable blending and disable texture - same as your working line code
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);
        // Set color
        GL11.glColor4f(red, green, blue, alpha);

        // Draw the triangle
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x3, y3);
        GL11.glEnd();

        // Reset state - same order as your line code
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    /**
     * Debug method to test triangle rendering - draws various triangle types
     */
    public static void drawTestTriangles(int startX, int startY) {
        // Basic triangle
        drawFilledTriangle(startX, startY, startX + 50, startY + 50, startX + 25, startY - 30, 0x80FF0000);

        // Equilateral triangle
        drawEquilateralTriangle(startX + 100, startY, 30, 0, 0x8000FF00);

        // Right triangle
        drawRightTriangle(startX + 150, startY + 50, 40, 40, 0x800000FF);

        // Up arrow triangle
        drawUpTriangle(startX + 220, startY + 50, 30, 25, 0x80FFFF00);

        // Down arrow triangle
        drawDownTriangle(startX + 270, startY - 20, 30, 25, 0x80FF00FF);

        // Triangle with outline
        drawTriangle(startX + 320, startY, startX + 370, startY + 40, startX + 340, startY - 25,
                0x8000FFFF, 0xFF000000, 2.0f);
    }
}