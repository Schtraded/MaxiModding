package com.example.examplemod.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class ShapeCuttingUtils {

    // ========================= STENCIL BUFFER METHODS =========================

    /**
     * Draws a rectangle with a rectangular cutout using stencil buffer
     */
    public static void drawRectWithRectCutout(float x, float y, float width, float height, int color,
                                              float cutX, float cutY, float cutWidth, float cutHeight) {
        // Clear and enable stencil buffer
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        // First pass: Write cutout area to stencil buffer
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false); // Don't draw to color buffer

        // Draw the cutout shape to stencil
        ////RectangleDrawingUtils.drawFilledRect(cutX, cutY, cutWidth, cutHeight, 0xFF000000);

        // Second pass: Draw main shape where stencil != 1
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true); // Re-enable color drawing

        // Draw the main shape
        ////RectangleDrawingUtils.drawFilledRect(x, y, width, height, color);

        // Cleanup
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    /**
     * Draws a rectangle with a circular cutout using stencil buffer
     */
    public static void drawRectWithCircleCutout(float x, float y, float width, float height, int color,
                                                float circleX, float circleY, float radius) {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        // First pass: Write circle to stencil buffer
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);

        // Draw circle to stencil
        drawCircle(circleX, circleY, radius, 0xFF000000);

        // Second pass: Draw rectangle where stencil != 1
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        //RectangleDrawingUtils.drawFilledRect(x, y, width, height, color);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    /**
     * Draws multiple shapes with complex stencil cutouts
     */
    public static void drawComplexStencilShape(float x, float y, float width, float height, int color) {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        // Pass 1: Create stencil mask with multiple cutouts
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);

        // Add multiple cutout shapes to stencil
        drawCircle(x + width * 0.25f, y + height * 0.25f, 20, 0xFF000000);
        drawCircle(x + width * 0.75f, y + height * 0.75f, 15, 0xFF000000);
        //RectangleDrawingUtils.drawFilledRect(x + width * 0.4f, y + height * 0.4f, width * 0.2f, height * 0.2f, 0xFF000000);

        // Pass 2: Draw main shape avoiding stencil areas
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        //RectangleDrawingUtils.drawFilledRect(x, y, width, height, color);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    // ========================= SCISSOR TESTING METHODS =========================

    /**
     * Draws a shape clipped to a rectangular region using scissor test
     */
    public static void drawWithScissorClip(float x, float y, float width, float height, int color,
                                           float clipX, float clipY, float clipWidth, float clipHeight) {
        // Convert coordinates for OpenGL scissor (bottom-left origin)
        ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
        int windowHeight = scaledRes.getScaledHeight();

        int scissorX = (int)clipX;
        int scissorY = windowHeight - (int)(clipY + clipHeight);
        int scissorW = (int)clipWidth;
        int scissorH = (int)clipHeight;

        // Enable scissor test
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);

        // Draw shape - only scissor area will be visible
        //RectangleDrawingUtils.drawFilledRect(x, y, width, height, color);

        // Disable scissor
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    /**
     * Draws multiple shapes with different scissor regions
     */
    public static void drawMultipleScissorRegions(float baseX, float baseY) {
        // First region
        drawWithScissorClip(baseX, baseY, 200, 100, 0xFFFF0000, baseX + 20, baseY + 20, 160, 60);

        // Second region (overlapping)
        drawWithScissorClip(baseX + 50, baseY + 25, 200, 100, 0xFF00FF00, baseX + 70, baseY + 45, 160, 60);

        // Third region
        drawWithScissorClip(baseX + 100, baseY + 50, 200, 100, 0xFF0000FF, baseX + 120, baseY + 70, 160, 60);
    }

    /**
     * Creates a "window" effect using scissor testing
     */
    public static void drawWindowEffect(float x, float y, float windowWidth, float windowHeight) {
        // Draw background pattern
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int checkerColor = ((i + j) % 2 == 0) ? 0xFF333333 : 0xFF666666;
                //RectangleDrawingUtils.drawFilledRect(x + i * 30, y + j * 30, 30, 30, checkerColor);
            }
        }

        // Create window effect
        drawWithScissorClip(x, y, 300, 300, 0x00000000, x + 50, y + 50, windowWidth, windowHeight);

        // Draw window frame
        //RectangleDrawingUtils.drawRectOutline(x + 50, y + 50, windowWidth, windowHeight, 3, 0xFFFFFFFF);
    }

    // ========================= PIXEL-BY-PIXEL METHODS =========================

    /**
     * Draws a rectangle with a circular hole using pixel-by-pixel checking
     */
    public static void drawRectWithCircleHole(float x, float y, float width, float height, int color,
                                              float holeX, float holeY, float holeRadius) {
        // Draw pixel by pixel, skipping hole area
        for (int px = (int)x; px < x + width; px++) {
            for (int py = (int)y; py < y + height; py++) {
                // Calculate distance from hole center
                float dx = px - holeX;
                float dy = py - holeY;
                float distance = (float)Math.sqrt(dx * dx + dy * dy);

                // Only draw if outside hole radius
                if (distance > holeRadius) {
                    Gui.drawRect(px, py, px + 1, py + 1, color);
                }
            }
        }
    }

    /**
     * Draws a shape with multiple circular holes
     */
    public static void drawRectWithMultipleHoles(float x, float y, float width, float height, int color,
                                                 float[] holeX, float[] holeY, float[] holeRadius) {
        for (int px = (int)x; px < x + width; px++) {
            for (int py = (int)y; py < y + height; py++) {
                boolean inHole = false;

                // Check against all holes
                for (int i = 0; i < holeX.length; i++) {
                    float dx = px - holeX[i];
                    float dy = py - holeY[i];
                    float distance = (float)Math.sqrt(dx * dx + dy * dy);

                    if (distance <= holeRadius[i]) {
                        inHole = true;
                        break;
                    }
                }

                // Only draw if not in any hole
                if (!inHole) {
                    Gui.drawRect(px, py, px + 1, py + 1, color);
                }
            }
        }
    }

    /**
     * Draws a shape with complex mathematical cutout pattern
     */
    public static void drawMathematicalPattern(float x, float y, float width, float height, int color) {
        for (int px = (int)x; px < x + width; px++) {
            for (int py = (int)y; py < y + height; py++) {
                // Normalize coordinates to [-1, 1]
                float normX = 2.0f * (px - x) / width - 1.0f;
                float normY = 2.0f * (py - y) / height - 1.0f;

                // Mathematical function for cutout (spiral pattern)
                double angle = Math.atan2(normY, normX);
                double radius = Math.sqrt(normX * normX + normY * normY);
                double spiral = Math.sin(angle * 5 + radius * 10);

                // Only draw if spiral function is positive
                if (spiral > 0) {
                    Gui.drawRect(px, py, px + 1, py + 1, color);
                }
            }
        }
    }

    // ========================= ALPHA BLENDING METHODS =========================

    /**
     * Creates cutout effect using alpha blending
     */
    public static void drawWithAlphaCutout(float x, float y, float width, float height, int mainColor,
                                           float cutX, float cutY, float cutWidth, float cutHeight) {
        // First pass: Draw main shape
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //RectangleDrawingUtils.drawFilledRect(x, y, width, height, mainColor);

        // Second pass: "Cut out" by drawing with destination alpha manipulation
        GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //RectangleDrawingUtils.drawFilledRect(cutX, cutY, cutWidth, cutHeight, 0xFF000000);

        // Reset blending
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Creates fade-out effect using alpha blending
     */
    public static void drawWithAlphaFadeout(float x, float y, float width, float height, int color,
                                            float fadeX, float fadeY, float fadeWidth, float fadeHeight) {
        // Draw main shape
        //RectangleDrawingUtils.drawFilledRect(x, y, width, height, color);

        // Create fade-out effect with gradient
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Draw gradient overlay for fade effect
        //RectangleDrawingUtils.drawGradientRect(fadeX, fadeY, fadeWidth, fadeHeight,
        //        0x00000000, 0xFF000000);

        // Reset blending
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Creates complex blending effects
     */
    public static void drawComplexBlending(float x, float y, float width, float height) {
        // Base layer
        //RectangleDrawingUtils.drawFilledRect(x, y, width, height, 0xFF0066CC);

        // Enable different blend modes for different effects
        GL11.glEnable(GL11.GL_BLEND);

        // Additive blending for bright spots
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawCircle(x + width * 0.3f, y + height * 0.3f, 30, 0x8000FF00);
        drawCircle(x + width * 0.7f, y + height * 0.7f, 25, 0x80FF0000);

        // Multiplicative blending for shadows
        GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
        drawCircle(x + width * 0.5f, y + height * 0.2f, 20, 0x80404040);

        // Reset to normal blending
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    // ========================= MINECRAFT GUI CLIPPING =========================

    /**
     * Uses Minecraft's GUI system for clipping
     */
    public static void drawWithMinecraftClipping(float x, float y, float width, float height, int color,
                                                 float clipX, float clipY, float clipWidth, float clipHeight) {
        GlStateManager.pushMatrix();

        // Set up clipping using Minecraft's system
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
        int windowHeight = scaledRes.getScaledHeight();

        GL11.glScissor((int)clipX, windowHeight - (int)(clipY + clipHeight),
                (int)clipWidth, (int)clipHeight);

        // Draw content
        //RectangleDrawingUtils.drawFilledRect(x, y, width, height, color);

        // Restore state
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    /**
     * Creates nested clipping regions
     */
    public static void drawNestedClipping(float baseX, float baseY) {
        // Outer clipping region
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
        int windowHeight = scaledRes.getScaledHeight();

        // First level clip
        GL11.glScissor((int)(baseX + 20), windowHeight - (int)(baseY + 180), 160, 160);
        //RectangleDrawingUtils.drawFilledRect(baseX, baseY, 200, 200, 0xFF666666);

        // Second level clip (intersection)
        GL11.glScissor((int)(baseX + 40), windowHeight - (int)(baseY + 160), 120, 120);
        //RectangleDrawingUtils.drawFilledRect(baseX, baseY, 200, 200, 0xFFFF0000);

        // Third level clip
        GL11.glScissor((int)(baseX + 60), windowHeight - (int)(baseY + 140), 80, 80);
        //RectangleDrawingUtils.drawFilledRect(baseX, baseY, 200, 200, 0xFF00FF00);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    // ========================= UTILITY METHODS =========================

    /**
     * Helper method to draw a circle
     */
    private static void drawCircle(float centerX, float centerY, float radius, int color) {
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(red, green, blue, alpha);

        int segments = 32;
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(centerX, centerY);
        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            GL11.glVertex2f(centerX + (float)(Math.cos(angle) * radius),
                    centerY + (float)(Math.sin(angle) * radius));
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Demo method showcasing all cutting techniques
     */
    public static void demonstrateAllTechniques(int startX, int startY) {
        int spacing = 220;
        int row1Y = startY;
        int row2Y = startY + 150;
        int row3Y = startY + 300;

        // Row 1: Stencil buffer techniques
        drawRectWithRectCutout(startX, row1Y, 200, 120, 0xFF0066CC,
                startX + 50, row1Y + 30, 100, 60);

        drawRectWithCircleCutout(startX + spacing, row1Y, 200, 120, 0xFF00CC66,
                startX + spacing + 100, row1Y + 60, 40);

        drawComplexStencilShape(startX + spacing * 2, row1Y, 200, 120, 0xFFCC6600);

        // Row 2: Scissor and pixel-by-pixel techniques
        drawWithScissorClip(startX, row2Y, 200, 120, 0xFFFF0066,
                startX + 25, row2Y + 25, 150, 70);

        drawRectWithCircleHole(startX + spacing, row2Y, 200, 120, 0xFF6600FF,
                startX + spacing + 100, row2Y + 60, 35);

        float[] holeX = {startX + spacing * 2 + 60, startX + spacing * 2 + 140, startX + spacing * 2 + 100};
        float[] holeY = {row2Y + 30, row2Y + 30, row2Y + 90};
        float[] holeRadius = {20, 15, 25};
        drawRectWithMultipleHoles(startX + spacing * 2, row2Y, 200, 120, 0xFFFFCC00,
                holeX, holeY, holeRadius);

        // Row 3: Blending and advanced techniques
        drawWithAlphaCutout(startX, row3Y, 200, 120, 0xFF00CCFF,
                startX + 50, row3Y + 30, 100, 60);

        drawMathematicalPattern(startX + spacing, row3Y, 200, 120, 0xFFCC00CC);

        drawComplexBlending(startX + spacing * 2, row3Y, 200, 120);
    }
}
