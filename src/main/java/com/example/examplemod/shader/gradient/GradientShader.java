package com.example.examplemod.shader.gradient;

import com.example.examplemod.shader.Shader;
import com.example.examplemod.shader.Uniform;

public class GradientShader extends Shader {

    public static final GradientShader INSTANCE = new GradientShader();

    // Shader uniforms
    private float[] position = new float[2]; // x, y position (top-left)
    private float[] size = new float[2]; // width, height
    private float[] topLeftColor = new float[4]; // RGBA
    private float[] topRightColor = new float[4]; // RGBA
    private float[] bottomLeftColor = new float[4]; // RGBA
    private float[] bottomRightColor = new float[4]; // RGBA

    private GradientShader() {
        super("gradient");
    }

    @Override
    protected void setupUniforms() {
        // Position uniform (top-left of rectangle)
        addUniform(new Uniform.Vec2(this, "u_position", () -> position));

        // Size uniform (width, height)
        addUniform(new Uniform.Vec2(this, "u_size", () -> size));

        // Corner colors
        addUniform(new Uniform.Vec4(this, "u_topLeftColor", () -> topLeftColor));
        addUniform(new Uniform.Vec4(this, "u_topRightColor", () -> topRightColor));
        addUniform(new Uniform.Vec4(this, "u_bottomLeftColor", () -> bottomLeftColor));
        addUniform(new Uniform.Vec4(this, "u_bottomRightColor", () -> bottomRightColor));
    }

    @Override
    public void updateUniforms() {
        for (Uniform<?> uniform : uniforms) {
            uniform.update();
        }
    }

    // Setter methods
    public void setPosition(float x, float y) {
        this.position[0] = x;
        this.position[1] = y;
    }

    public void setSize(float width, float height) {
        this.size[0] = width;
        this.size[1] = height;
    }

    public void setTopLeftColor(float r, float g, float b, float a) {
        this.topLeftColor[0] = r;
        this.topLeftColor[1] = g;
        this.topLeftColor[2] = b;
        this.topLeftColor[3] = a;
    }

    public void setTopRightColor(float r, float g, float b, float a) {
        this.topRightColor[0] = r;
        this.topRightColor[1] = g;
        this.topRightColor[2] = b;
        this.topRightColor[3] = a;
    }

    public void setBottomLeftColor(float r, float g, float b, float a) {
        this.bottomLeftColor[0] = r;
        this.bottomLeftColor[1] = g;
        this.bottomLeftColor[2] = b;
        this.bottomLeftColor[3] = a;
    }

    public void setBottomRightColor(float r, float g, float b, float a) {
        this.bottomRightColor[0] = r;
        this.bottomRightColor[1] = g;
        this.bottomRightColor[2] = b;
        this.bottomRightColor[3] = a;
    }

    // Convenience methods to set colors from int
    public void setTopLeftColor(int color) {
        setColorFromInt(color, topLeftColor);
    }

    public void setTopRightColor(int color) {
        setColorFromInt(color, topRightColor);
    }

    public void setBottomLeftColor(int color) {
        setColorFromInt(color, bottomLeftColor);
    }

    public void setBottomRightColor(int color) {
        setColorFromInt(color, bottomRightColor);
    }

    private void setColorFromInt(int color, float[] targetArray) {
        targetArray[3] = ((color >> 24) & 0xFF) / 255.0f; // Alpha
        targetArray[0] = ((color >> 16) & 0xFF) / 255.0f; // Red
        targetArray[1] = ((color >> 8) & 0xFF) / 255.0f;  // Green
        targetArray[2] = (color & 0xFF) / 255.0f;         // Blue
    }
}