package com.example.examplemod.shader;

import org.lwjgl.opengl.GL20;

public class ShaderHelper {

    public static final int GL_COMPILE_STATUS = GL20.GL_COMPILE_STATUS;
    public static final int GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
    public static final int GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;

    public static int glCreateShader(int type) {
        return GL20.glCreateShader(type);
    }

    public static void glShaderSource(int shader, String source) {
        GL20.glShaderSource(shader, source);
    }

    public static void glCompileShader(int shader) {
        GL20.glCompileShader(shader);
    }

    public static int glGetShaderInt(int shader, int pname) {
        return GL20.glGetShaderi(shader, pname);
    }

    public static String glGetShaderInfoLog(int shader, int maxLength) {
        return GL20.glGetShaderInfoLog(shader, maxLength);
    }

    public static int glCreateProgram() {
        return GL20.glCreateProgram();
    }

    public static void glAttachShader(int program, int shader) {
        GL20.glAttachShader(program, shader);
    }

    public static void glLinkProgram(int program) {
        GL20.glLinkProgram(program);
    }

    public static void glUseProgram(int program) {
        GL20.glUseProgram(program);
    }

    public static int glGetUniformLocation(int program, String name) {
        return GL20.glGetUniformLocation(program, name);
    }

    public static void glUniform1f(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    public static void glUniform2f(int location, float x, float y) {
        GL20.glUniform2f(location, x, y);
    }
}

