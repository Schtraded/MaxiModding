package com.example.examplemod.shader.newTry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public class HorizontalBlur {

	private HorizontalBlurShader shader;
	private int framebuffer;
	private int texture;
	private int width;
	private int height;

	public HorizontalBlur(int width, int height) {
		this.width = width;
		this.height = height;

		shader = new HorizontalBlurShader();
		shader.start();
		shader.loadTargetWidth(width);
		shader.stop();

		setupFramebuffer();
	}

	private void setupFramebuffer() {
		framebuffer = GL30.glGenFramebuffers();
		texture = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height,
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, texture, 0);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void render(int inputTexture) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GL11.glViewport(0, 0, width, height);

		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, inputTexture);

		renderQuad();

		shader.stop();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public int getOutputTexture() {
		return texture;
	}

	private void renderQuad() {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0f, 0f);
		GL11.glVertex2f(-1f, -1f);
		GL11.glTexCoord2f(1f, 0f);
		GL11.glVertex2f(1f, -1f);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex2f(1f, 1f);
		GL11.glTexCoord2f(0f, 1f);
		GL11.glVertex2f(-1f, 1f);
		GL11.glEnd();
	}

	public void cleanUp() {
		shader.cleanUp();
		GL30.glDeleteFramebuffers(framebuffer);
		GL11.glDeleteTextures(texture);
	}
}

