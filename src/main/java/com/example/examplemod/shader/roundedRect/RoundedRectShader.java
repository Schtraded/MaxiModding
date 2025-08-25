package com.example.examplemod.shader.roundedRect;

import com.example.examplemod.shader.Shader;
import com.example.examplemod.shader.Uniform;
import net.minecraft.client.Minecraft;

public class RoundedRectShader extends Shader {

    public static final RoundedRectShader INSTANCE = new RoundedRectShader();

    private final Uniform.Vec2 resolutionUniform;
    private final Uniform.Vec2 positionUniform;
    private final Uniform.Vec2 sizeUniform;
    private final Uniform.Vec4 cornerRadiiUniform;
    private final Uniform.Vec4 colorUniform;
    private final Uniform.Float borderWidthUniform;
    private final Uniform.Vec4 borderColorUniform;
    private final Uniform.Float smoothnessUniform;

    private RoundedRectShader() {
        super("rounded_rect");

        // Screen resolution
        resolutionUniform = new Uniform.Vec2(this, "resolution", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight};
            }
        });

        // Rectangle position (top-left corner in screen coordinates, 0-1 range)
        positionUniform = new Uniform.Vec2(this, "position", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{0.25f, 0.25f}; // Default position
            }
        });

        // Rectangle size (width, height in screen space, 0-1 range)
        sizeUniform = new Uniform.Vec2(this, "size", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{0.5f, 0.3f}; // Default size
            }
        });

        // Corner radii (top-left, top-right, bottom-right, bottom-left)
        cornerRadiiUniform = new Uniform.Vec4(this, "cornerRadii", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return new float[]{0.05f, 0.05f, 0.05f, 0.05f}; // Default uniform radius
            }
        });

        // Fill color (RGBA)
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
                return 0.01f; // Default border width
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
                return 0.002f; // Default smoothness for anti-aliasing
            }
        });

        addUniform(resolutionUniform);
        addUniform(positionUniform);
        addUniform(sizeUniform);
        addUniform(cornerRadiiUniform);
        addUniform(colorUniform);
        addUniform(borderWidthUniform);
        addUniform(borderColorUniform);
        addUniform(smoothnessUniform);
    }

    // Dynamic values
    private float[] currentPosition = {0.25f, 0.25f};
    private float[] currentSize = {0.5f, 0.3f};
    private float[] currentCornerRadii = {0.05f, 0.05f, 0.05f, 0.05f};
    private float[] currentColor = {1.0f, 1.0f, 1.0f, 1.0f};
    private float currentBorderWidth = 0.01f;
    private float[] currentBorderColor = {0.0f, 0.0f, 0.0f, 1.0f};
    private float currentSmoothness = 0.002f;

    public void setPosition(float x, float y) {
        currentPosition[0] = x;
        currentPosition[1] = y;
    }

    public void setSize(float width, float height) {
        currentSize[0] = width;
        currentSize[1] = height;
    }

    // Set uniform radius for all corners (percentage of smaller dimension)
    public void setCornerRadius(float radiusPercent) {
        float actualRadius = convertPercentToRadius(radiusPercent);
        currentCornerRadii[0] = actualRadius;
        currentCornerRadii[1] = actualRadius;
        currentCornerRadii[2] = actualRadius;
        currentCornerRadii[3] = actualRadius;
    }

    // Set individual corner radii as percentages (top-left, top-right, bottom-right, bottom-left)
    public void setCornerRadii(float topLeftPercent, float topRightPercent, float bottomRightPercent, float bottomLeftPercent) {
        currentCornerRadii[0] = convertPercentToRadius(topLeftPercent);
        currentCornerRadii[1] = convertPercentToRadius(topRightPercent);
        currentCornerRadii[2] = convertPercentToRadius(bottomRightPercent);
        currentCornerRadii[3] = convertPercentToRadius(bottomLeftPercent);
    }

    // Set uniform radius for all corners (absolute value in screen space)
    public void setCornerRadiusAbsolute(float radius) {
        currentCornerRadii[0] = radius;
        currentCornerRadii[1] = radius;
        currentCornerRadii[2] = radius;
        currentCornerRadii[3] = radius;
    }

    // Set individual corner radii as absolute values (top-left, top-right, bottom-right, bottom-left)
    public void setCornerRadiiAbsolute(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        currentCornerRadii[0] = topLeft;
        currentCornerRadii[1] = topRight;
        currentCornerRadii[2] = bottomRight;
        currentCornerRadii[3] = bottomLeft;
    }

    // Helper method to convert percentage to actual radius
    private float convertPercentToRadius(float percent) {
        // 100% = half the smaller dimension (creates perfect circle/oval)
        // 50% = quarter the smaller dimension
        float smallerDimension = Math.min(currentSize[0], currentSize[1]);
        return (percent / 100.0f) * (smallerDimension / 2.0f);
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

        positionUniform.supplier = new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return currentPosition;
            }
        };
        positionUniform.update();

        sizeUniform.supplier = new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return currentSize;
            }
        };
        sizeUniform.update();

        cornerRadiiUniform.supplier = new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return currentCornerRadii;
            }
        };
        cornerRadiiUniform.update();

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
