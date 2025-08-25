package com.example.examplemod;

import com.example.examplemod.shader.circle.CircleRenderer;
import com.example.examplemod.shader.roundedRect.RoundedRectRenderer;
import com.example.examplemod.shader.sdf.SDFRenderer;
import com.example.examplemod.shader.sdf.SDFUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class test {
    //public static void drawAnimatedCircle() {
    //    float time = (float)(System.currentTimeMillis() % 5000) / 5000.0f; // 5 second cycle
//
    //    // Animated position
    //    float xPercent = 0.3f + 0.4f * (float)Math.sin(time * 2 * Math.PI);
    //    float yPercent = 0.3f + 0.4f * (float)Math.cos(time * 2 * Math.PI);
//
    //    // Animated size
    //    float radiusPercent = 0.03f + 0.02f * (float)Math.sin(time * 4 * Math.PI);
//
    //    // Animated color
    //    int red = (int)(127 + 127 * Math.sin(time * 3 * Math.PI));
    //    int green = (int)(127 + 127 * Math.sin(time * 3 * Math.PI + 2.0));
    //    int blue = (int)(127 + 127 * Math.sin(time * 3 * Math.PI + 4.0));
//
    //    SDFUtils.drawCirclePercent(xPercent, yPercent, radiusPercent,
    //            ColorUtils.createColor(red, green, blue, 255));
    //}
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        //RoundedRectRenderer.renderRoundedRectPercent(
        //        250, 50, // Position
        //        200, 80, // Size
        //        0.0f,   // Top-left: 0% (sharp corner)
        //        25.0f,  // Top-right: 25% (slightly rounded)
        //        50.0f,  // Bottom-right: 50% (half rounded)
        //        100.0f, // Bottom-left: 100% (fully rounded)
        //        1.0f, 1.0f, 0.5f, 1.0f // Yellow
        //);

        //CircleRenderer.renderCircleWithBorder(
        //        100, 10, // Offset position
        //        40, // Radius
        //        0.0f, 0.0f, 1.0f, 0.8f, // Blue fill with 80% opacity
        //        5, // Border width
        //        0.0f, 0.0f, 0.0f, 1.0f // Black border
        //SDFUtils.drawRoundedRect(100, 100, 200, 100, 10, 15, 5, 8, 0xFFFF5722); // Solid orange
        // Example of animated circle
        //drawAnimatedCircle();
    }
}
