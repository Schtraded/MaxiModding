package com.example.examplemod.gui;

import net.minecraft.client.gui.FontRenderer;

public class UIHelper {
    public static final float POSITION_CORRECTION = 1.15f;
    public static class Object {
        public static float centeredX, centeredY;
        public static void verticalAndHorizontalCenter(float x1, float y1, float x2, float y2) {
            centeredX = horizontalCenterObject(x1, x2);
            centeredY = verticalCenterObject(y1, y2);
        }
        public static float horizontalCenterObject(float x1, float x2) {
            if (x2 < x1) {
                float tempX1 = x1;
                x1 = x2;
                x2 = tempX1;
            }
            return (x2 - x1) / 2.0f ;
        }
        public static float verticalCenterObject(float y1, float y2) {
            if (y2 < y1) {
                float tempY1 = y1;
                y1 = y2;
                y2 = tempY1;
            }
            return (y2 - y1) / 2.0f ;
        }
    }
    public class Text {
        private float centeredX, centeredY;
        public void verticalAndHorizontalCenter(float x1, float y1, float x2, float y2,
                                                float scale, FontRenderer fr, String text,
                                                int sw, int sh
        ) {
            horizontalCenter(x1, x2, scale, fr.getStringWidth(text), sw);
            verticalCenter(y1, y2, scale, fr.FONT_HEIGHT, sh);
        }
        public void horizontalCenter(float x1, float x2, float scale, int fontWidth, int sw) {
            if (x2 < x1) {
                float tempX1 = x1;
                x1 = x2;
                x2 = tempX1;
            }
            this.centeredX = (x2 - x1 - (float) fontWidth / (float) sw * scale) / 2.0f ;
        }
        public void verticalCenter(float y1, float y2, float scale, int fontHeight, int sh) {
            if (y2 < y1) {
                float tempY1 = y1;
                y1 = y2;
                y2 = tempY1;
            }
            this.centeredY = (y2 - y1 - (float) fontHeight / (float) sh * scale) / 2.0f ;
        }
    }
}
