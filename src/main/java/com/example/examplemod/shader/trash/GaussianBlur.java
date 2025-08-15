package com.example.examplemod.shader.trash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;


public class GaussianBlur {

    private Minecraft mc = Minecraft.getMinecraft();
    private Framebuffer framebuffer;
    private int shaderHorizontal;
    private int shaderVertical;
    private int width, height;

    public GaussianBlur(int width, int height) {
        this.width = width;
        this.height = height;
        framebuffer = new Framebuffer(width, height, true);

        shaderHorizontal = createShader(horizontalFragmentShader);
        shaderVertical = createShader(verticalFragmentShader);
    }

    // Call this every frame to apply blur
    public void applyBlur(float radius) {
        // Capture screen into framebuffer
        captureScreen();

        // Render scene here if needed

        //Horizontal blur pass
        GL20.glUseProgram(shaderHorizontal);
        GL20.glUniform1f(GL20.glGetUniformLocation(shaderHorizontal, "resolution"), (float) width);
        GL20.glUniform1f(GL20.glGetUniformLocation(shaderHorizontal, "radius"), radius);
        renderFullscreenQuad(framebuffer.framebufferTexture);

        //Vertical blur pass
        GL20.glUseProgram(shaderVertical);
        GL20.glUniform1f(GL20.glGetUniformLocation(shaderVertical, "resolution"), (float) height);
        GL20.glUniform1f(GL20.glGetUniformLocation(shaderVertical, "radius"), radius);
        renderFullscreenQuad(framebuffer.framebufferTexture);

        //Reset shader
        GL20.glUseProgram(0);
        mc.getFramebuffer().bindFramebuffer(true);
    }

    public void captureScreen() {
        mc.getFramebuffer().bindFramebufferTexture(); // bind main screen texture
        framebuffer.bindFramebuffer(true);            // bind our framebuffer

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        renderFullscreenQuad(mc.getFramebuffer().framebufferTexture);

        framebuffer.unbindFramebuffer(); // done copying
    }

    private void renderFullscreenQuad(int texture) {
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(texture);
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer renderer = tess.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(-1, -1, 0).tex(0, 0).endVertex();
        renderer.pos(1, -1, 0).tex(1, 0).endVertex();
        renderer.pos(1, 1, 0).tex(1, 1).endVertex();
        renderer.pos(-1, 1, 0).tex(0, 1).endVertex();
        tess.draw();
        GlStateManager.bindTexture(0);
    }

    private int createShader(String fragmentShaderSource) {
        int shader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(shader, fragmentShaderSource);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + GL20.glGetShaderInfoLog(shader, 500));
        }
        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, shader);
        GL20.glLinkProgram(program);
        return program;
    }

    // Horizontal blur GLSL
    private static final String horizontalFragmentShader =
            "#version 120\n" +
                    "uniform sampler2D texture;\n" +
                    "uniform float resolution;\n" +
                    "uniform float radius;\n" +
                    "varying vec2 texCoord;\n" +
                    "void main() {\n" +
                    "    vec4 sum = vec4(0.0);\n" +
                    "    float weights[5];\n" +
                    "    weights[0] = 0.204164;\n" +
                    "    weights[1] = 0.304005;\n" +
                    "    weights[2] = 0.093913;\n" +
                    "    weights[3] = 0.010381;\n" +
                    "    weights[4] = 0.000229;\n" +
                    "    for (int i = -4; i <= 4; i++) {\n" +
                    "        float weight = weights[int(abs(float(i)))];\n" +
                    "        vec2 offset = vec2(i / resolution * radius, 0.0);\n" +
                    "        sum += texture2D(texture, texCoord + offset) * weight;\n" +
                    "    }\n" +
                    "    gl_FragColor = sum;\n" +
                    "}";

    // Vertical blur GLSL
    private static final String verticalFragmentShader =
            "#version 120\n" +
                    "uniform sampler2D texture;\n" +
                    "uniform float resolution;\n" +
                    "uniform float radius;\n" +
                    "varying vec2 texCoord;\n" +
                    "void main() {\n" +
                    "    vec4 sum = vec4(0.0);\n" +
                    "    float weights[5];\n" +
                    "    weights[0] = 0.204164;\n" +
                    "    weights[1] = 0.304005;\n" +
                    "    weights[2] = 0.093913;\n" +
                    "    weights[3] = 0.010381;\n" +
                    "    weights[4] = 0.000229;\n" +
                    "    for (int i = -4; i <= 4; i++) {\n" +
                    "        float weight = weights[int(abs(float(i)))];\n" +
                    "        vec2 offset = vec2(0.0, i / resolution * radius);\n" +
                    "        sum += texture2D(texture, texCoord + offset) * weight;\n" +
                    "    }\n" +
                    "    gl_FragColor = sum;\n" +
                    "}";
}
