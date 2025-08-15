package com.example.examplemod.gui;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
public class UIGroup extends UIWidget {
    private final String text;
    public static final float SCALE = 1.5f;
    private static int instancesCount = 0;
    /**Start counting at 1, so first instance has the number 1**/
    private int instanceNum;
    public static float borderX, borderX2;
    public static float settingX, settingX2, settingY, settingY2;
    public float xCenterGroup, yCenterGroup;
    public static float avgGroupLength;
    public static float groupHeight;
    public boolean isHovered;
    public static int activeWindow;
    public static ArrayList<String> instances = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> widgetY = new ArrayList<>();
    public static ArrayList<Integer> widgetCounter = new ArrayList<>();
    /**The panel is not to be initialized in pixels but in percent of the screen, the percent is to be initialized as an int value**/
    public UIGroup(String text) {
        instances.add(text);
        widgetCounter.add(0);
        widgetY.add(new ArrayList<>());
        this.text = text;
        instancesCount++;
        instanceNum = instancesCount;
        if (!UIPanel.init) {
            System.out.println("[MAXIMOD][ERROR] UIPanel has not been yet initialized, please initialize first");
            return;
        }
        float borderLength = borderX2 - borderX;
        this.avgGroupLength = borderLength / (float) instancesCount;
        this.isHovered = false;
        this.activeWindow = 1;

        //TODO:for (ArrayList<Integer> innerList : widgetCounter) {
        //TODO:    for (Integer num : innerList) {
        //TODO:        System.out.println(num);
        //TODO:    }
        //TODO:}
    }
    /**sw = scaledWidth; sh = scaledHeight**/
    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        this.xCenterGroup = borderX + avgGroupLength * instanceNum - avgGroupLength / 2.0f;
        this.yCenterGroup = UIPanel.yCenterGroup;
        int color = isHovered ? 0xFFFFFF : 0x949494;
        GL11.glPushMatrix();
        GL11.glScalef(SCALE, SCALE, SCALE);
        fr.drawStringWithShadow(
                text,
                (int) ((float)sw * (xCenterGroup - ((float)fr.getStringWidth(text) * SCALE / sw) / 2.0f)) / SCALE,
                (int) ((float)sh * (yCenterGroup - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)) / SCALE,
                color
        );
        GL11.glPopMatrix();
    }
    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        int x = (int) (sw * (xCenterGroup - avgGroupLength / 2.0f));
        int x2 = (int) (sw * (xCenterGroup + avgGroupLength / 2.0f));
        int y = (int) (sh * (yCenterGroup - groupHeight / 2.0f));
        int y2 = (int) (sh * (yCenterGroup + groupHeight / 2.0f));
        isHovered = (mx >= x && mx <= x2 && my >= y && my <= y2);
        return isHovered;
    }

    @Override
    public void onMousePressed(int mx, int my, int button) {
        this.activeWindow = instanceNum;
    }
}