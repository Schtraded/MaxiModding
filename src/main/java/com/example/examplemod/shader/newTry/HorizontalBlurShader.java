package com.example.examplemod.shader.newTry;

import net.minecraft.util.ResourceLocation;

public class HorizontalBlurShader extends ShaderProgram {

	private static final ResourceLocation VERTEX_FILE = new ResourceLocation("examplemod:shaders/gaussianBlur/horizontalBlur.vsh");
	private static final ResourceLocation FRAGMENT_FILE = new ResourceLocation("examplemod:shaders/gaussianBlur/blurFragment.fsh");

	private int location_targetWidth;

	public HorizontalBlurShader() {
		super(
				VERTEX_FILE,
				FRAGMENT_FILE
		);
	}

	public void loadTargetWidth(float width) {
		super.loadFloat(location_targetWidth, width);
	}

	@Override
	protected void getAllUniformLocations() {
		location_targetWidth = super.getUniformLocation("targetWidth");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
