package com.example.examplemod.shader.newTry;

import com.example.examplemod.shader.newTry.ShaderProgram;
import net.minecraft.util.ResourceLocation;

public class VerticalBlurShader extends ShaderProgram {

	private static final ResourceLocation VERTEX_FILE = new ResourceLocation("examplemod:shaders/gaussianBlur/verticalBlur.vsh");
	private static final ResourceLocation FRAGMENT_FILE = new ResourceLocation("examplemod:shaders/gaussianBlur/blurFragment.fsh");

	private int location_targetHeight;

	public VerticalBlurShader() {
		super(
				VERTEX_FILE,
				FRAGMENT_FILE
		);
	}

	public void loadTargetHeight(float height) {
		super.loadFloat(location_targetHeight, height);
	}

	@Override
	protected void getAllUniformLocations() {
		location_targetHeight = super.getUniformLocation("targetHeight");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
