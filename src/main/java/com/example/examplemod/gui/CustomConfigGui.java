package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomConfigGui extends GuiScreen {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final KeyBinding toggleKey;
    private boolean visible = false;

    private static CustomConfigGui instance;

    // widgets
    private final List<UIWidget> widgets = new ArrayList<>();
    private UIPanel mainPanel;
    private UIWidget hovered = null;
    private UIWidget active = null;

    private final Set<Integer> allowedKeys = new HashSet<>();

    public CustomConfigGui(KeyBinding toggleKey) {
        this.toggleKey = toggleKey;
        instance = this;  // store singleton
        buildUI();
    }

    public static CustomConfigGui getInstance() {
        return instance;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private void initAllowedKey() {
        allowedKeys.add(Keyboard.KEY_ESCAPE); // always allow ESC
        allowedKeys.add(Keyboard.KEY_F11);    // fullscreen toggle
        allowedKeys.add(Keyboard.KEY_LMETA);    // fullscreen toggle
        //allowedKeys.add(Keyboard.KEY_W);
        //allowedKeys.add(Keyboard.KEY_A);
        //allowedKeys.add(Keyboard.KEY_S);
        //allowedKeys.add(Keyboard.KEY_D);
        //allowedKeys.add(Keyboard.KEY_UP);
        //allowedKeys.add(Keyboard.KEY_DOWN);
        //allowedKeys.add(Keyboard.KEY_LEFT);
        //allowedKeys.add(Keyboard.KEY_RIGHT);
    }




    private void buildUI() {
        initAllowedKey();
        // create a panel
        mainPanel = new UIPanel( 0, 0, 100, 100, "Maxi Mod");

        // create a group
        //set group border
        UIGroup.borderX = 0.1f;
        UIGroup.borderX2 = 0.9f;

        widgets.add(new UIGroup("General"));
        widgets.add(new UIGroup("Color"));
        widgets.add(new UIGroup("Blocked"));
        widgets.add(new UIGroup("Kuudra"));

        //set settings border
        UIGroup.settingX = 0.25f;
        UIGroup.settingX2 = 0.75f;
        UIGroup.settingY2 = 0.9f;

        widgets.add(new UIToggle("General1", "General"));
        widgets.add(new UIToggle("General2", "General"));
        widgets.add(new UIToggle("General3", "General"));


        // sample content: sliders & toggles aligned left (just for demo)
        //int x = 5;
        //int y = mainPanel.y + 60;
        //widgets.add(new UILabel(x, y - 28, "Screen"));
        //widgets.add(new UISlider(x, y, 300, "GUI Scale", 1, 4, 3));
        //y += 36;
        //widgets.add(new UISlider(x, y, 300, "Background Opacity", 0f, 1f, 0.6f));
        //y += 36;
        //widgets.add(new UISlider(x, y, 300, "Blur Radius", 0, 20, 7));
        //y += 46;
        //widgets.add(new UILabel(x, y - 18, "Extension"));
        //widgets.add(new UIToggle(x, y, "Emoji Shortcodes", false));
        //y += 36;
        //widgets.add(new UIToggle(x, y, "Smooth Scrolling", true));
        //y += 46;
        //widgets.add(new UILabel(x, y - 18, "Tooltip"));
        //widgets.add(new UIToggle(x, y, "Center Title", false));

        // add widgets to panel (panel is not strict container here but used for bounds)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        // check our keybinding toggle in here (safe on client side)
        //if (toggleKey.isPressed()) visible = !visible;
        //if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) visible = false

        if (!this.visible) return;
        //if(this.mc.currentScreen != null) {
        //    this.visible = false;
        //    return;
        //}
        handleKeyboardInput();
        if (!this.visible) return;
        if (this.visible && !(this.mc.currentScreen instanceof DummyGui)) mc.displayGuiScreen(new DummyGui());
        if (this.mc.inGameHasFocus) this.mc.setIngameNotInFocus();
        //mc.setIngameNotInFocus();
        //KeyHelper.nullifyKeyPressedExceptF11();
        //if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
        //    visible = false;
        //    Keyboard.destroy();
        //    try {
        //        Keyboard.create();
        //    } catch (LWJGLException e) {
        //        throw new RuntimeException(e);
        //    }
        //    return;
        //}
        //if(!mc.inGameHasFocus);

        // draw blurred dim background effect (simple translucent rect here)
        //ExampleMod.blurRenderer.renderBlurredBackground(event);

        ScaledResolution sr = new ScaledResolution(mc);
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();

        // convert mouse to scaled GUI coords
        int mx = (int) (Mouse.getX() * sr.getScaledWidth() / (double) mc.displayWidth);
        int my = sr.getScaledHeight() - (int) (Mouse.getY() * sr.getScaledHeight() / (double) mc.displayHeight) - 1;

        // setup GL state
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        //mc.getTextureManager().bindTexture(null);


        // draw panel
        mainPanel.render(mc.fontRendererObj, sw ,sh);

        // render widgets
        hovered = null;
        for (UIWidget w : widgets) {
            if (w.isHovered(mx, my, sw ,sh)) hovered = w;
            w.render(mc.fontRendererObj, sw, sh);
        }
//
        // handle mouse presses/releases for widgets (simple immediate mode)
        handleMouse(mx, my);
//
        // draw tooltip if hovered
        //if (hovered != null && hovered.getTooltip() != null) {
        //    drawTooltip(mc.fontRendererObj, hovered.getTooltip(), mx, my);
        //}

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void handleMouse(int mx, int my) {
        // Using LWJGL Mouse events - MUST poll events in render loop for reliable reading.
        // We'll use state checks (pressed this frame)
        while (Mouse.next()) {
            int button = Mouse.getEventButton();
            boolean pressed = Mouse.getEventButtonState();
//
            if (button == -1) continue;
//
            if (pressed) {
                // press
                if (hovered != null) {
                    active = hovered;
                    active.onMousePressed(mx, my, button);
                }
            } else {
                // release
                if (active != null) {
                    active.onMouseReleased(mx, my, button);
                    active = null;
                }
            }
        }
//
        // if mouse dragging and active is slider, forward drag
        //if (active instanceof UISlider && Mouse.isButtonDown(0)) {
        //    ((UISlider) active).onDrag(mx, my);
        //}
    }

    //TODO: ONLY CHECK ESC
    public void handleKeyboardInput() {
        while (Keyboard.next()) {
            int key = Keyboard.getEventKey();
            if (key == Keyboard.KEY_NONE) continue;
            boolean keyPressed = Keyboard.getEventKeyState();

            if (!allowedKeys.contains(key)) {
                Keyboard.destroy();
                try {
                    Keyboard.create();
                } catch (LWJGLException e) {
                    throw new RuntimeException(e);
                }
            } else if (keyPressed) {
                //ESC KEY
                if (key == Keyboard.KEY_ESCAPE /*|| key == Keyboard.KEY_LMETA*/) {
                    this.visible = false;
                    this.mc.displayGuiScreen(null);
                    if (this.mc.currentScreen == null)
                    {
                        this.mc.setIngameFocus();
                    }
                    return;
                }
            }
        }
        //if (Keyboard.getEventKey() == Keyboard.KEY_T) {
        //    System.out.println(Keyboard.getEventKey());
        //}
//
//
        //System.out.println(Keyboard.getEventKey());
//
        //if (Keyboard.getEventKeyState()) {
        //    int pressedKey = Keyboard.getEventKey();
        //    //ESC KEY
        //    if (pressedKey == Keyboard.KEY_ESCAPE) {
        //        this.visible = false;
        //        this.mc.displayGuiScreen(null);
        //        if (this.mc.currentScreen == null)
        //        {
        //            this.mc.setIngameFocus();
        //        }
        //    } else {
        //        Keyboard.destroy();
        //        try {
        //            Keyboard.create();
        //        } catch (LWJGLException e) {
        //            throw new RuntimeException(e);
        //        }
        //    }
        //}
    }
//
    //// tiny helper drawRect (wrapper of Gui.drawRect equivalent)
    //private void drawRect(int left, int top, int right, int bottom, int color) {
    //    // color format: ARGB
    //    net.minecraft.client.gui.Gui.drawRect(left, top, right, bottom, color);
    //}
//
    //private void drawTooltip(FontRenderer fr, String text, int x, int y) {
    //    if (text == null) return;
    //    int w = fr.getStringWidth(text) + 12;
    //    int h = 12 + 4;
    //    int px = x + 12;
    //    int py = y - 12;
    //    drawRect(px - 3, py - 3, px + w, py + h, 0xEE333333);
    //    fr.drawStringWithShadow(text, px + 6, py + 2, 0xFFFFFF);
    //}
}
