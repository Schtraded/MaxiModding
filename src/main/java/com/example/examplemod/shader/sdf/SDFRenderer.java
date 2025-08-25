package com.example.examplemod.shader.sdf;

import com.example.examplemod.gui.DummyGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class SDFRenderer {

    //private int screenWidth = -1;
    //private int screenHeight = -1;

    public void initShaders() {
        // Initialize the SDFShader
        if (!SDFShader.INSTANCE.created) {
            System.err.println("Failed to create SDF shader!");
            return;
        }
    }

    //@SubscribeEvent
    //public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
    //    // Only render for the HUD/ALL element
    //    if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
    //    if (!SDFShader.INSTANCE.created) return;
//
    //    Minecraft mc = Minecraft.getMinecraft();
    //    // Only render when specific GUI is open (you can change this condition)
    //    if (!(mc.currentScreen instanceof DummyGui)) return;
//
    //    // Check if screen size changed
    //    //if (screenWidth != mc.displayWidth || screenHeight != mc.displayHeight) {
    //    //    screenWidth = mc.displayWidth;
    //    //    screenHeight = mc.displayHeight;
    //    //}
//
    //    // Render SDF shapes
    //    //renderSDFShapes();
    //}

    private void renderSDFShapes() {
        // Save current OpenGL state
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Setup OpenGL state for rendering
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Configure and render a rounded rectangle
        SDFShader shader = SDFShader.INSTANCE;

        // Example configuration - you can customize this
        shader.setPosition(0.0f, 0.0f); // Center of screen
        shader.setSize(0.15f, 0.1f); // Smaller, more reasonable size
        shader.setCorners(0.02f, 0.03f, 0.01f, 0.025f); // Smaller corner radii
        shader.setColor(0.3f, 0.7f, 1.0f, 1.0f); // Solid blue
        shader.setBackgroundColor(0.0f, 0.0f, 0.0f, 0.0f); // Transparent background

        // Enable shader
        shader.enable();

        // Draw fullscreen quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f, 1.0f);
        GL11.glEnd();

        // Disable shader
        shader.disable();

        // Restore OpenGL state
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Restore state
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    // Method to render a specific rounded rectangle with custom parameters
    public void renderRoundedRect(float x, float y, float width, float height,
                                  float topRight, float bottomRight, float bottomLeft, float topLeft,
                                  float r, float g, float b, float a) {

        if (!SDFShader.INSTANCE.created) return;

        // Save current OpenGL state
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
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Configure shader
        SDFShader shader = SDFShader.INSTANCE;
        shader.setPosition(x, y);
        shader.setSize(width / 2.0f, height / 2.0f); // SDF expects half-sizes
        shader.setCorners(topRight, bottomRight, bottomLeft, topLeft);
        shader.setColor(r, g, b, a);
        shader.setBackgroundColor(0.0f, 0.0f, 0.0f, 0.0f); // Transparent background

        // Enable shader
        shader.enable();

        // Draw fullscreen quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f, 1.0f);
        GL11.glEnd();

        // Disable shader
        shader.disable();

        // Restore state
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    // Animated version for demonstration
    public void renderAnimatedRect() {
        if (!SDFShader.INSTANCE.created) return;

        // Save current OpenGL state
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Setup projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 1, 0, 1, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // Setup OpenGL state
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Animate parameters
        float time = (float)(System.currentTimeMillis() % 10000) / 10000.0f;

        // Animated position
        float x = 0.2f * (float)Math.sin(time * 2.0 * Math.PI);
        float y = 0.1f * (float)Math.cos(time * 2.0 * Math.PI);

        // Animated size
        float baseSize = 0.2f;
        float sizeVariation = 0.05f * (float)Math.sin(time * 4.0 * Math.PI);

        // Animated corners
        float corner1 = 0.02f + 0.03f * (float)Math.sin(time * 6.0 * Math.PI);
        float corner2 = 0.02f + 0.03f * (float)Math.sin(time * 6.0 * Math.PI + 1.57f);
        float corner3 = 0.02f + 0.03f * (float)Math.sin(time * 6.0 * Math.PI + 3.14f);
        float corner4 = 0.02f + 0.03f * (float)Math.sin(time * 6.0 * Math.PI + 4.71f);

        // Animated color
        float r = 0.5f + 0.5f * (float)Math.sin(time * 3.0 * Math.PI);
        float g = 0.5f + 0.5f * (float)Math.sin(time * 3.0 * Math.PI + 2.09f);
        float b = 0.5f + 0.5f * (float)Math.sin(time * 3.0 * Math.PI + 4.18f);

        // Configure shader
        SDFShader shader = SDFShader.INSTANCE;
        shader.setPosition(x, y);
        shader.setSize(baseSize + sizeVariation, (baseSize + sizeVariation) * 0.6f);
        shader.setCorners(corner1, corner2, corner3, corner4);
        shader.setColor(r, g, b, 0.8f);
        shader.setBackgroundColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Enable shader
        shader.enable();

        // Draw fullscreen quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f, 1.0f);
        GL11.glEnd();

        // Disable shader
        shader.disable();

        // Restore state
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}