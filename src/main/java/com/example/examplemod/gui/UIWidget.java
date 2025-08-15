package com.example.examplemod.gui;


import net.minecraft.client.gui.FontRenderer;

public abstract class UIWidget {
    /**The panel is not to be initialized in pixels but in percent of the screen, the percent is to be initialized as an int value**/
    public UIWidget() {
    }
    /**sw = scaledWidth; sh = scaledHeight**/
    public abstract void render(FontRenderer fr, int sw ,int sh);
    public boolean isHovered(int mx, int my, int sw, int sh) {
        return false;
    }
    public void onMousePressed(int mx, int my, int button) {}
    public void onMouseReleased(int mx, int my, int button) {}
    //public void onDrag(int mx, int my) {}
    //public String getTooltip() { return null; }
}