package com.example.examplemod.gui;


import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import static com.example.examplemod.gui.UIHelper.Object.horizontalCenterObject;
import static com.example.examplemod.gui.UIHelper.Object.verticalCenterObject;


public class UIPanel {
    public float x, y, x2, y2;
    private float buildX, buildY, buildX2, buildY2;
    public float xCenterGui;
    public static float yCenterGroup;

    public final String title;
    public static final float SCALE = 2.0f;
    public static boolean init = false;
    /**
     * The panel is not to be initialized in pixels but in percent of the screen, the percent is to be initialized as an int value
     *
     **/
    public UIPanel(int x, int y, int w, int h, String title) {
        init = true;
        this.x = this.buildX = x/100.0f;
        this.y = this.buildY = this.buildY2 = y/100.0f;
        this.x2 = this.buildX2 = x/100.0f + w/100.0f;
        this.y2 = y/100.0f + h/100.0f;
        this.title = title;
        this.xCenterGui = horizontalCenterObject(this.x, this.x2);
    }
    /**
     * (x1, y1, x2, y2) or (buildX, buildY, buildX2, buildY2)
     * Pass percentage of change
     **/
    private void buildCoordinateAdjustmentObject(float x1, float x2, float y1, float y2) {
        if (buildY != buildY2) {buildY = buildY2;}
        buildX += x1;
        buildY += y1;
        buildX2 += x2;
        buildY2 += y2;
    }
    private void resetBuild() {
        buildX = this.x;
        buildY = buildY2 = this.y;
        buildX2 = this.x2;
    }
    /**sw = scaledWidth; sh = scaledHeight**/
    public void render(FontRenderer fr, int sw, int sh) {
        resetBuild();
        // background
        net.minecraft.client.gui.Gui.drawRect(
                (int) (sw * x),
                (int) (sh * y),
                (int) (sw * x2),
                (int) (sh * y2),
                0x001E1E1E
        );
        // group top border line
        float adjustedY = 0.06f + ((float) fr.FONT_HEIGHT * SCALE / sh) / 2.0f;
        float yCenterTitel = verticalCenterObject(buildY, buildY + adjustedY);
        buildCoordinateAdjustmentObject(0f, 0f, adjustedY, adjustedY + 1.0f / sh);

        net.minecraft.client.gui.Gui.drawRect(
                (int) (sw * (buildX + 0.01f)),
                (int) (sh * buildY),
                (int) (sw * (buildX2 - 0.01f)),
                (int) (sh * buildY2),
                0xFF42A4F5
        );
        // group bottom border line
        adjustedY = 0.04f + ((float) fr.FONT_HEIGHT * UIGroup.SCALE / sh) / 2.0f;
        UIGroup.groupHeight = adjustedY;
        yCenterGroup = buildY2 + adjustedY / 2.0f;//verticalCenterObject(buildY2, buildY2 + adjustedY);
        buildCoordinateAdjustmentObject(0f, 0f, adjustedY, adjustedY + 1.0f / sh);
        net.minecraft.client.gui.Gui.drawRect(
                (int) (sw * (buildX + 0.01f)),
                (int) (sh * buildY),
                (int) (sw * (buildX2 - 0.01f)),
                (int) (sh * buildY2),
                0xFF42A4F5
        );

        UIGroup.settingY = buildY2;

        // title
        GL11.glPushMatrix();
        GL11.glScalef(SCALE, SCALE, SCALE);
        fr.drawStringWithShadow(
                title,
                (sw *(xCenterGui - ((float)fr.getStringWidth(title) * SCALE / sw) / 2.0f)) / SCALE,
                (sh *(yCenterTitel - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)) / SCALE,
                0xFFFFFF
        );
        GL11.glPopMatrix();
        // faux rounded corners/extra style could be added here

    }
}