package com.example.examplemod.helper;

import com.example.examplemod.ExampleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DisplayUtils {
    public static int displayWidth;
    public static int displayHeight;
    public static int scaledWidth;
    public static int scaledHeight;
    public static int currentGuiScale;
    public static boolean displayResized;
    public static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    public static Minecraft mc = Minecraft.getMinecraft();
    private DisplayUtils(int displayWidth, int displayHeight, int scaledWidth, int scaledHeight) {
        this.displayWidth = -1;
        this.displayHeight = -1;
        this.scaledWidth = -1;
        this.scaledHeight = -1;
        this.displayResized = false;
        this.currentGuiScale = Minecraft.getMinecraft().gameSettings.guiScale;
    }
    public static void update() {
        if (displayWidth != mc.displayWidth) {
            displayWidth = mc.displayWidth;
            displayResized = true;
        }
        if (displayHeight != mc.displayHeight) {
            displayHeight = mc.displayHeight;
            displayResized = true;
        }

        if (Minecraft.getMinecraft().gameSettings.guiScale != currentGuiScale || displayResized) {
            sr = new ScaledResolution(Minecraft.getMinecraft());
            currentGuiScale = Minecraft.getMinecraft().gameSettings.guiScale;
        }

        if (scaledWidth != sr.getScaledWidth()) {
            scaledWidth = sr.getScaledWidth();
            displayResized = true;
        }
        if (scaledHeight != sr.getScaledHeight()) {
            scaledHeight = sr.getScaledHeight();
            displayResized = true;
        }

        if (displayResized) {
            //ExampleMod.customConfigGui.updateWidgetYDisplay();
        }

        displayResized = false;
    }
}
