package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class UITextField extends UIWidget {
    private final String label;
    private String text;
    private String placeholder;
    public boolean isHovered;
    public boolean isFocused;
    private int cursorPosition;
    private long lastCursorBlink;
    private boolean showCursor;
    private static final long CURSOR_BLINK_TIME = 500; // milliseconds

    public static int activeWindow = UIGroup.activeWindow;
    public int belongsToGroupNum;
    public int instanceNum;

    public static float SCALE = 1.0f;
    private static final float FIELD_HEIGHT = 0.025f; // 2.5% of screen height
    private static final float PADDING = 0.005f;      // Internal padding

    public UITextField(String label, String groupLabel, String placeholder) {
        this.label = label;
        this.text = "";
        this.placeholder = placeholder != null ? placeholder : "Type here...";
        this.cursorPosition = 0;
        this.isFocused = false;
        this.lastCursorBlink = System.currentTimeMillis();
        this.showCursor = true;

        int indexOfLabel = UIGroup.instances.indexOf(groupLabel);
        if (indexOfLabel == -1) {
            System.out.println("[MAXIMOD][WARNING] UITextField " + this.label + " doesn't belong to any group");
            return;
        }

        this.belongsToGroupNum = indexOfLabel + 1;

        int oldValue = UIGroup.widgetCounter.get(this.belongsToGroupNum - 1);
        this.instanceNum = oldValue + 1;
        UIGroup.widgetCounter.set(this.belongsToGroupNum - 1, oldValue + 1);

        float spaceBetweenWidgets = 0.05f; // More space for text field
        UIGroup.widgetY.get(this.belongsToGroupNum - 1).add(spaceBetweenWidgets);
    }

    public UITextField(String label, String groupLabel) {
        this(label, groupLabel, null);
    }

    @Override
    public void render(FontRenderer fr, int sw, int sh) {
        if (belongsToGroupNum == UIGroup.activeWindow) {
            float buildYBottom = UIGroup.settingY;
            for (int i = 0; i < instanceNum; i++) {
                buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(i);
            }
            buildYBottom += instanceNum * ((float)fr.FONT_HEIGHT * SCALE / sh);

            float labelY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(instanceNum - 1) / 2.0f;
            float fieldY = labelY + ((float)fr.FONT_HEIGHT * SCALE / sh);

            // Draw label
            fr.drawStringWithShadow(
                    label,
                    (int) ((float)sw * UIGroup.settingX),
                    (int) ((float)sh * (labelY - ((float)fr.FONT_HEIGHT * (SCALE/UIHelper.POSITION_CORRECTION) / sh) / 2.0f)),
                    0xFFFFFF
            );

            // Calculate field dimensions
            int fieldStartX = (int) ((float)sw * UIGroup.settingX);
            int fieldEndX = (int) ((float)sw * UIGroup.settingX2);
            int fieldStartY = (int) ((float)sh * fieldY);
            int fieldHeight = (int) ((float)sh * FIELD_HEIGHT);

            // Draw field background
            int backgroundColor = isFocused ? 0xFF2A2A2A : (isHovered ? 0xFF333333 : 0xFF222222);
            net.minecraft.client.gui.Gui.drawRect(
                    fieldStartX, fieldStartY,
                    fieldEndX, fieldStartY + fieldHeight,
                    backgroundColor
            );

            // Draw border
            int borderColor = isFocused ? 0xFF42A4F5 : 0xFF666666;
            // Top border
            net.minecraft.client.gui.Gui.drawRect(
                    fieldStartX, fieldStartY,
                    fieldEndX, fieldStartY + 1,
                    borderColor
            );
            // Bottom border
            net.minecraft.client.gui.Gui.drawRect(
                    fieldStartX, fieldStartY + fieldHeight - 1,
                    fieldEndX, fieldStartY + fieldHeight,
                    borderColor
            );
            // Left border
            net.minecraft.client.gui.Gui.drawRect(
                    fieldStartX, fieldStartY,
                    fieldStartX + 1, fieldStartY + fieldHeight,
                    borderColor
            );
            // Right border
            net.minecraft.client.gui.Gui.drawRect(
                    fieldEndX - 1, fieldStartY,
                    fieldEndX, fieldStartY + fieldHeight,
                    borderColor
            );

            // Calculate text rendering area
            int textX = fieldStartX + (int)((float)sw * PADDING);
            int textY = fieldStartY + (fieldHeight - fr.FONT_HEIGHT) / 2;
            int textWidth = fieldEndX - fieldStartX - 2 * (int)((float)sw * PADDING);

            // Enable scissor for text clipping
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(textX, sh - (fieldStartY + fieldHeight), textWidth, fieldHeight);

            // Draw text or placeholder
            String displayText = text.isEmpty() ? placeholder : text;
            int textColor = text.isEmpty() ? 0xFF888888 : 0xFFFFFFFF;

            fr.drawString(displayText, textX, textY, textColor);

            // Draw cursor if focused
            if (isFocused && showCursor && !text.isEmpty()) {
                String textBeforeCursor = text.substring(0, Math.min(cursorPosition, text.length()));
                int cursorX = textX + fr.getStringWidth(textBeforeCursor);

                // Make sure cursor is visible within the field
                if (cursorX >= textX && cursorX <= fieldEndX - 2) {
                    net.minecraft.client.gui.Gui.drawRect(
                            cursorX, textY,
                            cursorX + 1, textY + fr.FONT_HEIGHT,
                            0xFFFFFFFF
                    );
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            // Update cursor blink
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCursorBlink > CURSOR_BLINK_TIME) {
                showCursor = !showCursor;
                lastCursorBlink = currentTime;
            }
        }
    }

    @Override
    public boolean isHovered(int mx, int my, int sw, int sh) {
        if (belongsToGroupNum != UIGroup.activeWindow) {
            isHovered = false;
            return false;
        }

        float buildYBottom = UIGroup.settingY;
        for (int i = 0; i < instanceNum; i++) {
            buildYBottom += UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(i);
        }
        buildYBottom += instanceNum * ((float)Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * SCALE / sh);

        float labelY = buildYBottom - UIGroup.widgetY.get(this.belongsToGroupNum - 1).get(instanceNum - 1) / 2.0f;
        float fieldY = labelY + ((float) Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * SCALE / sh);

        int fieldStartX = (int) ((float)sw * UIGroup.settingX);
        int fieldEndX = (int) ((float)sw * UIGroup.settingX2);
        int fieldStartY = (int) ((float)sh * fieldY);
        int fieldHeight = (int) ((float)sh * FIELD_HEIGHT);

        isHovered = (mx >= fieldStartX && mx <= fieldEndX &&
                my >= fieldStartY && my <= fieldStartY + fieldHeight);
        return isHovered;
    }

    @Override
    public void onMousePressed(int mx, int my, int button) {
        if (button == 0) {
            if (isHovered) {
                isFocused = true;
                showCursor = true;
                lastCursorBlink = System.currentTimeMillis();

                // Calculate cursor position from mouse click
                // This is a simplified version - you might want to make it more precise
                cursorPosition = text.length();
            } else {
                isFocused = false;
            }
        }
    }

    public void handleKeyInput(int key, char character) {
        if (!isFocused) return;

        if (key == Keyboard.KEY_BACK) {
            // Backspace
            if (cursorPosition > 0 && !text.isEmpty()) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
        } else if (key == Keyboard.KEY_DELETE) {
            // Delete
            if (cursorPosition < text.length()) {
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
            }
        } else if (key == Keyboard.KEY_LEFT) {
            // Move cursor left
            if (cursorPosition > 0) {
                cursorPosition--;
            }
        } else if (key == Keyboard.KEY_RIGHT) {
            // Move cursor right
            if (cursorPosition < text.length()) {
                cursorPosition++;
            }
        } else if (key == Keyboard.KEY_HOME) {
            // Move cursor to beginning
            cursorPosition = 0;
        } else if (key == Keyboard.KEY_END) {
            // Move cursor to end
            cursorPosition = text.length();
        } else if (character >= 32 && character < 127) {
            // Printable character
            text = text.substring(0, cursorPosition) + character + text.substring(cursorPosition);
            cursorPosition++;
        }

        // Reset cursor blink when typing
        showCursor = true;
        lastCursorBlink = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = Math.min(this.cursorPosition, this.text.length());
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        this.isFocused = focused;
        if (focused) {
            showCursor = true;
            lastCursorBlink = System.currentTimeMillis();
        }
    }
}