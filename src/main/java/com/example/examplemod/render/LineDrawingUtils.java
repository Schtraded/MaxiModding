package com.example.examplemod.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class LineDrawingUtils {

    /**
     * Draws a line between two points using OpenGL
     * @param x1 Starting X coordinate
     * @param y1 Starting Y coordinate
     * @param x2 Ending X coordinate
     * @param y2 Ending Y coordinate
     * @param width Line width in pixels
     * @param color Color in ARGB format (0xAARRGGBB)
     */
    public static void drawLine(float x1, float y1, float x2, float y2, float width, int color) {
        // Extract color components
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        // Enable blending and disable texture
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set line width and color
        GL11.glLineWidth(width);
        GL11.glColor4f(red, green, blue, alpha);

        // Draw the line
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();

        // Reset state
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(1.0f);
    }

    /**
     * Draws a line with default width of 1 pixel
     */
    public static void drawLine(float x1, float y1, float x2, float y2, int color) {
        drawLine(x1, y1, x2, y2, 1.0f, color);
    }

    /**
     * Draws a horizontal line
     */
    public static void drawHorizontalLine(float x, float y, float length, float width, int color) {
        drawLine(x, y, x + length, y, width, color);
    }

    /**
     * Draws a vertical line
     */
    public static void drawVerticalLine(float x, float y, float length, float width, int color) {
        drawLine(x, y, x, y + length, width, color);
    }

    /**
     * Draws an anti-aliased line (smoother but potentially slower)
     */
    public static void drawSmoothLine(float x1, float y1, float x2, float y2, float width, int color) {
        // Extract color components
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        // Enable anti-aliasing and blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        // Set line width and color
        GL11.glLineWidth(width);
        GL11.glColor4f(red, green, blue, alpha);

        // Draw the line
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();

        // Reset state
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(1.0f);
    }
}

//package com.example.examplemod.render;
//
//        import net.minecraft.client.gui.Gui;
//        import net.minecraft.client.renderer.GlStateManager;
//        import net.minecraft.client.renderer.Tessellator;
//        import net.minecraft.client.renderer.WorldRenderer;
//        import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//        import org.lwjgl.opengl.GL11;

