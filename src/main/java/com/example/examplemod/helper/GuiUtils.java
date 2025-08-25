package com.example.examplemod.helper;

import com.example.examplemod.gui.CustomConfigGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class GuiUtils {
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        CustomConfigGui gui = CustomConfigGui.getInstance();
        if (gui != null && gui.isVisible()) {
            switch (event.type) {
                case HOTBAR:

                case CHAT:
                    // Hide only the hotbar
                    event.setCanceled(true);
                    break;
                // Add other elements you want to hide:
                // case CHAT: // Already handled by reflection method
                //     event.setCanceled(true);
                //     break;

                // Keep these visible:
                // case HEALTH: - Don't cancel
                // case FOOD: - Don't cancel
                // case EXPERIENCE: - Don't cancel
                // case CROSSHAIRS: - Don't cancel
            }
        }
    }
}
