package com.example.examplemod.shader;

import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShaderManager {

    private static Shader activeShader;

    public enum ShaderTypeInternal {
        GAUSSIAN_BLUR(GaussianBlurShader.INSTANCE);

        private final Shader shader;

        ShaderTypeInternal(Shader shader) {
            this.shader = shader;
        }

        public void enable() {
            ShaderManager.enableShader(this.shader);
        }
    }

    public static void attachShader(int program, int shader) {
        ShaderHelper.glAttachShader(program, shader);
    }

    public static void enableShader(Shader shader) {
        if (!shader.created) return;
        activeShader = shader;
        shader.enable();
        shader.updateUniforms();
    }

    public static void disableShader() {
        if (activeShader != null) {
            activeShader.disable();
            activeShader = null;
        }
    }

    public static int loadShader(ShaderType type, String fileName) {
        try {
            StringBuilder source = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Minecraft.getMinecraft().getResourceManager()
                            .getResource(new net.minecraft.util.ResourceLocation("examplemod", "shaders/" + fileName + type.extension))
                            .getInputStream()
            ));

            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line).append("\n");
            }

            int shaderID = ShaderHelper.glCreateShader(type.shaderType);
            ShaderHelper.glShaderSource(shaderID, source.toString());
            ShaderHelper.glCompileShader(shaderID);

            if (ShaderHelper.glGetShaderInt(shaderID, ShaderHelper.GL_COMPILE_STATUS) == 0) {
                System.err.println("Shader compilation failed: " + fileName);
                System.err.println(ShaderHelper.glGetShaderInfoLog(shaderID, 1024));
                return -1;
            }
            return shaderID;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
