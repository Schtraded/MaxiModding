package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class UIColorPicker extends UIWidget {
    private final String label;
    private int selectedColor;
    public boolean isHovered;

    public static int activeWindow = UIGroup.activeWindow;
    public int belongsToGroupNum;
    public int instanceNum;

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
        this.instanceNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum - 1, oldValue + 1);

        float spaceBetweenWidgets = 0.06f; // More space for color picker
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

            float centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(instanceNum - 1) / 2.0f;

            // Draw label
            fr.drawStringWithShadow(
                    label,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (centerY - 0.02f - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            // Draw current color preview
            float previewX = UIGroup.settingX2 - COLOR_BOX_SIZE - 0.01f;
            float previewY = centerY - 0.02f - COLOR_BOX_SIZE / 2.0f;

            int previewStartX = (int) ((float)sw * previewX);
            int previewStartY = (int) ((float)sh * previewY);
            int previewEndX = (int) ((float)sw * (previewX + COLOR_BOX_SIZE));
            int previewEndY = (int) ((float)sh * (previewY + COLOR_BOX_SIZE));

            // Draw preview with border
            net.minecraft.client.gui.Gui.drawRect(previewStartX - 1, previewStartY - 1, previewEndX + 1, previewEndY + 1, 0xFFFFFFFF);
            net.minecraft.client.gui.Gui.drawRect(previewStartX, previewStartY, previewEndX, previewEndY, 0xFF000000 | selectedColor);

            // Draw color palette
            float paletteStartX = UIGroup.settingX;
            float paletteStartY = centerY + 0.01f;
            int colorsPerRow = 6;
            float boxSpacing = 0.002f;

            for (int i = 0; i < COLOR_PALETTE.length; i++) {
                int row = i / colorsPerRow;
                int col = i % colorsPerRow;

                float colorX = paletteStartX + col * (COLOR_BOX_SIZE + boxSpacing);
                float colorY = paletteStartY + row * (COLOR_BOX_SIZE + boxSpacing);

                int colorStartX = (int) ((float)sw * colorX);
                int colorStartY = (int) ((float)sh * colorY);
                int colorEndX = (int) ((float)sw * (colorX + COLOR_BOX_SIZE));
                int colorEndY = (int) ((float)sh * (colorY + COLOR_BOX_SIZE));

                // Draw color box with border
                boolean isSelected = COLOR_PALETTE[i] == selectedColor;
                int borderColor = isSelected ? 0xFFFFFFFF : 0xFF666666;

                net.minecraft.client.gui.Gui.drawRect(colorStartX - 1, colorStartY - 1, colorEndX + 1, colorEndY + 1, borderColor);
                net.minecraft.client.gui.Gui.drawRect(colorStartX, colorStartY, colorEndX, colorEndY, 0xFF000000 | COLOR_PALETTE[i]);

                // Draw selection indicator
                if (isSelected) {
                    net.minecraft.client.gui.Gui.drawRect(colorStartX - 2, colorStartY - 2, colorEndX + 2, colorEndY + 2, 0xFFE8926F);
                    net.minecraft.client.gui.Gui.drawRect(colorStartX - 1, colorStartY - 1, colorEndX + 1, colorEndY + 1, 0xFFFFFFFF);
                }
            }
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

        float centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(instanceNum - 1) / 2.0f;

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
            for (int i = 0; i < instanceNum; i++) {
                buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(i);
            }
            buildYBottom += instanceNum * ((float)Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * SCALE / Minecraft.getMinecraft().currentScreen.height);

            float centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(instanceNum - 1) / 2.0f;
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
