package com.example.examplemod.shader.newTry;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

//import static com.example.examplemod.ExampleMod.blur;

public class subscribeEvent {
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        GL11.glPushMatrix();
        //blur.applyBlur();
        GL11.glPopMatrix();
    }
}
