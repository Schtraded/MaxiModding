package com.example.examplemod.gui;

import net.minecraft.client.gui.FontRenderer;

public class UIToggle extends UIWidget {
    private final String text;
    public boolean isHovered;
    public static int activeWindow = UIGroup.activeWindow;
    public int belongsToGroupNum;
    public int instanceNum;

    public static float SCALE = 1.0f;
    public UIToggle(String label, String groupLabel) {
        this.text = label;

        int indexOfLabel = UIGroup.instances.indexOf(groupLabel);
        if (indexOfLabel == -1) {
            System.out.println("[MAXIMOD][WARNING] UIToggle " + this.text + " doesn't belong to any group");
            return;
        }

        this.belongsToGroupNum = indexOfLabel + 1;

        int oldValue = UIGroup.widgetCounter.get(this.belongsToGroupNum);
        this.instanceNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum, oldValue + 1);

        float spaceBetweenWidgets = 0.02f;
        UIGroup.widgetY.get(this.belongsToGroupNum).add(spaceBetweenWidgets);
    }
    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            float buildYBottom = UIGroup.settingY;
            for (int i = 0; i < instanceNum; i++) {
                buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum).get(i);
            }
            buildYBottom += instanceNum * ((float)fr.FONT_HEIGHT * SCALE / sh);
            float buildYCenter = buildYBottom
                    //- (((float)fr.FONT_HEIGHT * SCALE / sh) / 2.0f)
                    - UIGroup.widgetY.get(this.belongsToGroupNum).get(instanceNum - 1) / 2.0f;

            //int color = isHovered ? 0xFFFFFF : 0x949494;
            //GL11.glPushMatrix();
            //GL11.glScalef(SCALE, SCALE, SCALE);
            fr.drawStringWithShadow(
                    text,
                    (int) (float)sw * UIGroup.settingX,
                    (int) ((float)sh * (buildYCenter - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)) / SCALE,
                    0xFFFFFF
            );
        }
        //GL11.glPopMatrix();

        //fr.drawStringWithShadow(label, x, y - 12, 0xFFFFFF);
        //// background
        //net.minecraft.client.gui.Gui.drawRect(x, y, x + w, y + h, 0xFF333333);
        //// knob
        //int knobX = x + (state ? (w - 10) : 2);
        //net.minecraft.client.gui.Gui.drawRect(knobX, y + 2, knobX + 8, y + h - 2, 0xFFF0D9D9);
    }
    //@Override
    //public void onMousePressed(int mx, int my, int button) {
    //    if (button == 0 && isHovered(mx, my)) {
    //        state = !state;
    //    }
    //}
    //@Override
    //public String getTooltip() {
    //    return label + (state ? " : ON" : " : OFF");
    //}
}
