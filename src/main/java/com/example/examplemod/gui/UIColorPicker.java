package com.example.examplemod.gui;

import com.example.examplemod.helper.DisplayUtils;
import com.example.examplemod.shader.gradient.GradientUtils;
import com.example.examplemod.shader.sdf.SDFUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

public class UIColorPicker extends UIWidget {
    private final String label;
    private int selectedColor;
    public boolean isHovered;

    public static int activeWindow = UIGroup.activeWindow;
    public int belongsToGroupNum;
    public int widgetNum;

    public static float SCALE = 1.0f;
    private static final float COLOR_BOX_SIZE = 0.025f;

    // Predefined color palette
    private static final int[] COLOR_PALETTE = {
            0xFF0000, // Red
            0x00FF00, // Green
            0x0000FF, // Blue
            0xFFFF00, // Yellow
            0xFF00FF, // Magenta
            0x00FFFF, // Cyan
            0xFFFFFF, // White
            0x000000, // Black
            0xFF8000, // Orange
            0x8000FF, // Purple
            0x80FF00, // Lime
            0xFF0080, // Pink
    };

    private float centerY;

    private float colorPickerX;
    private float colorPickerY;
    private float colorPickerWidth;
    private float colorPickerHeight;
    private float colorSliderX;
    private float colorSliderY;
    private float colorSliderWidth;
    private float colorSliderHeight;
    private float selectedColorBoxX;
    private float selectedColorBoxY;
    private float selectedColorBoxWidth;
    private float selectedColorBoxHeight;
    private float borderThickness;
    private float selectedColorBoxLeft;
    private float selectedColorBoxTop;
    private float selectedColorBoxRight;
    private float selectedColorBoxBottom;
    private float borderThicknessPX;

    public ColorPickerGUI colorPickerTab;

    private final static float selectedColorBoxWidthScale = 5.0f;

    public UIColorPicker(String label, String groupLabel, int defaultColor) {
        this.label = label;
        this.selectedColor = defaultColor;

        int indexOfLabel = UIGroup.instances.indexOf(groupLabel);
        if (indexOfLabel == -1) {
            System.out.println("[MAXIMOD][WARNING] UIColorPicker " + this.label + " doesn't belong to any group");
            return;
        }

        this.belongsToGroupNum = indexOfLabel + 1;

        int oldValue = UIGroup.widgetCounter.get(this.belongsToGroupNum - 1);
        this.widgetNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum - 1, oldValue + 1);

