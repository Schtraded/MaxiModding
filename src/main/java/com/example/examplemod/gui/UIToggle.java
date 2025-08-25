// Modern UIToggle.java
package com.example.examplemod.gui;

import com.example.examplemod.helper.ColorUtils;
import com.example.examplemod.helper.DisplayUtils;
import com.example.examplemod.shader.sdf.SDFUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;
import scala.collection.parallel.ParIterableLike;

public class UIToggle extends UIWidget {
    private final String text;
    public boolean isHovered;
    private boolean buttonState;
    private int color;
    //public static int activeWindow = UIGroup.activeWindow;
    public int belongsToGroupNum;
    public int widgetNum;

    public static float SCALE = 1.0f;
    private static float TOGGLE_WIDTH = 0.075f;//1.5f*((float)Minecraft.getMinecraft().fontRendererObj.getStringWidth("XXXX") * (SCALE/UIHelper.POSITION_CORRECTION) / DisplayUtils.scaledWidth);;
    private static float TOGGLE_HEIGHT = 0.05f;//1.5f*((float)Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / DisplayUtils.scaledHeight);

    private static float toggleWidthScale = 2.5f;

    private float toggleButtonX;
    private float toggleButtonY;
    private float borderThickness;
    private float centerY;
    private float toggleXCirclePosition;
    //private float TOGGLE_CIRCLE_HEIGHT;
    private int bgStartX;
    private int bgStartY;
    private int bgEndX;
    private int bgEndY;
    public UIToggle(String label, String groupLabel) {
        this(label, groupLabel, false, "#FFFFFF");
    }

    public UIToggle(String label, String groupLabel, boolean initialState) {
        this(label, groupLabel, initialState, "#FFFFFF");
    }

    public UIToggle(String label, String groupLabel, boolean initialState, String color) {
        this.color = ColorUtils.createColorFromHex(color);
        this.text = label;
        this.buttonState = initialState;

        int indexOfLabel = UIGroup.instances.indexOf(groupLabel);
        if (indexOfLabel == -1) {
            System.out.println("[MAXIMOD][WARNING] UIToggle " + this.text + " doesn't belong to any group");
            return;
        }

        this.belongsToGroupNum = indexOfLabel + 1;

        int oldValue = UIGroup.widgetCounter.get(this.belongsToGroupNum - 1);
        this.widgetNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum - 1, oldValue + 1);

        float spaceBetweenWidgets = 0.035f;
        UIGroup.widgetY.get(this.belongsToGroupNum - 1).add(spaceBetweenWidgets);
    }

    public void update(FontRenderer fr, int sw, int sh) {
        //TODO: UPDATE WHEN SCREEN IS RESIZED OTHERWISE NOT
        float buildYBottom = UIGroup.widgetYPosition.get(this.belongsToGroupNum - 1).get(widgetNum - 1) + UIGroup.settingY;

        this.centerY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(widgetNum - 1) / 2.0f;

        float textHeightPercent = (float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh;
        this.TOGGLE_HEIGHT = textHeightPercent * 1.0f;

        this.TOGGLE_WIDTH = this.TOGGLE_HEIGHT * this.toggleWidthScale;

        this.toggleButtonX = UIGroup.settingX2 - this.TOGGLE_WIDTH / 2.0f - 0.02f;
        this.toggleButtonY = this.centerY - this.TOGGLE_HEIGHT / 2.0f;

        this.borderThickness = this.TOGGLE_HEIGHT * 0.15f * 2.0f;

        //float textHeightPercentToWidth = (float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sw;
        //this.TOGGLE_CIRCLE_HEIGHT = textHeightPercentToWidth * 2f;

        this.toggleXCirclePosition = this.buttonState
                ? 0.0f
                : this.TOGGLE_HEIGHT * (this.toggleWidthScale - 1.0f);

        this.bgStartX = (int)((float)sw * this.toggleButtonX);
        this.bgStartY = (int)((float)sh * this.toggleButtonY);
        this.bgEndX = bgStartX + (int)((float)sh * this.TOGGLE_WIDTH);
        this.bgEndY = bgStartY + (int)((float)sh * this.TOGGLE_HEIGHT);
    }

    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            update(fr, sw, sh);

            // Draw label
            fr.drawStringWithShadow(
                    this.text,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (this.centerY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            // Draw toggle button border
            SDFUtils.drawRoundedRect(
                    (float)sw * this.toggleButtonX - sh * this.borderThickness * 0.5f,
                    (float)sh * (this.toggleButtonY - this.borderThickness * 0.5f),
                    (float) sh * (this.TOGGLE_WIDTH + this.borderThickness),
                    (float) sh * (this.TOGGLE_HEIGHT + this.borderThickness),
                    (float)sh * (this.TOGGLE_HEIGHT + this.borderThickness),
                    0xFFFFFFFF
            );

            // Draw toggle button background
            int buttonColor = buttonState ? 0xFF42A4F5 : 0xFF404040; //0xFF404040 = grey
            SDFUtils.drawRoundedRect(
                    (float)sw * this.toggleButtonX,
                    (float)sh * this.toggleButtonY,
                    (float) sh * this.TOGGLE_WIDTH,
                    (float) sh * this.TOGGLE_HEIGHT,
                    (float)sh * this.TOGGLE_HEIGHT,
                    buttonColor
            );

            // Draw toggle button circle
            SDFUtils.drawRoundedRect(
                    (float)sw * this.toggleButtonX + sh * this.toggleXCirclePosition,
                    (float)sh * this.toggleButtonY,
                    (float) sh * this.TOGGLE_HEIGHT,
                    (float) sh * this.TOGGLE_HEIGHT,
                    (float) sh * this.TOGGLE_HEIGHT,
                    0xFFFFFFFF
            );
        }
    }

    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        if (this.belongsToGroupNum != UIGroup.activeWindow) {
            this.isHovered = false;
            return false;
        }

        this.isHovered = (mx >= bgStartX && mx <= bgEndX && my >= bgStartY && my <= bgEndY);
        return this.isHovered;
    }

    @Override
    public void onMousePressed(int mx, int my, int button) {
        if (button == 0 && this.isHovered) {
            this.buttonState = !this.buttonState;
        }
    }

    public boolean getState() {
        return this.buttonState;
    }

    public void setState(boolean state) {
        this.buttonState = state;
    }
}

//net.minecraft.client.gui.Gui.drawRect(
//        (int) (sw * this.toggleButtonX),
//        (int) 0,
//        (int) ((sw * this.toggleButtonX) + 1f),
//        (int) sh,
//        0xFFFF0000
//);
//net.minecraft.client.gui.Gui.drawRect(
//        (int) (sw * (this.toggleButtonX + this.TOGGLE_WIDTH)),
//        (int) 0,
//        (int) ((sw * (this.toggleButtonX + this.TOGGLE_WIDTH)) + 1f),
//        (int) sh,
//        0xFFFF0000
//);
//net.minecraft.client.gui.Gui.drawRect(
//        (int) 0,
//        (int) (sh * this.toggleButtonY),
//        (int) sw,
//        (int) ((sh * this.toggleButtonY) + 1f),
//        0xFFFF0000
//);
//net.minecraft.client.gui.Gui.drawRect(
//        (int) 0,
//        (int) (sh * (this.toggleButtonY + this.TOGGLE_HEIGHT)),
//        (int) sw,
//        (int) ((sh * (this.toggleButtonY + this.TOGGLE_HEIGHT)) + 1f),
//        0xFFFF0000
//);