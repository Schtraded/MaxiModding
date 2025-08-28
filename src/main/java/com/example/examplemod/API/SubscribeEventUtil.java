package com.example.examplemod.API;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gui.ColorPickerGUI;
import com.example.examplemod.gui.CustomConfigGui;
import com.example.examplemod.helper.DisplayUtils;
import com.example.examplemod.helper.GuiUtils;
import com.example.examplemod.render.LineDrawingUtils;
import com.example.examplemod.render.TriangleRenderer;
import com.example.examplemod.shader.gradient.GradientUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import scala.collection.parallel.ParIterableLike;

public class SubscribeEventUtil {
    /**
    PRE RENDER GAME OVERLAY
     **/
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlayLowest(RenderGameOverlayEvent.Pre event) {
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderOverlayLow(RenderGameOverlayEvent.Pre event) {
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderOverlayNormal(RenderGameOverlayEvent.Pre event) {
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderOverlayHigh(RenderGameOverlayEvent.Pre event) {
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderOverlayHighest(RenderGameOverlayEvent.Pre event) {
        DisplayUtils.update();
        ExampleMod.blurRenderer.onRenderGameOverlayPre(event);
        GuiUtils.onRenderGameOverlay(event);
        // Convert mouse coordinates
        int mx = (int) (Mouse.getX() * DisplayUtils.scaledWidth / (double) Minecraft.getMinecraft().displayWidth);
        int my = DisplayUtils.scaledHeight - (int) (Mouse.getY() * DisplayUtils.scaledHeight / (double) Minecraft.getMinecraft().displayHeight) - 1;
        ExampleMod.colorPickerGUI.render(Minecraft.getMinecraft().fontRendererObj, DisplayUtils.scaledWidth, DisplayUtils.scaledHeight, mx, my);
    }

    /**
    POST RENDER GAME OVERLAY
    **/
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlayLowest(RenderGameOverlayEvent.Post event) {
        CustomConfigGui.getInstance().onRenderOverlay(event);
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderOverlayLow(RenderGameOverlayEvent.Post event) {
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderOverlayNormal(RenderGameOverlayEvent.Post event) {
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderOverlayHigh(RenderGameOverlayEvent.Post event) {
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderOverlayHighest(RenderGameOverlayEvent.Post event) {
    }
}
