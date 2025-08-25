package com.example.examplemod.shader.gaussianBlur;

import com.example.examplemod.shader.Shader;
import com.example.examplemod.shader.Uniform;
import net.minecraft.client.Minecraft;

public class VerticalBlurShader extends Shader {

    public static final VerticalBlurShader INSTANCE = new VerticalBlurShader();

    private final Uniform.Vec2 resolutionUniform;

    private VerticalBlurShader() {
        super("vertical_blur");

        resolutionUniform = new Uniform.Vec2(this, "resolution", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight};
            }
        });

        addUniform(resolutionUniform);
    }

    @Override
    public void updateUniforms() {
        resolutionUniform.update();
    }
}