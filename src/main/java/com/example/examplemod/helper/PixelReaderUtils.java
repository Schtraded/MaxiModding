package com.example.examplemod.helper;

import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class PixelReaderUtils {

    /**
     * Reads a single pixel color at the specified scaled screen coordinates
     * Returns color as ARGB integer (0xAARRGGBB)
     */
    public static int getPixelColor(int scaledX, int scaledY) {
        Minecraft mc = Minecraft.getMinecraft();

        // Convert scaled coordinates to actual framebuffer coordinates
        int actualX = (int)(scaledX * mc.displayWidth / (float)DisplayUtils.scaledWidth);
        int actualY = (int)(scaledY * mc.displayHeight / (float)DisplayUtils.scaledHeight);

        // Flip Y coordinate (OpenGL uses bottom-left origin, screen uses top-left)
        int flippedY = mc.displayHeight - actualY - 1;

        // Ensure OpenGL commands are flushed
        GL11.glFlush();
        GL11.glFinish();

        // Create buffer to store pixel data (4 bytes: RGBA)
        ByteBuffer buffer = BufferUtils.createByteBuffer(4);

        // Read single pixel
        GL11.glReadPixels(actualX, flippedY, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Extract RGBA values (0-255)
        int r = buffer.get(0) & 0xFF;
        int g = buffer.get(1) & 0xFF;
        int b = buffer.get(2) & 0xFF;
        int a = buffer.get(3) & 0xFF;

        // Return as ARGB integer
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Alternative method that takes actual framebuffer coordinates (not scaled)
     */
    public static int getPixelColorActual(int actualX, int actualY) {
        Minecraft mc = Minecraft.getMinecraft();

        // Flip Y coordinate (OpenGL uses bottom-left origin, screen uses top-left)
        int flippedY = mc.displayHeight - actualY - 1;

        // Ensure OpenGL commands are flushed
        GL11.glFlush();
        GL11.glFinish();

        // Create buffer to store pixel data (4 bytes: RGBA)
        ByteBuffer buffer = BufferUtils.createByteBuffer(4);

        // Read single pixel
        GL11.glReadPixels(actualX, flippedY, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Extract RGBA values (0-255)
        int r = buffer.get(0) & 0xFF;
        int g = buffer.get(1) & 0xFF;
        int b = buffer.get(2) & 0xFF;
        int a = buffer.get(3) & 0xFF;

        // Return as ARGB integer
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Reads a single pixel color and returns it as separate RGBA components
     * Uses scaled coordinates
     */
    public static int[] getPixelColorRGBA(int scaledX, int scaledY) {
        Minecraft mc = Minecraft.getMinecraft();

        // Convert scaled coordinates to actual framebuffer coordinates
        int actualX = (int)(scaledX * mc.displayWidth / (float)DisplayUtils.scaledWidth);
        int actualY = (int)(scaledY * mc.displayHeight / (float)DisplayUtils.scaledHeight);
        int flippedY = mc.displayHeight - actualY - 1;

        // Ensure OpenGL commands are flushed
        GL11.glFlush();
        GL11.glFinish();

        ByteBuffer buffer = BufferUtils.createByteBuffer(4);
        GL11.glReadPixels(actualX, flippedY, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        return new int[] {
                buffer.get(0) & 0xFF, // Red
                buffer.get(1) & 0xFF, // Green
                buffer.get(2) & 0xFF, // Blue
                buffer.get(3) & 0xFF  // Alpha
        };
    }

    /**
     * Reads a rectangular area of pixels
     * Returns 2D array where [y][x] contains the ARGB color
     */
    public static int[][] getPixelArea(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int flippedY = mc.displayHeight - y - height;

        // Ensure OpenGL commands are flushed
        GL11.glFlush();
        GL11.glFinish();

        // Create buffer for entire area (4 bytes per pixel)
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        // Read pixel area
        GL11.glReadPixels(x, flippedY, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        int[][] pixels = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = (row * width + col) * 4;

                int r = buffer.get(index) & 0xFF;
                int g = buffer.get(index + 1) & 0xFF;
                int b = buffer.get(index + 2) & 0xFF;
                int a = buffer.get(index + 3) & 0xFF;

                // Flip Y coordinate in the result array
                pixels[height - 1 - row][col] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        return pixels;
    }

    /**
     * Alternative method using IntBuffer for potentially better performance
     */
    public static int getPixelColorFast(int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        int flippedY = mc.displayHeight - y - 1;

        // Ensure OpenGL commands are flushed
        GL11.glFlush();
        GL11.glFinish();

        IntBuffer buffer = BufferUtils.createIntBuffer(1);

        // Read as packed integer (BGRA format on most systems)
        GL11.glReadPixels(x, flippedY, 1, 1, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);

        int bgra = buffer.get(0);

        // Convert BGRA to ARGB
        int b = (bgra >> 24) & 0xFF;
        int g = (bgra >> 16) & 0xFF;
        int r = (bgra >> 8) & 0xFF;
        int a = bgra & 0xFF;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Utility method to convert ARGB integer back to readable format
     */
    public static String colorToString(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        return String.format("RGBA(%d, %d, %d, %d) [0x%08X]", r, g, b, a, argb);
    }

    /**
     * Check if a pixel matches a specific color (with tolerance)
     */
    public static boolean isPixelColor(int x, int y, int expectedColor, int tolerance) {
        int actualColor = getPixelColor(x, y);

        int aExpected = (expectedColor >> 24) & 0xFF;
        int rExpected = (expectedColor >> 16) & 0xFF;
        int gExpected = (expectedColor >> 8) & 0xFF;
        int bExpected = expectedColor & 0xFF;

        int aActual = (actualColor >> 24) & 0xFF;
        int rActual = (actualColor >> 16) & 0xFF;
        int gActual = (actualColor >> 8) & 0xFF;
        int bActual = actualColor & 0xFF;

        return Math.abs(aExpected - aActual) <= tolerance &&
                Math.abs(rExpected - rActual) <= tolerance &&
                Math.abs(gExpected - gActual) <= tolerance &&
                Math.abs(bExpected - bActual) <= tolerance;
    }

    /**
     * Sample colors along a line (useful for analyzing gradients)
     */
    public static int[] sampleLine(int x1, int y1, int x2, int y2, int numSamples) {
        int[] colors = new int[numSamples];

        for (int i = 0; i < numSamples; i++) {
            float t = (float) i / (numSamples - 1);
            int x = (int) (x1 + t * (x2 - x1));
            int y = (int) (y1 + t * (y2 - y1));
            colors[i] = getPixelColor(x, y);
        }

        return colors;
    }

    /**
     * Debug method to print pixel color information with coordinate conversion
     */
    public static void debugPixelColor(int scaledX, int scaledY) {
        Minecraft mc = Minecraft.getMinecraft();
        int actualX = (int)(scaledX * mc.displayWidth / (float)DisplayUtils.scaledWidth);
        int actualY = (int)(scaledY * mc.displayHeight / (float)DisplayUtils.scaledHeight);

        int color = getPixelColor(scaledX, scaledY);
        System.out.println("Scaled coords: (" + scaledX + ", " + scaledY + ") -> " +
                "Actual coords: (" + actualX + ", " + actualY + ") -> " +
                "Color: " + colorToString(color));
    }

    /**
     * Utility to get scaling factors
     */
    public static float[] getScalingFactors() {
        Minecraft mc = Minecraft.getMinecraft();
        float scaleX = mc.displayWidth / (float)DisplayUtils.scaledWidth;
        float scaleY = mc.displayHeight / (float)DisplayUtils.scaledHeight;
        return new float[]{scaleX, scaleY};
    }
}