        float spaceBetweenWidgets = 0.04f; // More space for color picker
        UIGroup.widgetY.get(this.belongsToGroupNum - 1).add(spaceBetweenWidgets);
    }

    public void update(FontRenderer fr, int sw, int sh) {
        //TODO: UPDATE WHEN SCREEN IS RESIZED OTHERWISE NOT
        float buildYBottom = UIGroup.widgetYPosition.get(this.belongsToGroupNum - 1).get(widgetNum - 1) + UIGroup.settingY;

        this.centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;

        this.colorPickerX = 0.1f * (float)sh;
        this.colorPickerY = 0.1f * (float)sh;
        this.colorPickerWidth = 0.1f * (float)sh;
        this.colorPickerHeight = 0.1f * (float)sh;

        this.colorSliderX = this.colorPickerX + 0.02f * (float)sh;
        this.colorSliderY = 0.1f * (float)sh;
        this.colorSliderWidth = 0.02f * (float)sh;
        this.colorSliderHeight = 0.02f * (float)sh;

        float textHeightPercent = (float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh;
        this.selectedColorBoxHeight = textHeightPercent * 1.0f;
        this.selectedColorBoxWidth = this.selectedColorBoxHeight * this.selectedColorBoxWidthScale;
        this.selectedColorBoxX = UIGroup.settingX2 - this.selectedColorBoxWidth / 2.0f - 0.02f;
        this.selectedColorBoxY = this.centerY - this.selectedColorBoxHeight / 2.0f;

        this.selectedColorBoxLeft = sw * this.selectedColorBoxX;
        this.selectedColorBoxTop = sh * this.selectedColorBoxY;
        this.selectedColorBoxRight = this.selectedColorBoxLeft + sh * this.selectedColorBoxWidth;
        this.selectedColorBoxBottom = this.selectedColorBoxTop + sh * this.selectedColorBoxHeight;

        this.borderThickness = this.selectedColorBoxHeight * 0.15f * 2.0f;
        this.borderThicknessPX = (float)sh * this.selectedColorBoxHeight * 0.15f * 2.0f;
    }

    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            update(fr, sw, sh);

            // Draw label
            fr.drawStringWithShadow(
                    this.label,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (this.centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            // Draw selected color border
            net.minecraft.client.gui.Gui.drawRect(
                    (int)(this.selectedColorBoxLeft - this.borderThicknessPX * 0.5f),
                    (int)(this.selectedColorBoxTop - this.borderThicknessPX * 0.5f),
                    (int)(this.selectedColorBoxRight + this.borderThicknessPX * 0.5f),
                    (int)(this.selectedColorBoxBottom + this.borderThicknessPX * 0.5f),
                    0xFFFFFF
            );

            // Draw selected color background
            net.minecraft.client.gui.Gui.drawRect(
                    (int)this.selectedColorBoxLeft,
                    (int)this.selectedColorBoxTop,
                    (int)this.selectedColorBoxRight,
                    (int)this.selectedColorBoxBottom,
                    this.selectedColor
            );


            //GradientUtils.drawGradientRect(this.colorPickerX, this.colorPickerY, this.colorPickerWidth, this.colorPickerHeight, this.selectedColor);
            //GradientUtils.drawSmoothRainbowRect(this.colorSliderX, this.colorSliderY, this.colorSliderWidth, this.colorSliderHeight);
        }
    }

    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        if (belongsToGroupNum != UIGroup.activeWindow) {
            isHovered = false;
            return false;
        }

        float buildYBottom = UIGroup.settingY;
        for (int i = 0; i < widgetNum; i++) {
            buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(i);
        }
        buildYBottom += widgetNum * ((float)Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * SCALE / sh);

        float centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;

        // Check if hovering over color palette area
        float paletteStartX = UIGroup.settingX;
        float paletteStartY = centerY + 0.01f;
        float paletteEndX = paletteStartX + 6 * (COLOR_BOX_SIZE + 0.002f);
        float paletteEndY = paletteStartY + 2 * (COLOR_BOX_SIZE + 0.002f);

        int paletteStartXPx = (int) ((float)sw * paletteStartX);
        int paletteStartYPx = (int) ((float)sh * paletteStartY);
        int paletteEndXPx = (int) ((float)sw * paletteEndX);
        int paletteEndYPx = (int) ((float)sh * paletteEndY);

        isHovered = (mx >= paletteStartXPx && mx <= paletteEndXPx &&
                my >= paletteStartYPx && my <= paletteEndYPx);
        return isHovered;
    }

    @Override
    public void onMousePressed(int mx, int my, int button) {
        if (button == 0 && isHovered) {
            // Calculate which color was clicked
            float buildYBottom = UIGroup.settingY;
            for (int i = 0; i < widgetNum; i++) {
                buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(i);
            }
            buildYBottom += widgetNum * ((float)Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * SCALE / Minecraft.getMinecraft().currentScreen.height);

            float centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;
            float paletteStartX = UIGroup.settingX;
            float paletteStartY = centerY + 0.01f;
            int colorsPerRow = 6;
            float boxSpacing = 0.002f;

            for (int i = 0; i < COLOR_PALETTE.length; i++) {
                int row = i / colorsPerRow;
                int col = i % colorsPerRow;

                float colorX = paletteStartX + col * (COLOR_BOX_SIZE + boxSpacing);
                float colorY = paletteStartY + row * (COLOR_BOX_SIZE + boxSpacing);

                int colorStartX = (int) ((float)Minecraft.getMinecraft().currentScreen.width * colorX);
                int colorStartY = (int) ((float)Minecraft.getMinecraft().currentScreen.height * colorY);
                int colorEndX = (int) ((float)Minecraft.getMinecraft().currentScreen.width * (colorX + COLOR_BOX_SIZE));
                int colorEndY = (int) ((float)Minecraft.getMinecraft().currentScreen.height * (colorY + COLOR_BOX_SIZE));

                if (mx >= colorStartX && mx <= colorEndX && my >= colorStartY && my <= colorEndY) {
                    selectedColor = COLOR_PALETTE[i];
                    break;
                }
            }
        }
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int color) {
        this.selectedColor = color;
    }

    // Helper method to get RGB components
    public int getRed() {
        return (selectedColor >> 16) & 0xFF;
    }

    public int getGreen() {
        return (selectedColor >> 8) & 0xFF;
    }

    public int getBlue() {
        return selectedColor & 0xFF;
    }
}
