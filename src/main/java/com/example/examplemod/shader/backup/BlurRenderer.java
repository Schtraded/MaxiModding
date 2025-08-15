package com.example.examplemod.shader.backup;

import com.example.examplemod.shader.HorizontalBlurShader;
import com.example.examplemod.shader.VerticalBlurShader;
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
    private int horizontalFramebuffer = -1;
    private int horizontalTexture = -1;

    public void initShaders() {
        // Initialize the blur shaders
        if (!HorizontalBlurShader.INSTANCE.created || !VerticalBlurShader.INSTANCE.created) {
            System.err.println("Failed to create blur shaders!");
            return;
        }

        // Initialize framebuffer for rendering scene to texture
        initFramebuffer();
    }

    private void initFramebuffer() {
        Minecraft mc = Minecraft.getMinecraft();
        screenWidth = mc.displayWidth;
        screenHeight = mc.displayHeight;

        // Original framebuffer for screen capture
        framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);

        colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, screenWidth, screenHeight, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);

        // Check framebuffer status
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Main framebuffer not complete!");
        }

        // Horizontal pass framebuffer
        horizontalFramebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, horizontalFramebuffer);

        horizontalTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, horizontalTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, screenWidth, screenHeight, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, horizontalTexture, 0);

        // Check framebuffer status
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Horizontal framebuffer not complete!");
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
        // This fires AFTER everything including hand is rendered
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!HorizontalBlurShader.INSTANCE.created || !VerticalBlurShader.INSTANCE.created || framebuffer == -1) return;

        Minecraft mc = Minecraft.getMinecraft();

        // Skip if in GUI
        if (mc.currentScreen != null) return;

        // Check if screen size changed
        if (screenWidth != mc.displayWidth || screenHeight != mc.displayHeight) {
            cleanupFramebuffer();
            initFramebuffer();
        }

        // Step 1: Capture current screen to texture
        captureScreenToTexture();

        // Step 2: Apply two-pass blur effect
        applyTwoPassBlur();
    }

    private void captureScreenToTexture() {
        // Bind our texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);

        // Copy from the current framebuffer to our texture
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, screenWidth, screenHeight, 0);

        // Unbind texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void applyTwoPassBlur() {
        // Pass 1: Horizontal blur
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, horizontalFramebuffer);
        GL11.glViewport(0, 0, screenWidth, screenHeight);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        applyBlurPass(colorTexture, true); // true = horizontal

        // Pass 2: Vertical blur to screen
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, screenWidth, screenHeight);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        applyBlurPass(horizontalTexture, false); // false = vertical
    }

    private void applyBlurPass(int inputTexture, boolean horizontal) {
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

        // Bind input texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, inputTexture);

        // Use appropriate shader
        if (horizontal) {
            HorizontalBlurShader.INSTANCE.enable();
            int textureUniform = GL20.glGetUniformLocation(HorizontalBlurShader.INSTANCE.shaderProgram, "texture");
            GL20.glUniform1i(textureUniform, 0);
        } else {
            VerticalBlurShader.INSTANCE.enable();
            int textureUniform = GL20.glGetUniformLocation(VerticalBlurShader.INSTANCE.shaderProgram, "texture");
            GL20.glUniform1i(textureUniform, 0);
        }

        // Draw fullscreen quad
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f, 0.0f); // Bottom-left
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(1.0f, 0.0f); // Bottom-right
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(1.0f, 1.0f); // Top-right
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f, 1.0f); // Top-left
        GL11.glEnd();

        // Disable shader
        if (horizontal) {
            HorizontalBlurShader.INSTANCE.disable();
        } else {
            VerticalBlurShader.INSTANCE.disable();
        }

        // Cleanup
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
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
        if (horizontalFramebuffer != -1) {
            GL30.glDeleteFramebuffers(horizontalFramebuffer);
            horizontalFramebuffer = -1;
        }
        if (horizontalTexture != -1) {
            GL11.glDeleteTextures(horizontalTexture);
            horizontalTexture = -1;
        }
    }
}