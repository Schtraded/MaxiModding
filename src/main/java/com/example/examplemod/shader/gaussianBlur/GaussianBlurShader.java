package com.example.examplemod.shader.gaussianBlur;

import com.example.examplemod.shader.Shader;
import com.example.examplemod.shader.Uniform;
import net.minecraft.client.Minecraft;

public class GaussianBlurShader extends Shader {

    public static final GaussianBlurShader INSTANCE = new GaussianBlurShader();

    private final Uniform.Float radiusUniform;
    private final Uniform.Vec2 resolutionUniform;

    private GaussianBlurShader() {
        super("gaussian_blur");

        radiusUniform = new Uniform.Float(this, "radius", new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return 5.0f;
            }
        });
        resolutionUniform = new Uniform.Vec2(this, "resolution", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight};
            }
        });

        addUniform(radiusUniform);
        addUniform(resolutionUniform);
    }

    @Override
    public void updateUniforms() {
        radiusUniform.update();
        resolutionUniform.update();
    }
}