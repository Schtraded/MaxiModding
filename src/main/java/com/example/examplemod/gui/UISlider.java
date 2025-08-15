package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class UISlider extends UIWidget {
    private final String text;
    public boolean isHovered;
    public boolean isDragging;
    private float value;
    private final float min, max;
    public static int activeWindow = UIGroup.activeWindow;
    public int belongsToGroupNum;
    public int instanceNum;

    public static float SCALE = 1.0f;
    private static final float SLIDER_HEIGHT = 0.02f; // 2% of screen height
    private static final float SLIDER_WIDTH = 0.2f;   // 20% of screen width

    public UISlider(String label, String groupLabel, float min, float max, float initialValue) {
        this.text = label;
        this.min = min;
        this.max = max;
        this.value = Math.max(min, Math.min(max, initialValue));
        this.isDragging = false;

        int indexOfLabel = UIGroup.instances.indexOf(groupLabel);
        if (indexOfLabel == -1) {
            System.out.println("[MAXIMOD][WARNING] UISlider " + this.text + " doesn't belong to any group");
            return;
        }

        this.belongsToGroupNum = indexOfLabel + 1;

        int oldValue = UIGroup.widgetCounter.get(this.belongsToGroupNum - 1);
        this.instanceNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum - 1, oldValue + 1);

        float spaceBetweenWidgets = 0.04f; // More space for slider
        UIGroup.widgetY.get(this.belongsToGroupNum - 1).add(spaceBetweenWidgets);
    }

    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            float buildYBottom = UIGroup.settingY;
            for (int i = 0; i < instanceNum; i++) {
                buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(i);
            }
            buildYBottom += instanceNum * ((float)fr.FONT_HEIGHT * SCALE / sh);

            float labelY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(instanceNum - 1) / 2.0f;
            float sliderY = labelY + ((float)fr.FONT_HEIGHT * SCALE / sh);

            // Draw label
            fr.drawStringWithShadow(
                    text,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (labelY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            // Calculate slider dimensions
            int sliderStartX = (int) ((float)sw * UIGroup.settingX);
            int sliderEndX = (int) ((float)sw * UIGroup.settingX2);
            int sliderWidth = sliderEndX - sliderStartX;
            int sliderStartY = (int) ((float)sh * sliderY);
            int sliderHeight = (int) ((float)sh * SLIDER_HEIGHT);

            // Draw slider background
            net.minecraft.client.gui.Gui.drawRect(
                    sliderStartX, sliderStartY,
                    sliderEndX, sliderStartY + sliderHeight,
                    isHovered ? 0xFF555555 : 0xFF333333
            );

            // Draw slider fill
            float valuePercent = (value - min) / (max - min);
            int fillWidth = (int) (sliderWidth * valuePercent);
            net.minecraft.client.gui.Gui.drawRect(
                    sliderStartX, sliderStartY,
                    sliderStartX + fillWidth, sliderStartY + sliderHeight,
                    isDragging ? 0xFF4A9EFF : 0xFF42A4F5
            );

            // Draw slider handle
            int handleX = sliderStartX + fillWidth - 3;
            int handleWidth = 6;
            net.minecraft.client.gui.Gui.drawRect(
                    handleX, sliderStartY - 2,
                    handleX + handleWidth, sliderStartY + sliderHeight + 2,
                    0xFFFFFFFF
            );

            // Draw value text
            String valueText = String.format("%.2f", value);
            fr.drawStringWithShadow(
                    valueText,
                    sliderEndX + 10,
                    (int) ((float)sh * (sliderY + SLIDER_HEIGHT / 2.0f - ((float)fr.FONT_HEIGHT * SCALE / sh) / 2.0f)),
                    0xFFFFFF
            );
        }
    }

    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        if (belongsToGroupNum != UIGroup.activeWindow) {
            isHovered = false;
            return false;
        }

        float buildYBottom = UIGroup.settingY;
        for (int i = 0; i < instanceNum; i++) {
            buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(i);
        }
        buildYBottom += instanceNum * ((float)Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * SCALE / sh);

        float labelY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(instanceNum - 1) / 2.0f;
        float sliderY = labelY + ((float)Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * SCALE / sh);

        int sliderStartX = (int) ((float)sw * UIGroup.settingX);
        int sliderEndX = (int) ((float)sw * UIGroup.settingX2);
        int sliderStartY = (int) ((float)sh * sliderY);
        int sliderHeight = (int) ((float)sh * SLIDER_HEIGHT);

        isHovered = (mx >= sliderStartX && mx <= sliderEndX &&
                my >= sliderStartY - 2 && my <= sliderStartY + sliderHeight + 2);
        return isHovered;
    }

    @Override
    public void onMousePressed(int mx, int my, int button) {
        if (button == 0 && isHovered) {
            isDragging = true;
            updateValueFromMouse(mx);
        }
    }

    @Override
    public void onMouseReleased(int mx, int my, int button) {
        if (button == 0) {
            isDragging = false;
        }
    }

    public void onDrag(int mx, int my) {
        if (isDragging) {
            updateValueFromMouse(mx);
        }
    }

    private void updateValueFromMouse(int mx) {
        // Calculate mouse position relative to slider
        int sliderStartX = (int) ((float) Minecraft.getMinecraft().displayWidth * UIGroup.settingX / Minecraft.getMinecraft().currentScreen.width);
        int sliderEndX = (int) ((float)Minecraft.getMinecraft().displayWidth * UIGroup.settingX2 / Minecraft.getMinecraft().currentScreen.width);
        int sliderWidth = sliderEndX - sliderStartX;

        float percent = (float) (mx - sliderStartX) / sliderWidth;
        percent = Math.max(0.0f, Math.min(1.0f, percent));

        value = min + percent * (max - min);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(min, Math.min(max, value));
    }
}