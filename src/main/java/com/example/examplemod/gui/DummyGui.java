package com.example.examplemod.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class DummyGui extends GuiScreen {
    @Override
    public void handleKeyboardInput() throws IOException {}
    @Override
    public boolean doesGuiPauseGame() {return false;}
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {}
    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {}
    @Override
    protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {}
    @Override
    protected void drawHoveringText(java.util.List<String> textLines, int x, int y) {}
    @Override
    protected void drawHoveringText(java.util.List<String> textLines, int x, int y, FontRenderer font) {}
    @Override
    protected void handleComponentHover(IChatComponent component, int x, int y) {}
    @Override
    protected void setText(String newChatText, boolean shouldOverwrite) {}
    @Override
    public void sendChatMessage(String msg) {}
    @Override
    public void sendChatMessage(String msg, boolean addToChat) {}
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {}
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {}
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {}
    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {}
    @Override
    public void setGuiSize(int w, int h) {}
    @Override
    public void initGui() {}
    @Override
    public void handleInput() throws IOException {}
    @Override
    public void handleMouseInput() throws IOException {}
    @Override
    public void updateScreen() {}
    @Override
    public void onGuiClosed() {}
    @Override
    public void drawDefaultBackground() {}
    @Override
    public void drawWorldBackground(int tint) {}
    @Override
    public void drawBackground(int tint) {}
    @Override
    public void confirmClicked(boolean result, int id) {}
    @Override
    public void onResize(Minecraft mcIn, int w, int h) {}
}
