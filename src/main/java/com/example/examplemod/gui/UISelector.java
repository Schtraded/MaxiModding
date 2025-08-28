package com.example.examplemod.gui;

import com.example.examplemod.render.TriangleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class UISelector extends UIWidget {
    private final String label;
    private final String[] options;
    private int selectedIndex;
    public boolean isHovered;
    public boolean isLeftArrowHovered;
    public boolean isRightArrowHovered;

    public static int activeWindow = UIGroup.activeWindow;
    public int belongsToGroupNum;
    public int widgetNum;

    public static float SCALE = 1.0f;
    private static final float ARROW_SIZE = 0.015f;
    private float ARROW_HEIGHT;
    private float centerY;
    private float selectorCenterX;
    private float leftArrowX;
    private float rightArrowX;
    private String currentOption;
    private int optionWidth;

    private static final int hoveredColor = 0xFFFFFFFF;
    private static final int availableColor = 0xFF42A4F5;
    private static final int noneColor = 0xFF666666;

    public UISelector(String label, String groupLabel, String[] options, int defaultIndex) {
        this.label = label;
        this.options = options != null ? options : new String[]{"Option 1"};
        this.selectedIndex = Math.max(0, Math.min(defaultIndex, this.options.length - 1));

        int indexOfLabel = UIGroup.instances.indexOf(groupLabel);
        if (indexOfLabel == -1) {
            System.out.println("[MAXIMOD][WARNING] UISelector " + this.label + " doesn't belong to any group");
            return;
        }

        this.belongsToGroupNum = indexOfLabel + 1;

        int oldValue = UIGroup.widgetCounter.get(this.belongsToGroupNum - 1);
        this.widgetNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum - 1, oldValue + 1);

        float spaceBetweenWidgets = 0.035f;
        UIGroup.widgetY.get(this.belongsToGroupNum - 1).add(spaceBetweenWidgets);

        updateOption();
    }

    public void update(FontRenderer fr, int sw, int sh) {
        //TODO: UPDATE WHEN SCREEN IS RESIZED OTHERWISE NOT
        float buildYBottom = UIGroup.widgetYPosition.get(this.belongsToGroupNum - 1).get(widgetNum - 1) + UIGroup.settingY;
        this.centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;

        float textHeightPercent = (float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh;
        this.ARROW_HEIGHT = textHeightPercent * 1.0f;

        // Calculate selector area (right side)
        this.selectorCenterX = UIGroup.settingX2 - 0.125f - 0.05f;
        this.leftArrowX = selectorCenterX - 0.125f;
        this.rightArrowX = selectorCenterX + 0.125f;
    }

    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            update(fr, sw, sh);

            // Draw label on the left
            fr.drawStringWithShadow(
                    label,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            // Draw left arrow
            int leftArrowColor = isLeftArrowHovered ? (selectedIndex > 0 ? hoveredColor : noneColor) : (selectedIndex > 0 ? availableColor : noneColor);
            TriangleRenderer.drawFilledTriangleGL(
                    (float)sw * leftArrowX, (float)sh * centerY,
                    (float)sw * leftArrowX + ARROW_HEIGHT * sh, (float)sh * centerY + ARROW_HEIGHT * sh * 0.5f,
                    (float)sw * leftArrowX + ARROW_HEIGHT * sh, (float)sh * centerY - ARROW_HEIGHT * sh * 0.5f,
                    leftArrowColor
            );

            // Draw right arrow
            int rightArrowColor = isRightArrowHovered ? (selectedIndex < options.length - 1 ? hoveredColor : noneColor) : (selectedIndex < options.length - 1 ? availableColor : noneColor);
            TriangleRenderer.drawFilledTriangleGL(
                    (float)sw * rightArrowX, (float)sh * centerY,
                    (float)sw * rightArrowX - ARROW_HEIGHT * sh, (float)sh * centerY + ARROW_HEIGHT * sh * 0.5f,
                    (float)sw * rightArrowX - ARROW_HEIGHT * sh, (float)sh * centerY - ARROW_HEIGHT * sh * 0.5f,
                    rightArrowColor
            );

            // Draw current option
            fr.drawStringWithShadow(
                    currentOption,
                    (int) ((float)sw * selectorCenterX) - optionWidth / 2,
                    (int) ((float)sh * (centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );
        }
    }

    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        if (belongsToGroupNum != UIGroup.activeWindow) {
            isHovered = false;
            isLeftArrowHovered = false;
            isRightArrowHovered = false;
            return false;
        }
        final int arrowCenterYpos = (int)((float)sh * centerY);
        final int arrowSize = (int)(ARROW_HEIGHT * sh);

        final int leftArrowXpos = (int)((float) sw * leftArrowX);
        isLeftArrowHovered = (
                mx >= leftArrowXpos &&
                mx <= leftArrowXpos + arrowSize &&
                my >= arrowCenterYpos - (int)((float)arrowSize * 0.5f) &&
                my <= arrowCenterYpos + (int)((float)arrowSize * 0.5f)
        );

        final int rightArrowXpos = (int)((float) sw * rightArrowX);
        isRightArrowHovered = (
                mx >= rightArrowXpos - arrowSize &&
                mx <= rightArrowXpos &&
                my >= arrowCenterYpos - (int)((float)arrowSize * 0.5f) &&
                my <= arrowCenterYpos + (int)((float)arrowSize * 0.5f)
        );

        isHovered = isLeftArrowHovered || isRightArrowHovered;
        return isHovered;
    }

    @Override
    public void onMousePressed(int mx, int my, int button) {
        if (button == 0) {
            if (isLeftArrowHovered && selectedIndex > 0) {
                selectedIndex--;
                updateOption();
            } else if (isRightArrowHovered && selectedIndex < options.length - 1) {
                selectedIndex++;
                updateOption();
            }
        }
    }
    private void updateOption() {
        this.currentOption = options[selectedIndex];
        this.optionWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(currentOption);
    }

    public String getSelectedOption() {
        return options[selectedIndex];
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.length) {
            selectedIndex = index;
        }
    }
}
