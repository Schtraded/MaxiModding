package com.example.examplemod.shader;

import org.lwjgl.opengl.GL20;

public abstract class Uniform<T> {

    protected final Shader shader;
    protected final String name;
    protected final UniformSupplier<T> supplier;
    protected int location = -1;

    public Uniform(Shader shader, String name, UniformSupplier<T> supplier) {
        this.shader = shader;
        this.name = name;
        this.supplier = supplier;
    }

    public void update() {
        if (location == -1) {
            location = GL20.glGetUniformLocation(shader.shaderProgram, name);
        }
        if (location != -1) {
            setValue(supplier.get());
        }
    }

    protected abstract void setValue(T value);

    public static class Float extends Uniform<java.lang.Float> {
        public Float(Shader shader, String name, UniformSupplier<java.lang.Float> supplier) {
            super(shader, name, supplier);
        }

        @Override
        protected void setValue(java.lang.Float value) {
            GL20.glUniform1f(location, value);
        }
    }

    public static class Vec2 extends Uniform<float[]> {
        public Vec2(Shader shader, String name, UniformSupplier<float[]> supplier) {
            super(shader, name, supplier);
        }

        @Override
        protected void setValue(float[] value) {
            if (value.length >= 2) {
                GL20.glUniform2f(location, value[0], value[1]);
            }
        }
    }

    public interface UniformSupplier<T> {
        T get();
    }
}