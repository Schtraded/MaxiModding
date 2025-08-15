package com.example.examplemod.shader;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public abstract class Shader {

    public int shaderProgram; // Made public so subclasses can access it
    public boolean created; // Made public for external checks
    private final String shaderName;
    private final List<Uniform<?>> uniforms = new ArrayList<Uniform<?>>();

    public Shader(String shaderName) {
        this.shaderName = shaderName;
        createShader();
    }

    private void createShader() {
        int vertexShader = ShaderManager.loadShader(ShaderType.VERTEX, shaderName);
        int fragmentShader = ShaderManager.loadShader(ShaderType.FRAGMENT, shaderName);

        if (vertexShader == -1 || fragmentShader == -1) {
            System.err.println("Failed to load shaders for: " + shaderName);
            return;
        }

        shaderProgram = ShaderHelper.glCreateProgram();
        ShaderManager.attachShader(shaderProgram, vertexShader);
        ShaderManager.attachShader(shaderProgram, fragmentShader);
        ShaderHelper.glLinkProgram(shaderProgram);

        // Check linking status
        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == 0) {
            System.err.println("Shader program linking failed: " + shaderName);
            System.err.println(GL20.glGetProgramInfoLog(shaderProgram, 1024));
            return;
        }

        created = true;
    }

    public void enable() {
        if (created) {
            ShaderHelper.glUseProgram(shaderProgram);
            updateUniforms();
        }
    }

    public void disable() {
        ShaderHelper.glUseProgram(0);
        GL11.glFlush();
    }

    public abstract void updateUniforms();

    protected void addUniform(Uniform<?> uniform) {
        uniforms.add(uniform);
    }

    public int getShaderProgram() {
        return shaderProgram;
    }
}