package com.example.examplemod.gui;

import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class DummyGui extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
    @Override
    public void handleKeyboardInput() throws IOException {
    }
    @Override
    public boolean doesGuiPauseGame() {return false;}
}