//public class LineDrawingUtils {
//
//    /**
//     * Draws a line using Minecraft's Tessellator system - most reliable method
//     */
//    public static void drawLine(float x1, float y1, float x2, float y2, float width, int color) {
//        // Extract color components
//        float alpha = ((color >> 24) & 0xFF) / 255.0f;
//        float red = ((color >> 16) & 0xFF) / 255.0f;
//        float green = ((color >> 8) & 0xFF) / 255.0f;
//        float blue = (color & 0xFF) / 255.0f;
//
//        // Save current state
//        GlStateManager.pushMatrix();
//        GlStateManager.enableBlend();
//        GlStateManager.disableTexture2D();
//        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
//
//        // Set line width
//        GL11.glLineWidth(width);
//
//        Tessellator tessellator = Tessellator.getInstance();
//        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
//
//        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//        worldRenderer.pos(x1, y1, 0.0D).color(red, green, blue, alpha).endVertex();
//        worldRenderer.pos(x2, y2, 0.0D).color(red, green, blue, alpha).endVertex();
//        tessellator.draw();
//
//        // Restore state
//        GL11.glLineWidth(1.0f);
//        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
//        GlStateManager.popMatrix();
//    }
//
//    /**
//     * Alternative method using rectangles - guaranteed to work
//     */
//    public static void drawLineRect(float x1, float y1, float x2, float y2, float width, int color) {
//        if (x1 == x2 && y1 == y2) {
//            // Single point - draw small rectangle
//            Gui.drawRect((int)(x1 - width/2), (int)(y1 - width/2),
//                    (int)(x1 + width/2), (int)(y1 + width/2), color);
//            return;
//        }
//
//        // Calculate line properties
//        float dx = x2 - x1;
//        float dy = y2 - y1;
//        float length = (float)Math.sqrt(dx * dx + dy * dy);
//
//        if (length == 0) return;
//
//        // Normalize direction
//        float unitX = dx / length;
//        float unitY = dy / length;
//
//        // Calculate perpendicular for width
//        float perpX = -unitY * width / 2.0f;
//        float perpY = unitX * width / 2.0f;
//
//        // Use multiple small rectangles to approximate the line
//        int steps = (int)Math.ceil(length);
//        for (int i = 0; i <= steps; i++) {
//            float t = (float)i / steps;
//            float x = x1 + dx * t;
//            float y = y1 + dy * t;
//
//            Gui.drawRect((int)(x - width/2), (int)(y - width/2),
//                    (int)(x + width/2), (int)(y + width/2), color);
//        }
//    }
//
//    /**
//     * Pixel-perfect line using Bresenham's algorithm
//     */
//    public static void drawPixelLine(int x1, int y1, int x2, int y2, int color) {
//        int dx = Math.abs(x2 - x1);
//        int dy = Math.abs(y2 - y1);
//        int sx = x1 < x2 ? 1 : -1;
//        int sy = y1 < y2 ? 1 : -1;
//        int err = dx - dy;
//
//        int x = x1;
//        int y = y1;
//
//        while (true) {
//            // Draw pixel
//            Gui.drawRect(x, y, x + 1, y + 1, color);
//
//            if (x == x2 && y == y2) break;
//
//            int e2 = 2 * err;
//            if (e2 > -dy) {
//                err -= dy;
//                x += sx;
//            }
//            if (e2 < dx) {
//                err += dx;
//                y += sy;
//            }
//        }
//    }
//
//    /**
//     * Thick line using multiple parallel thin lines
//     */
//    public static void drawThickLine(int x1, int y1, int x2, int y2, int thickness, int color) {
//        if (thickness <= 1) {
//            drawPixelLine(x1, y1, x2, y2, color);
//            return;
//        }
//
//        // Calculate perpendicular offset
//        float dx = x2 - x1;
//        float dy = y2 - y1;
//        float length = (float)Math.sqrt(dx * dx + dy * dy);
//
//        if (length == 0) {
//            Gui.drawRect(x1 - thickness/2, y1 - thickness/2,
//                    x1 + thickness/2, y1 + thickness/2, color);
//            return;
//        }
//
//        float perpX = -dy / length;
//        float perpY = dx / length;
//
//        // Draw multiple parallel lines
//        for (int i = -thickness/2; i <= thickness/2; i++) {
//            int offsetX = (int)(perpX * i);
//            int offsetY = (int)(perpY * i);
//            drawPixelLine(x1 + offsetX, y1 + offsetY, x2 + offsetX, y2 + offsetY, color);
//        }
//    }
//
//    /**
//     * Simple horizontal line
//     */
//    public static void drawHorizontalLine(int x, int y, int length, int thickness, int color) {
//        Gui.drawRect(x, y - thickness/2, x + length, y + thickness/2 + 1, color);
//    }
//
//    /**
//     * Simple vertical line
//     */
//    public static void drawVerticalLine(int x, int y, int length, int thickness, int color) {
//        Gui.drawRect(x - thickness/2, y, x + thickness/2 + 1, y + length, color);
//    }
//
//    /**
//     * Debug method to test line drawing - draws multiple test lines
//     */
//    public static void drawTestLines(int startX, int startY) {
//        // Test different methods
//        drawHorizontalLine(startX, startY, 100, 2, 0xFFFF0000); // Red horizontal
//        drawVerticalLine(startX, startY + 20, 50, 2, 0xFF00FF00); // Green vertical
//        drawPixelLine(startX, startY + 80, startX + 100, startY + 120, 0xFF0000FF); // Blue diagonal
//        drawThickLine(startX, startY + 140, startX + 100, startY + 160, 3, 0xFFFFFF00); // Yellow thick
//        drawLineRect(startX, startY + 180, startX + 100, startY + 200, 4, 0xFFFF00FF); // Magenta rect-based
//    }
//}

