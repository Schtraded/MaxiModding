package com.example.examplemod.shader.sdf;

import com.example.examplemod.shader.Shader;
import com.example.examplemod.shader.Uniform;
import net.minecraft.client.Minecraft;

public class SDFShader extends Shader {

    public static final SDFShader INSTANCE = new SDFShader();

    private final Uniform.Vec2 resolutionUniform;
    private final Uniform.Float timeUniform;
    private final Uniform.Vec2 positionUniform;
    private final Uniform.Vec2 sizeUniform;
    private final Uniform.Vec4 cornersUniform;
    private final Uniform.Vec4 colorUniform;
    private final Uniform.Vec4 backgroundColorUniform;
    private final Uniform.Vec4 borderColorUniform;
    private final Uniform.Float borderWidthUniform;

    private float[] position = {0.0f, 0.0f};
    private float[] size = {0.5f, 0.3f};
    private float[] corners = {0.1f, 0.1f, 0.1f, 0.1f}; // top-right, bottom-right, bottom-left, top-left
    private float[] color = {0.65f, 0.85f, 1.0f, 1.0f};
    private float[] backgroundColor = {0.9f, 0.6f, 0.3f, 1.0f};
    private float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f}; // Default white border
    private float borderWidth = 0.0f; // Default no border

    private SDFShader() {
        super("sdf");

        resolutionUniform = new Uniform.Vec2(this, "iResolution", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                Minecraft mc = Minecraft.getMinecraft();
                return new float[]{mc.displayWidth, mc.displayHeight};
            }
        });

        timeUniform = new Uniform.Float(this, "iTime", new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return (float)(System.currentTimeMillis() % 1000000) / 1000.0f;
            }
        });

        positionUniform = new Uniform.Vec2(this, "uPosition", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return position;
            }
        });

        sizeUniform = new Uniform.Vec2(this, "uSize", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return size;
            }
        });

        cornersUniform = new Uniform.Vec4(this, "uCorners", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return corners;
            }
        });

        colorUniform = new Uniform.Vec4(this, "uColor", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return color;
            }
        });

        backgroundColorUniform = new Uniform.Vec4(this, "uBackgroundColor", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return backgroundColor;
            }
        });

        borderColorUniform = new Uniform.Vec4(this, "uBorderColor", new Uniform.UniformSupplier<float[]>() {
            @Override
            public float[] get() {
                return borderColor;
            }
        });

        borderWidthUniform = new Uniform.Float(this, "uBorderWidth", new Uniform.UniformSupplier<Float>() {
            @Override
            public Float get() {
                return borderWidth;
            }
        });

        addUniform(resolutionUniform);
        addUniform(timeUniform);
        addUniform(positionUniform);
        addUniform(sizeUniform);
        addUniform(cornersUniform);
        addUniform(colorUniform);
        addUniform(backgroundColorUniform);
        addUniform(borderColorUniform);
        addUniform(borderWidthUniform);
    }

    @Override
    public void updateUniforms() {
        resolutionUniform.update();
        timeUniform.update();
        positionUniform.update();
        sizeUniform.update();
        cornersUniform.update();
        colorUniform.update();
        backgroundColorUniform.update();
        borderColorUniform.update();
        borderWidthUniform.update();
    }

    // Existing setters
    public void setPosition(float x, float y) {
        this.position[0] = x;
        this.position[1] = y;
    }

    public void setSize(float width, float height) {
        this.size[0] = width;
        this.size[1] = height;
    }

    public void setCorners(float topRight, float bottomRight, float bottomLeft, float topLeft) {
        this.corners[0] = topRight;
        this.corners[1] = bottomRight;
        this.corners[2] = bottomLeft;
        this.corners[3] = topLeft;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        this.backgroundColor[0] = r;
        this.backgroundColor[1] = g;
        this.backgroundColor[2] = b;
        this.backgroundColor[3] = a;
    }

    // New border setters
    public void setBorderColor(float r, float g, float b, float a) {
        this.borderColor[0] = r;
        this.borderColor[1] = g;
        this.borderColor[2] = b;
        this.borderColor[3] = a;
    }

    public void setBorderWidth(float width) {
        this.borderWidth = width;
    }

    // Convenience method to set border with color
    public void setBorder(float width, float r, float g, float b, float a) {
        setBorderWidth(width);
        setBorderColor(r, g, b, a);
    }

    // Method to disable border
    public void disableBorder() {
        this.borderWidth = 0.0f;
    }

    // Getters for current values
    public float getBorderWidth() {
        return borderWidth;
    }

    public float[] getBorderColor() {
        return borderColor.clone();
    }
}