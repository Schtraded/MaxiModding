package com.example.examplemod.shader.circle;

import com.example.examplemod.shader.Shader;
import com.example.examplemod.shader.Uniform;
import net.minecraft.client.Minecraft;

public class CircleShader extends Shader {

    public static final CircleShader INSTANCE = new CircleShader();

    private final Uniform.Vec2 resolutionUniform;
    private final Uniform.Vec2 centerUniform;
    private final Uniform.Float radiusUniform;
    private final Uniform.Vec4 colorUniform;
    private final Uniform.Float borderWidthUniform;
    private final Uniform.Vec4 borderColorUniform;
    private final Uniform.Float smoothnessUniform;

    private CircleShader() {
        super("circle");

        // Screen resolution
        resolutionUniform = new Uniform.Vec2(this, "resolution", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight};
            }
        });

        // Circle center (in screen coordinates, 0-1 range)
        centerUniform = new Uniform.Vec2(this, "center", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{0.5f, 0.5f}; // Default to center of screen
            }
        });

        // Circle radius (in screen space, 0-1 range where 0.5 = half screen)
        radiusUniform = new Uniform.Float(this, "radius", new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return 0.2f; // Default radius
            }
        });

        // Circle fill color (RGBA)
        colorUniform = new Uniform.Vec4(this, "color", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{1.0f, 1.0f, 1.0f, 1.0f}; // Default white
            }
        });

        // Border width (in screen space)
        borderWidthUniform = new Uniform.Float(this, "borderWidth", new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return 0.02f; // Default border width
            }
        });

        // Border color (RGBA)
        borderColorUniform = new Uniform.Vec4(this, "borderColor", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{0.0f, 0.0f, 0.0f, 1.0f}; // Default black border
            }
        });

        // Anti-aliasing smoothness
        smoothnessUniform = new Uniform.Float(this, "smoothness", new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return 0.005f; // Default smoothness for anti-aliasing
            }
        });

        addUniform(resolutionUniform);
        addUniform(centerUniform);
        addUniform(radiusUniform);
        addUniform(colorUniform);
        addUniform(borderWidthUniform);
        addUniform(borderColorUniform);
        addUniform(smoothnessUniform);
    }

    // Setters for dynamic values
    private float[] currentCenter = {0.5f, 0.5f};
    private float currentRadius = 0.2f;
    private float[] currentColor = {1.0f, 1.0f, 1.0f, 1.0f};
    private float currentBorderWidth = 0.02f;
    private float[] currentBorderColor = {0.0f, 0.0f, 0.0f, 1.0f};
    private float currentSmoothness = 0.005f;

    public void setCenter(float x, float y) {
        currentCenter[0] = x;
        currentCenter[1] = y;
    }

    public void setRadius(float radius) {
        currentRadius = radius;
    }

    public void setColor(float r, float g, float b, float a) {
        currentColor[0] = r;
        currentColor[1] = g;
        currentColor[2] = b;
        currentColor[3] = a;
    }

    public void setBorderWidth(float width) {
        currentBorderWidth = width;
    }

    public void setBorderColor(float r, float g, float b, float a) {
        currentBorderColor[0] = r;
        currentBorderColor[1] = g;
        currentBorderColor[2] = b;
        currentBorderColor[3] = a;
    }

    public void setSmoothness(float smoothness) {
        currentSmoothness = smoothness;
    }

    @Override
    public void updateUniforms() {
        resolutionUniform.update();

        // Update uniforms with current values
        centerUniform.supplier = new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return currentCenter;
            }
        };
        centerUniform.update();

        radiusUniform.supplier = new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return currentRadius;
            }
        };
        radiusUniform.update();

        colorUniform.supplier = new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return currentColor;
            }
        };
        colorUniform.update();

        borderWidthUniform.supplier = new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return currentBorderWidth;
            }
        };
        borderWidthUniform.update();

        borderColorUniform.supplier = new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return currentBorderColor;
            }
        };
        borderColorUniform.update();

        smoothnessUniform.supplier = new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return currentSmoothness;
            }
        };
        smoothnessUniform.update();
    }
}