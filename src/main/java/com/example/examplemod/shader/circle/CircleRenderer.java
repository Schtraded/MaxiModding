package com.example.examplemod.shader.circle;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.helper.DisplayUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class CircleRenderer {

    private static int sw = DisplayUtils.scaledWidth;
    private static int sh = DisplayUtils.scaledHeight;

    public static void renderCircle(float centerX, float centerY, float radius,
                                    float r, float g, float b, float a) {
        renderCircle(centerX, centerY, radius, r, g, b, a, 0, 0, 0, 0, 0);
    }

    public static void renderCircle(float centerX, float centerY, float radius,
                                    float r, float g, float b, float a,
                                    float borderWidth, float borderR, float borderG, float borderB, float borderA) {
        if (!CircleShader.INSTANCE.created) {
            System.err.println("Circle shader not created!");
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        // Convert pixel coordinates to normalized coordinates (0.0 to 1.0)
        float normalizedCenterX = centerX / mc.displayWidth;
        float normalizedCenterY = 1.0f - (centerY / mc.displayHeight); // Flip Y coordinate
        float normalizedRadius = radius / Math.min(mc.displayWidth, mc.displayHeight);
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

        // Configure circle shader
        CircleShader shader = CircleShader.INSTANCE;
        shader.setCenter(normalizedCenterX, normalizedCenterY);
        shader.setRadius(normalizedRadius);
        shader.setColor(r, g, b, a);
        shader.setBorderWidth(normalizedBorderWidth);
        shader.setBorderColor(borderR, borderG, borderB, borderA);

        // Enable shader and render fullscreen quad
        shader.enable();

        // Render a fullscreen quad (the shader will handle the circle masking)
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

    // Convenience method for rendering a simple filled circle
    public static void renderFilledCircle(float centerX, float centerY, float radius,
                                          float r, float g, float b, float a) {
        renderCircle(centerX, centerY, radius, r, g, b, a);
    }

    // Convenience method for rendering a circle with border
    public static void renderCircleWithBorder(float centerX, float centerY, float radius,
                                              float fillR, float fillG, float fillB, float fillA,
                                              float borderWidth,
                                              float borderR, float borderG, float borderB, float borderA) {
        renderCircle(centerX, centerY, radius, fillR, fillG, fillB, fillA,
                borderWidth, borderR, borderG, borderB, borderA);
    }

    // Convenience method for rendering just a circle outline
    public static void renderCircleOutline(float centerX, float centerY, float radius,
                                           float borderWidth,
                                           float r, float g, float b, float a) {
        renderCircle(centerX, centerY, radius, 0, 0, 0, 0, // Transparent fill
                borderWidth, r, g, b, a); // Colored border
    }
}
