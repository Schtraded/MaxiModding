package com.example.examplemod.shader.backup.backup2;

import com.example.examplemod.gui.DummyGui;
import com.example.examplemod.shader.GaussianBlurShader;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class BlurRenderer {

    private int framebuffer = -1;
    private int colorTexture = -1;
    private int screenWidth = -1;
    private int screenHeight = -1;

    public void initShaders() {
        // Initialize the GaussianBlurShader
        if (!GaussianBlurShader.INSTANCE.created) {
            System.err.println("Failed to create blur shader!");
            return;
        }

        // Initialize framebuffer for rendering scene to texture
        initFramebuffer();
    }

    private void initFramebuffer() {
        Minecraft mc = Minecraft.getMinecraft();
        screenWidth = mc.displayWidth;
        screenHeight = mc.displayHeight;

        // Generate framebuffer
        framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);

        // Generate color texture
        colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, screenWidth, screenHeight, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Attach texture to framebuffer
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);

        // Check framebuffer status
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Framebuffer not complete!");
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void renderBlurredBackground(RenderGameOverlayEvent.Post event) {
        // This fires AFTER everything including hand is rendered
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!GaussianBlurShader.INSTANCE.created || framebuffer == -1) return;

        Minecraft mc = Minecraft.getMinecraft();

        // Skip if in GUI
        //TODO:if (mc.currentScreen != null) return;
        if (!(Minecraft.getMinecraft().currentScreen instanceof DummyGui)) return;

        // Check if screen size changed
        if (screenWidth != mc.displayWidth || screenHeight != mc.displayHeight) {
            cleanupFramebuffer();
            initFramebuffer();
        }

        // Step 1: Capture current screen to texture
        captureScreenToTexture();

        // Step 2: Apply blur effect
        applyBlurEffect();
    }

    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof DummyGui)) return;
        renderBlurredBackground(event);
    }

    private void captureScreenToTexture() {
        // Bind our texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);

        // Copy from the current framebuffer to our texture
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, screenWidth, screenHeight, 0);

        // Unbind texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void applyBlurEffect() {
        Minecraft mc = Minecraft.getMinecraft();

        // Save current state
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // Setup orthographic projection for fullscreen quad
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 1, 0, 1, -1, 1);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // Disable depth testing
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // Bind the texture containing the scene
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);

        // Enable blur shader
        GaussianBlurShader.INSTANCE.enable();

        // Set texture uniform
        int textureUniform = GL20.glGetUniformLocation(GaussianBlurShader.INSTANCE.shaderProgram, "texture");
        GL20.glUniform1i(textureUniform, 0); // Use texture unit 0

        // The uniforms are automatically updated by the shader's updateUniforms() method

        // Draw fullscreen quad with correct texture coordinates
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f, 1.0f);
        GL11.glEnd();

        // Disable shader
        GaussianBlurShader.INSTANCE.disable();

        // Explicitly unbind texture and reset texture unit
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        // Reset color to white (important!)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL20.glUseProgram(0); // Make sure shader is fully disabled

        // Restore state
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        // CRITICAL: Reset matrices properly for GUI rendering
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
    }

    private void cleanupFramebuffer() {
        if (framebuffer != -1) {
            GL30.glDeleteFramebuffers(framebuffer);
            framebuffer = -1;
        }
        if (colorTexture != -1) {
            GL11.glDeleteTextures(colorTexture);
            colorTexture = -1;
        }
    }
}