package com.example.examplemod.shader.roundedRect;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class RoundedRectRenderer {

    // Simple rounded rectangle with uniform corner radius (percentage)
    public static void renderRoundedRect(float x, float y, float width, float height,
                                         float cornerRadiusPercent,
                                         float r, float g, float b, float a) {
        renderRoundedRectPercent(x, y, width, height, cornerRadiusPercent, cornerRadiusPercent,
                cornerRadiusPercent, cornerRadiusPercent, r, g, b, a, 0, 0, 0, 0, 0);
    }

    public static void renderRoundedRect(float x, float y, float width, float height,
                                         float cornerRadiusTopLeftPercent, float cornerRadiusTopRightPercent,
                                         float cornerRadiusBottomRightPercent, float cornerBottomLeftRadiusPercent,
                                         float r, float g, float b, float a,
                                         float borderWidth, float borderR, float borderG, float borderB, float borderA) {
        renderRoundedRectPercent(x, y, width, height, cornerRadiusTopLeftPercent, cornerRadiusTopRightPercent,
                cornerRadiusBottomRightPercent, cornerBottomLeftRadiusPercent, r, g, b, a, borderWidth, borderR, borderG, borderB, borderA);
    }

    // Rounded rectangle with individual corner radii (percentages)
    public static void renderRoundedRectPercent(float x, float y, float width, float height,
                                                float topLeftPercent, float topRightPercent,
                                                float bottomRightPercent, float bottomLeftPercent,
                                                float r, float g, float b, float a) {
        renderRoundedRectPercent(x, y, width, height, topLeftPercent, topRightPercent,
                bottomRightPercent, bottomLeftPercent, r, g, b, a, 0, 0, 0, 0, 0);
    }

    // Full-featured rounded rectangle with percentage-based corners and border
    public static void renderRoundedRectPercent(float x, float y, float width, float height,
                                                float topLeftPercent, float topRightPercent,
                                                float bottomRightPercent, float bottomLeftPercent,
                                                float fillR, float fillG, float fillB, float fillA,
                                                float borderWidth, float borderR, float borderG,
                                                float borderB, float borderA) {
        if (!RoundedRectShader.INSTANCE.created) {
            System.err.println("Rounded rectangle shader not created!");
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        // Convert pixel coordinates to normalized coordinates (0.0 to 1.0)
        float normalizedX = x / mc.displayWidth;
        float normalizedY = y / mc.displayHeight;
        float normalizedWidth = width / mc.displayWidth;
        float normalizedHeight = height / mc.displayHeight;
        float normalizedBorderWidth = borderWidth / Math.min(mc.displayWidth, mc.displayHeight);

        // Save current OpenGL state
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Setup for rendering
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Setup orthographic projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, mc.displayWidth, mc.displayHeight, 0, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // Configure rounded rectangle shader
        RoundedRectShader shader = RoundedRectShader.INSTANCE;
        shader.setPosition(normalizedX, normalizedY);
        shader.setSize(normalizedWidth, normalizedHeight);

        // Use percentage-based corner radii
        shader.setCornerRadii(topLeftPercent, topRightPercent, bottomRightPercent, bottomLeftPercent);

        shader.setColor(fillR, fillG, fillB, fillA);
        shader.setBorderWidth(normalizedBorderWidth);
        shader.setBorderColor(borderR, borderG, borderB, borderA);

        // Enable shader and render fullscreen quad
        shader.enable();

        // Render a fullscreen quad (the shader will handle the rounded rectangle masking)
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(mc.displayWidth, 0);
        GL11.glVertex2f(mc.displayWidth, mc.displayHeight);
        GL11.glVertex2f(0, mc.displayHeight);
        GL11.glEnd();

        shader.disable();

        // Restore OpenGL state
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
        // ===== ABSOLUTE PIXEL-BASED METHODS (Original functionality) =====

        // Simple rounded rectangle with uniform corner radius (absolute pixels)
        public static void renderRoundedRectAbsolute(float x, float y, float width, float height,
        float cornerRadiusPixels,
        float r, float g, float b, float a) {
            renderRoundedRectAbsolute(x, y, width, height, cornerRadiusPixels, cornerRadiusPixels,
                    cornerRadiusPixels, cornerRadiusPixels, r, g, b, a, 0, 0, 0, 0, 0);
        }

        // Rounded rectangle with individual corner radii (absolute pixels)
        public static void renderRoundedRectAbsolute(float x, float y, float width, float height,
        float topLeftPixels, float topRightPixels,
        float bottomRightPixels, float bottomLeftPixels,
        float r, float g, float b, float a) {
            renderRoundedRectAbsolute(x, y, width, height, topLeftPixels, topRightPixels,
                    bottomRightPixels, bottomLeftPixels, r, g, b, a, 0, 0, 0, 0, 0);
        }

        // Full-featured rounded rectangle with absolute pixel corners and border
        public static void renderRoundedRectAbsolute(float x, float y, float width, float height,
        float topLeftPixels, float topRightPixels,
        float bottomRightPixels, float bottomLeftPixels,
        float fillR, float fillG, float fillB, float fillA,
        float borderWidth, float borderR, float borderG,
        float borderB, float borderA) {
            if (!RoundedRectShader.INSTANCE.created) {
                System.err.println("Rounded rectangle shader not created!");
                return;
            }

            Minecraft mc = Minecraft.getMinecraft();

            // Convert pixel coordinates to normalized coordinates (0.0 to 1.0)
            float normalizedX = x / mc.displayWidth;
            float normalizedY = y / mc.displayHeight;
            float normalizedWidth = width / mc.displayWidth;
            float normalizedHeight = height / mc.displayHeight;

            // Convert radii to normalized coordinates
            float normalizedTLRadius = topLeftPixels / Math.min(mc.displayWidth, mc.displayHeight);
            float normalizedTRRadius = topRightPixels / Math.min(mc.displayWidth, mc.displayHeight);
            float normalizedBRRadius = bottomRightPixels / Math.min(mc.displayWidth, mc.displayHeight);
            float normalizedBLRadius = bottomLeftPixels / Math.min(mc.displayWidth, mc.displayHeight);
            float normalizedBorderWidth = borderWidth / Math.min(mc.displayWidth, mc.displayHeight);

            // Save current OpenGL state
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            // Setup for rendering
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Setup orthographic projection
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, mc.displayWidth, mc.displayHeight, 0, -1, 1);

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            // Configure rounded rectangle shader
            RoundedRectShader shader = RoundedRectShader.INSTANCE;
            shader.setPosition(normalizedX, normalizedY);
            shader.setSize(normalizedWidth, normalizedHeight);
            shader.setCornerRadiiAbsolute(normalizedTLRadius, normalizedTRRadius, normalizedBRRadius, normalizedBLRadius);
            shader.setColor(fillR, fillG, fillB, fillA);
            shader.setBorderWidth(normalizedBorderWidth);
            shader.setBorderColor(borderR, borderG, borderB, borderA);

            // Enable shader and render fullscreen quad
            shader.enable();

            // Render a fullscreen quad (the shader will handle the rounded rectangle masking)
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(0, 0);
            GL11.glVertex2f(mc.displayWidth, 0);
            GL11.glVertex2f(mc.displayWidth, mc.displayHeight);
            GL11.glVertex2f(0, mc.displayHeight);
            GL11.glEnd();

            shader.disable();

            // Restore OpenGL state
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        // ===== CONVENIENCE METHODS (Updated to use percentage by default) =====
        public static void renderFilledRoundedRect(float x, float y, float width, float height,
        float cornerRadius,
        float r, float g, float b, float a) {
            renderRoundedRect(x, y, width, height, cornerRadius, r, g, b, a);
        }

        // Convenience method for rounded rectangle with border
        public static void renderRoundedRectWithBorder(float x, float y, float width, float height,
        float cornerRadius,
        float fillR, float fillG, float fillB, float fillA,
        float borderWidth,
        float borderR, float borderG, float borderB, float borderA) {
            renderRoundedRect(x, y, width, height, cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                    fillR, fillG, fillB, fillA, borderWidth, borderR, borderG, borderB, borderA);
        }

        // Convenience method for just a rounded rectangle outline
        public static void renderRoundedRectOutline(float x, float y, float width, float height,
        float cornerRadius, float borderWidth,
        float r, float g, float b, float a) {
            renderRoundedRect(x, y, width, height, cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                    0, 0, 0, 0, // Transparent fill
                    borderWidth, r, g, b, a); // Colored border
        }

        // Modern UI card style with subtle shadow effect (percentage-based)
        public static void renderCard(float x, float y, float width, float height,
        float cornerRadiusPercent,
        float r, float g, float b, float a) {
            // Render shadow first (slightly offset and darker)
            renderRoundedRectPercent(x + 2, y + 2, width, height, cornerRadiusPercent, cornerRadiusPercent,
                    cornerRadiusPercent, cornerRadiusPercent, 0, 0, 0, 0.1f, // Shadow
                    0, 0, 0, 0, 0); // No border

            // Render main card
            renderRoundedRect(x, y, width, height, cornerRadiusPercent, r, g, b, a);
        }

        // Button-style rounded rectangle with highlight border (percentage-based)
        public static void renderButton(float x, float y, float width, float height,
        float cornerRadiusPercent,
        float fillR, float fillG, float fillB, float fillA,
        boolean hovered) {
            if (hovered) {
                // Brighter version when hovered
                renderRoundedRectWithBorder(x, y, width, height, cornerRadiusPercent,
                        Math.min(1.0f, fillR + 0.1f), Math.min(1.0f, fillG + 0.1f),
                        Math.min(1.0f, fillB + 0.1f), fillA,
                        2, 1.0f, 1.0f, 1.0f, 0.3f); // White border
            } else {
                renderFilledRoundedRect(x, y, width, height, cornerRadiusPercent, fillR, fillG, fillB, fillA);
            }
        }
    }