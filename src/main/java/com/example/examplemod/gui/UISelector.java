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
    }

    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            float buildYBottom = UIGroup.widgetYPosition.get(this.belongsToGroupNum - 1).get(widgetNum - 1) + UIGroup.settingY;

            float centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;

            // Draw label on the left
            fr.drawStringWithShadow(
                    label,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            float textHeightPercent = (float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh;
            this.ARROW_HEIGHT = textHeightPercent * 1.0f;

            // Calculate selector area (right side)
            float selectorCenterX = UIGroup.settingX2 - 0.125f - 0.05f;
            float leftArrowX = selectorCenterX - 0.125f;
            float rightArrowX = selectorCenterX + 0.125f;

            // Draw left arrow
            int hoveredColor = 0xFFFFFFFF;
            int availableColor = 0xFFE8926F;
            int noneColor = 0xFF666666;
            int leftArrowColor = isLeftArrowHovered ? hoveredColor : (selectedIndex > 0 ? availableColor : noneColor);
            //drawArrow((int)((float)sw * leftArrowX), (int)((float)sh * centerY), (int)((float)sh * ARROW_SIZE), true, leftArrowColor);
            TriangleRenderer.drawFilledTriangleGL(
                    centerX, centerY,
                    centerX - ARROW_HEIGHT, centerY + ARROW_HEIGHT * 0.5f,
                    centerX - ARROW_HEIGHT, centerY - ARROW_HEIGHT * 0.5f,
                    0x80FFFFFF
            );

            // Draw right arrow
            int rightArrowColor = isRightArrowHovered ? hoveredColor : (selectedIndex < options.length - 1 ? availableColor : noneColor);
            //drawArrow((int)((float)sw * rightArrowX), (int)((float)sh * centerY), (int)((float)sh * ARROW_SIZE), false, rightArrowColor);
            TriangleRenderer.drawFilledTriangleGL(
                    centerX, centerY,
                    centerX - ARROW_HEIGHT, centerY + ARROW_HEIGHT * 0.5f,
                    centerX - ARROW_HEIGHT, centerY - ARROW_HEIGHT * 0.5f,
                    0x80FFFFFF
            );

            // Draw current option
            String currentOption = options[selectedIndex];
            int optionWidth = fr.getStringWidth(currentOption);
            fr.drawStringWithShadow(
                    currentOption,
                    (int) ((float)sw * selectorCenterX) - optionWidth / 2,
                    (int) ((float)sh * (centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );
        }
    }

    private void drawArrow(float centerX, float centerY, int color) {
        // Draw simple arrow using rectangles
        //if (pointingLeft) {
        //    // Left pointing arrow: <
        //    for (int i = 0; i < size/2; i++) {
        //        net.minecraft.client.gui.Gui.drawRect(
        //                centerX - size/2 + i, centerY - i,
        //                centerX - size/2 + i + 2, centerY - i + 1,
        //                color
        //        );
        //        net.minecraft.client.gui.Gui.drawRect(
        //                centerX - size/2 + i, centerY + i,
        //                centerX - size/2 + i + 2, centerY + i + 1,
        //                color
        //        );
        //    }
        //} else {
        //    // Right pointing arrow: >
        //    for (int i = 0; i < size/2; i++) {
        //        net.minecraft.client.gui.Gui.drawRect(
        //                centerX + size/2 - i - 1, centerY - i,
        //                centerX + size/2 - i + 1, centerY - i + 1,
        //                color
        //        );
        //        net.minecraft.client.gui.Gui.drawRect(
        //                centerX + size/2 - i - 1, centerY + i,
        //                centerX + size/2 - i + 1, centerY + i + 1,
        //                color
        //        );
        //    }
        //}
    }

    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        if (belongsToGroupNum != UIGroup.activeWindow) {
            isHovered = false;
            isLeftArrowHovered = false;
            isRightArrowHovered = false;
            return false;
        }

        float buildYBottom = UIGroup.widgetYPosition.get(this.belongsToGroupNum - 1).get(widgetNum - 1) + UIGroup.settingY;

        float centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;

        float selectorCenterX = UIGroup.settingX + 0.5f;
        float leftArrowX = selectorCenterX - 0.1f;
        float rightArrowX = selectorCenterX + 0.1f;

        int arrowSize = (int)((float)sh * ARROW_SIZE);

        // Check left arrow hover
        int leftArrowCenterX = (int)((float)sw * leftArrowX);
        int leftArrowCenterY = (int)((float)sh * centerY);
        isLeftArrowHovered = (mx >= leftArrowCenterX - arrowSize && mx <= leftArrowCenterX + arrowSize &&
                my >= leftArrowCenterY - arrowSize && my <= leftArrowCenterY + arrowSize);

        // Check right arrow hover
        int rightArrowCenterX = (int)((float)sw * rightArrowX);
        int rightArrowCenterY = (int)((float)sh * centerY);
        isRightArrowHovered = (mx >= rightArrowCenterX - arrowSize && mx <= rightArrowCenterX + arrowSize &&
                my >= rightArrowCenterY - arrowSize && my <= rightArrowCenterY + arrowSize);

        isHovered = isLeftArrowHovered || isRightArrowHovered;
        return isHovered;
    }

    @Override
    public void onMousePressed(int mx, int my, int button) {
        if (button == 0) {
            if (isLeftArrowHovered && selectedIndex > 0) {
                selectedIndex--;
            } else if (isRightArrowHovered && selectedIndex < options.length - 1) {
                selectedIndex++;
            }
        }
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
