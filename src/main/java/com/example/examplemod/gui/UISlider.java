// Modern UISlider.java
package com.example.examplemod.gui;

import com.example.examplemod.shader.sdf.SDFUtils;
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
    public int widgetNum;
    private final boolean isInteger;
    private final String suffix;

    public static float SCALE = 1.0f;
    private static final float SLIDER_HEIGHT = 0.003f; // Thin slider track
    private static final float HANDLE_SIZE = 0.012f;   // Circular handle size
    private static final float SLIDER_WIDTH = 0.2f;

    private float centerY;

    private int trackStartX;
    private int trackEndX;
    private int trackY;
    private int trackHeight;
    private int trackWidth;
    private int handleRadius;
    private int fillEndX;
    private String valueText;

    public UISlider(String label, String groupLabel, float min, float max, float initialValue) {
        this(label, groupLabel, min, max, initialValue, false, "");
    }

    public UISlider(String label, String groupLabel, float min, float max, float initialValue, boolean isInteger, String suffix) {
        this.text = label;
        this.min = min;
        this.max = max;
        this.value = Math.max(min, Math.min(max, initialValue));
        this.isDragging = false;
        this.isInteger = isInteger;
        this.suffix = suffix != null ? suffix : "";

        int indexOfLabel = UIGroup.instances.indexOf(groupLabel);
        if (indexOfLabel == -1) {
            System.out.println("[MAXIMOD][WARNING] UISlider " + this.text + " doesn't belong to any group");
            return;
        }

        this.belongsToGroupNum = indexOfLabel + 1;

        int oldValue = UIGroup.widgetCounter.get(this.belongsToGroupNum - 1);
        this.widgetNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum - 1, oldValue + 1);

        float spaceBetweenWidgets = 0.04f;
        UIGroup.widgetY.get(this.belongsToGroupNum - 1).add(spaceBetweenWidgets);

        updateTextValue();
    }

    public void update(FontRenderer fr, int sw, int sh) {
        //TODO: UPDATE WHEN SCREEN IS RESIZED OTHERWISE NOT
        float buildYBottom = UIGroup.widgetYPosition.get(this.belongsToGroupNum - 1).get(widgetNum - 1) + UIGroup.settingY;

        this.centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;

        // Calculate slider area (right side)
        final float sliderOffsetFromRight = 0.1f;
        float sliderStartX = UIGroup.settingX2 - SLIDER_WIDTH - sliderOffsetFromRight;
        float sliderEndX = UIGroup.settingX2 - sliderOffsetFromRight;
        float sliderY = centerY - SLIDER_HEIGHT * 0.5f;

        this.trackStartX = (int) ((float)sw * sliderStartX);
        this.trackEndX = (int) ((float)sw * sliderEndX);
        this.trackY = (int) ((float)sh * sliderY);
        this.trackHeight = Math.max(2, (int) ((float)sh * SLIDER_HEIGHT));
        this.trackWidth = this.trackEndX - this.trackStartX;

        float valuePercent = (value - min) / (max - min);
        this.fillEndX = trackStartX + (int) ((trackEndX - trackStartX) * valuePercent);

        this.handleRadius = (int) ((float)trackHeight * 1.5f);
    }

    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            update(fr, sw, sh);
            // Draw label on the left
            fr.drawStringWithShadow(
                    text,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            // Draw slider track background (dark)
            net.minecraft.client.gui.Gui.drawRect(
                    trackStartX, trackY - trackHeight/2,
                    trackEndX, trackY + trackHeight/2,
                    0xFF404040
            );

            // Draw slider track fill (colored)
            net.minecraft.client.gui.Gui.drawRect(
                    trackStartX,
                    trackY - trackHeight/2,
                    fillEndX,
                    trackY + trackHeight/2,
                    this.isDragging ? 0xFF2A66F5 : 0xFF42A4F5
            );

            // Draw circular handle
            // Handle shadow/border
            SDFUtils.drawCircle(fillEndX, trackY, handleRadius + 1, 0xFF2A2A2A);
            // Handle fill
            SDFUtils.drawCircle(fillEndX, trackY, handleRadius, isDragging ? 0xFF2A66F5 : 0xFFFFFFFF);
            // Handle inner highlight
            //SDFUtils.drawCircle(handleX, trackY, handleRadius - 2, 0xFFFFFFFF);

            fr.drawStringWithShadow(
                    valueText,
                    (int) ((float)sw * (UIGroup.settingX2 - 0.08f)),
                    (int) ((float)sh * (centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );
        }
    }

    //private void drawCircle(int centerX, int centerY, int radius, int color) {
    //    // Simple circle drawing using rectangles (pixelated but functional)
    //    for (int x = -radius; x <= radius; x++) {
    //        for (int y = -radius; y <= radius; y++) {
    //            if (x * x + y * y <= radius * radius) {
    //                net.minecraft.client.gui.Gui.drawRect(
    //                        centerX + x, centerY + y,
    //                        centerX + x + 1, centerY + y + 1,
    //                        color
    //                );
    //            }
    //        }
    //    }
    //}

    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        isHovered = (mx >= trackStartX - handleRadius && mx <= trackEndX + handleRadius &&
                my >= trackY - handleRadius && my <= trackY + handleRadius);
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

        float percent = (float) (mx - trackStartX) / trackWidth;
        percent = Math.max(0.0f, Math.min(1.0f, percent));

        float newValue = min + percent * (max - min);
        if (isInteger) {
            newValue = Math.round(newValue);
        }
        value = newValue;

        updateTextValue();
    }

    private void updateTextValue() {
        // Draw value text on the right
        if (isInteger) {
            valueText = String.valueOf((int)value) + suffix;
        } else {
            if (value == (int)value) {
                valueText = String.valueOf((int)value) + suffix;
            } else {
                valueText = String.format("%.1f", value) + suffix;
            }
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(min, Math.min(max, value));
    }
}