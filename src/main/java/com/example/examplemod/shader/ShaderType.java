package com.example.examplemod.shader;

public enum ShaderType {
    VERTEX(".vsh", ShaderHelper.GL_VERTEX_SHADER),
    FRAGMENT(".fsh", ShaderHelper.GL_FRAGMENT_SHADER);

    public final String extension;
    public final int shaderType;

    ShaderType(String extension, int shaderType) {
        this.extension = extension;
        this.shaderType = shaderType;
    }
}
