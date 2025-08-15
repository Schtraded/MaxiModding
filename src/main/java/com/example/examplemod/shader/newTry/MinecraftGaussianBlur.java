package com.example.examplemod.shader.newTry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class MinecraftGaussianBlur {
    private final HorizontalBlur horizontalBlur;
    private final VerticalBlur verticalBlur;
    private final Framebuffer downscaleFBO;
    private final Framebuffer horizontalFBO;

    private final Minecraft mc = Minecraft.getMinecraft();

    public MinecraftGaussianBlur(int downscaleFactor) {
        int width = mc.displayWidth / downscaleFactor;
        int height = mc.displayHeight / downscaleFactor;

        // Create downscaled FBO
        this.downscaleFBO = new Framebuffer(width, height, true);
        this.horizontalFBO = new Framebuffer(downscaleFBO.framebufferWidth, downscaleFBO.framebufferHeight, true);
        // Setup shaders
        this.horizontalBlur = new HorizontalBlur(width, height);
        this.verticalBlur = new VerticalBlur(width, height);
    }

    public void applyBlur() {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mc.getFramebuffer().framebufferObject);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, downscaleFBO.framebufferObject);

        GL30.glBlitFramebuffer(
                0, 0, mc.displayWidth, mc.displayHeight,                 // source rect
                0, 0, downscaleFBO.framebufferWidth, downscaleFBO.framebufferHeight, // dest rect
                GL11.GL_COLOR_BUFFER_BIT,                                 // copy color
                GL11.GL_LINEAR                                            // filtering
        );

        // Unbind FBOs (bind default framebuffer)
        //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, horizontalFBO.framebufferObject);
        GL11.glViewport(0, 0, horizontalFBO.framebufferWidth, horizontalFBO.framebufferHeight);

        // --- Horizontal blur pass ---
        horizontalBlur.render(downscaleFBO.framebufferTexture);

        // --- Vertical blur pass ---
        verticalBlur.render(horizontalBlur.getOutputTexture());

        // --- Draw final texture back to screen ---
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, verticalBlur.getOutputTexture());
        drawFullScreenQuad();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        //// Bind downscale FBO and copy Minecraft framebuffer
        //downscaleFBO.bindFramebuffer(true);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        //drawFullScreenQuad();
        //downscaleFBO.unbindFramebuffer();
//
        //// Horizontal pass
        //horizontalBlur.render(downscaleFBO.framebufferTexture);
//
        //// Vertical pass
        //verticalBlur.render(horizontalBlur.getOutputTexture());
//
        //// Draw final texture back to screen
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, verticalBlur.getOutputTexture());
        //drawFullScreenQuad();
    }

    private void drawFullScreenQuad() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GL11.glColor4f(1f, 0f, 0f, 1f); // bright red
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(-1f, -1f);
        GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(1f, -1f);
        GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(1f, 1f);
        GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(-1f, 1f);
        GL11.glEnd();
        GlStateManager.color(1f, 1f, 1f, 1f); // reset color
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

    }

    public void cleanUp() {
        horizontalBlur.cleanUp();
        verticalBlur.cleanUp();
        downscaleFBO.deleteFramebuffer();
    }
}
