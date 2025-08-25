package com.example.examplemod.API;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gui.CustomConfigGui;
import com.example.examplemod.helper.DisplayUtils;
import com.example.examplemod.helper.GuiUtils;
import com.example.examplemod.render.LineDrawingUtils;
import com.example.examplemod.render.TriangleRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        LineDrawingUtils.drawLine(1.0f, 1.0f, 100.0f, 100.0f, 1.0f, 0x80FFFFFF);
        //net.minecraft.client.gui.Gui.drawRect(0, 0, 100, 50, 0x001E1E1E);
        TriangleRenderer.drawFilledTriangleGL(0.0f, 0.0f, 100.0f, 50.0f, 0.0f, 50.0f, 0x80FFFFFF);
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
