package com.example.examplemod.helper;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;

import java.lang.reflect.Field;

import static com.example.examplemod.helper.DisplayUtils.mc;

public class ChatUtils {
    private static GuiNewChat originalChatGui;
    public static boolean chatHidden = false;

    public static void hideChatGUI() {
        if (!chatHidden) {
            try {
                // Get the GuiIngame instance
                GuiIngame ingameGUI = mc.ingameGUI;

                // Use reflection to access the persistantChatGUI field
                Field chatField = GuiIngame.class.getDeclaredField("persistantChatGUI");
                chatField.setAccessible(true);

                // Store the original chat GUI
                originalChatGui = (GuiNewChat) chatField.get(ingameGUI);

                // Create a dummy chat GUI that renders nothing
                GuiNewChat dummyChat = new GuiNewChat(mc) {
                    @Override
                    public void drawChat(int updateCounter) {
                        // Render nothing - effectively hiding chat
                    }

                    @Override
                    public void printChatMessage(net.minecraft.util.IChatComponent chatComponent) {
                        // Still process messages but don't display them
                        super.printChatMessage(chatComponent);
                    }
                };

                // Replace with dummy chat
                chatField.set(ingameGUI, dummyChat);
                chatHidden = true;

            } catch (Exception e) {
                System.out.println("[MAXIMOD][ERROR] Failed to hide chat: " + e.getMessage());
            }
        }
    }

    public static void showChatGUI() {
        if (chatHidden && originalChatGui != null) {
            try {
                // Restore the original chat GUI
                GuiIngame ingameGUI = mc.ingameGUI;
                Field chatField = GuiIngame.class.getDeclaredField("persistantChatGUI");
                chatField.setAccessible(true);
                chatField.set(ingameGUI, originalChatGui);
                chatHidden = false;
            } catch (Exception e) {
                System.out.println("[MAXIMOD][ERROR] Failed to show chat: " + e.getMessage());
            }
        }
    }
}
