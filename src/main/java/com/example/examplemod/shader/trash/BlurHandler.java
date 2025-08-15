package com.example.examplemod.shader.trash;

import com.example.examplemod.shader.trash.GaussianBlur;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;

public class BlurHandler {

    private GaussianBlur gaussianBlur;
    private Minecraft mc = Minecraft.getMinecraft();

    public BlurHandler() {
        int width = mc.displayWidth;
        int height = mc.displayHeight;
        gaussianBlur = new GaussianBlur(width, height);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        // Only apply to the main game view (skip chat, hotbar, etc.)
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        float blurRadius = 4.0f; // Change this for stronger/weaker blur
        gaussianBlur.applyBlur(blurRadius);
    }
}