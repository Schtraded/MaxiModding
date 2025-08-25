// Updated CustomConfigGui.java showing how to use the modern widgets
package com.example.examplemod.gui;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.helper.ChatUtils;
import com.example.examplemod.helper.DisplayUtils;
import com.example.examplemod.helper.HotbarUtils;
import com.example.examplemod.render.LineDrawingUtils;
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
    public final List<UIWidget> widgets = new ArrayList<>();
    private UIPanel mainPanel;
    private UIWidget hovered = null;
    private UIWidget active = null;

    private final Set<Integer> allowedKeys = new HashSet<>();

    public CustomConfigGui(KeyBinding toggleKey) {
        this.toggleKey = toggleKey;
        instance = this;
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
        allowedKeys.add(Keyboard.KEY_ESCAPE);
        allowedKeys.add(Keyboard.KEY_F11);
        allowedKeys.add(Keyboard.KEY_LMETA);
    }

    private void buildUI() {
        initAllowedKey();

        // Create main panel
        mainPanel = new UIPanel(10, 10, 80, 80, "Modern UI Settings");

        // Set group borders
        UIGroup.borderX = 0.15f;
        UIGroup.borderX2 = 0.85f;

        // Create groups
        widgets.add(new UIGroup("Screen"));
        widgets.add(new UIGroup("Extension"));
        widgets.add(new UIGroup("Tooltip"));
        widgets.add(new UIGroup("Colors"));

        // Set settings area
        UIGroup.settingX = 0.2f;
        UIGroup.settingX2 = 0.8f;
        UIGroup.settingY2 = 0.85f;

        // Screen settings with modern sliders
        widgets.add(new UISlider("GUI Scale", "Screen", 1, 4, 3, true, ""));
        widgets.add(new UISlider("Background Opacity", "Screen", 0f, 1f, 0.6f));
        widgets.add(new UISlider("Background Fade-in Duration", "Screen", 0, 2000, 200, true, "ms"));
        widgets.add(new UIToggle("Background Blur Effect", "Screen", true));
        widgets.add(new UIToggle("Adaptive Blur Effect", "Screen", false));
        widgets.add(new UISlider("Blur Radius", "Screen", 0, 20, 7, true, "px"));

        // Window mode selector
        String[] windowModes = {"Windowed", "Fullscreen", "Fullscreen Borderless"};
        widgets.add(new UISelector("Window Mode", "Screen", windowModes, 2));

        widgets.add(new UISlider("Framerate Limit (Inactive)", "Screen", 0, 120, 30, true, " FPS"));
        widgets.add(new UISlider("Framerate Limit (Minimized)", "Screen", 0, 60, 0, true, " FPS"));
        widgets.add(new UISlider("Master Volume (Inactive)", "Screen", 0f, 1f, 0.5f));
        widgets.add(new UISlider("Master Volume (Minimized)", "Screen", 0f, 1f, 0.25f));
        widgets.add(new UIToggle("Inventory Pause", "Screen", false));

        // Extension settings
        widgets.add(new UIToggle("Ding", "Extension", true));
        widgets.add(new UIToggle("Emoji Shortcodes", "Extension", false));
        widgets.add(new UIToggle("Smooth Scrolling", "Extension", true));
        widgets.add(new UIToggle("Modern Text Engine", "Extension", false));

        // Text style selector
        String[] textStyles = {"Classic", "Modern", "Italic", "Bold"};
        widgets.add(new UISelector("Text Style", "Extension", textStyles, 1));

        // Tooltip settings
        widgets.add(new UIToggle("Center Title", "Tooltip", false));
        widgets.add(new UISlider("Tooltip Delay", "Tooltip", 0, 2000, 500, true, "ms"));

        // Animation style selector
        String[] animationStyles = {"None", "Fade", "Slide", "Scale"};
        widgets.add(new UISelector("Animation Style", "Tooltip", animationStyles, 1));

        // Color settings
        widgets.add(new UIColorPicker("Primary Color", "Colors", 0xE8926F));
        widgets.add(new UIColorPicker("Accent Color", "Colors", 0x42A4F5));
        widgets.add(new UIColorPicker("Background Color", "Colors", 0x1E1E1E));
        widgets.add(new UIColorPicker("Text Color", "Colors", 0xFFFFFF));
    }

    public void finalizeWidgetYPercent() {
        float buildYBottom = UIGroup.settingY;
        //UIGroup.widgetYPixel = UIGroup.widgetY;

        for (ArrayList<Float> row : UIGroup.widgetY) {
            UIGroup.widgetYPosition.add(new ArrayList<>(row));
        }

        for (int i = 0; i <  UIGroup.widgetCounter.size(); i++) {
            for (int j = 0; j < UIGroup.widgetCounter.get(i) ; j++) {
                buildYBottom += UIGroup.widgetY.get(i).get(j);
                buildYBottom += (float)mc.fontRendererObj.FONT_HEIGHT * UISlider.SCALE / DisplayUtils.scaledHeight;
                UIGroup.widgetYPosition.get(i).set(j, buildYBottom);
            }
            buildYBottom = UIGroup.settingY;
        }
    }

    //public void updateWidgetYDisplay() {
    //    for (int i = 0; i <  UIGroup.widgetCounter.size(); i++) {
    //        for (int j = 0; j < UIGroup.widgetCounter.get(i) ; j++) {
    //            UIGroup.widgetYPixel.get(i).set(j,  UIGroup.widgetY.get(i).get(j) * DisplayUtils.displayHeight);
    //        }
    //    }
    //}

    //@SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        if (!this.visible) return;

        handleKeyboardInput();
        if (!this.visible) return;

        if (this.visible && !(this.mc.currentScreen instanceof DummyGui)) {
            mc.displayGuiScreen(new DummyGui());
        }
        if (this.mc.inGameHasFocus) this.mc.setIngameNotInFocus();

        int sw = DisplayUtils.scaledWidth;
        int sh = DisplayUtils.scaledHeight;

        // Draw background
        int color = 0x40000000;
        drawRect(0, 0, sw, sh, color);

        // Convert mouse coordinates
        int mx = (int) (Mouse.getX() * sw / (double) mc.displayWidth);
        int my = sh - (int) (Mouse.getY() * sh / (double) mc.displayHeight) - 1;

        // Setup GL state
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        // Draw panel
        mainPanel.render(mc.fontRendererObj, sw, sh);

        // Render widgets
        hovered = null;
        for (UIWidget w : widgets) {
            if (w.isHovered(mx, my, sw, sh)) hovered = w;
            w.render(mc.fontRendererObj, sw, sh);
        }

        // Handle mouse input
        handleMouse(mx, my);

        // Handle drag for sliders
        if (active instanceof UISlider && Mouse.isButtonDown(0)) {
            ((UISlider) active).onDrag(mx, my);
        }

        // Handle keyboard input for text fields
        handleTextFieldInput();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void handleMouse(int mx, int my) {
        while (Mouse.next()) {
            int button = Mouse.getEventButton();
            boolean pressed = Mouse.getEventButtonState();

            if (button == -1) continue;

            if (pressed) {
                if (hovered != null) {
                    active = hovered;
                    active.onMousePressed(mx, my, button);
                }
            } else {
                if (active != null) {
                    active.onMouseReleased(mx, my, button);
                    active = null;
                }
            }
        }
    }

    private void handleTextFieldInput() {
        // Handle keyboard input for text fields if any are focused
        for (UIWidget widget : widgets) {
            if (widget instanceof UITextField) {
                UITextField textField = (UITextField) widget;
                if (textField.isFocused()) {
                    while (Keyboard.next()) {
                        if (Keyboard.getEventKeyState()) {
                            int key = Keyboard.getEventKey();
                            char character = Keyboard.getEventCharacter();
                            textField.handleKeyInput(key, character);
                        }
                    }
                    break; // Only one text field can be focused at a time
                }
            }
        }
    }

    public void handleKeyboardInput() {
        while (Keyboard.next()) {
            int key = Keyboard.getEventKey();
            if (key == Keyboard.KEY_NONE) continue;
            boolean keyPressed = Keyboard.getEventKeyState();

            if (allowedKeys.contains(key) && keyPressed) {
                if (key == Keyboard.KEY_ESCAPE) {
                    this.visible = false;
                    this.mc.displayGuiScreen(null);
                    if (this.mc.currentScreen == null) {
                        this.mc.setIngameFocus();
                    }
                    //ChatUtils.showChatGUI();
                    return;
                } else if (key == Keyboard.KEY_F11) {
                    // Allow F11 to pass through to Minecraft for fullscreen toggle
                    this.mc.toggleFullscreen();
                }
                // Add other allowed key handling here if needed
            }
            // For non-allowed keys, we simply don't process them
            // No need to destroy/recreate the keyboard
        }
    }

    // Helper method to get widget values (for saving settings)
    public float getSliderValue(String label) {
        for (UIWidget widget : widgets) {
            if (widget instanceof UISlider) {
                UISlider slider = (UISlider) widget;
                // You'll need to add a getLabel() method to UISlider or store references
                // This is just an example of how you might retrieve values
            }
        }
        return 0;
    }

    public boolean getToggleValue(String label) {
        for (UIWidget widget : widgets) {
            if (widget instanceof UIToggle) {
                UIToggle toggle = (UIToggle) widget;
                // Similar to above - you'll need a way to identify widgets
            }
        }
        return false;
    }

    public String getSelectorValue(String label) {
        for (UIWidget widget : widgets) {
            if (widget instanceof UISelector) {
                UISelector selector = (UISelector) widget;
                // Similar to above
            }
        }
        return "";
    }

    public int getColorValue(String label) {
        for (UIWidget widget : widgets) {
            if (widget instanceof UIColorPicker) {
                UIColorPicker colorPicker = (UIColorPicker) widget;
                // Similar to above
            }
        }
        return 0xFFFFFF;
    }
}