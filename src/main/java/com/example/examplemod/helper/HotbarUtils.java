package com.example.examplemod.helper;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static com.example.examplemod.helper.DisplayUtils.mc;

//!TODO: DOESNT WORK

public class HotbarUtils {
    private static GuiIngame originalGuiIngame;
    private static boolean hotbarHidden = false;

    public static void hideHotbar() {
        if (!hotbarHidden) {
            try {
                // Store the original GuiIngame
                originalGuiIngame = mc.ingameGUI;

                // Create a custom GuiIngame that doesn't render the hotbar
                GuiIngame customGuiIngame = new GuiIngame(mc) {
                    @Override
                    protected void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
                        // Don't render the hotbar - leave this method empty
                        // All other HUD elements will still render normally
                    }
                };

                hotbarHidden = true;

            } catch (Exception e) {
                System.out.println("[MAXIMOD][ERROR] Failed to hide hotbar: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void showHotbar() {
        if (hotbarHidden && originalGuiIngame != null) {
            try {
                // Restore the original GUI
                mc.ingameGUI = originalGuiIngame;
                hotbarHidden = false;
            } catch (Exception e) {
                System.out.println("[MAXIMOD][ERROR] Failed to show hotbar: " + e.getMessage());
            }
        }
    }
}