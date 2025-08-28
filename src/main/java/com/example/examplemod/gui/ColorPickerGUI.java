package com.example.examplemod.gui;

import com.example.examplemod.helper.ColorFinderUtil;
import com.example.examplemod.helper.DisplayUtils;
import com.example.examplemod.helper.PixelReaderUtils;
import com.example.examplemod.render.QuadDrawingUtils;
import com.example.examplemod.shader.gradient.GradientUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import com.example.examplemod.shader.sdf.SDFUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import scala.collection.parallel.ParIterableLike;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ColorPickerGUI extends GuiScreen {
    private final float LABEL_SCALE = 1.5f;

    private final String TITLE = "Color Picker";
    private float titleY;
    private float titleX;

    private float tabWidth;
    private float tabHeight;
    private float tabX;
    private float tabY;

    private float colorPickerWidth;
    private float colorPickerHeight;
    private float colorPickerX;
    private float colorPickerY;

    private float colorSliderWidth;
    private float colorSliderHeight;
    private float colorSliderX;
    private float colorSliderY;

    public float savedPalletWidth;
    public float savedPalletHeight;
    public float savedPalletX;
    public float savedPalletY;
    public float savedPalletXSeparator;
    private java.util.List<SavedPalette> savedPalettes = new ArrayList<>();
    private float colorPickerBorder;
    public boolean colorSliderIsHovered;
    public boolean colorPickerIsHovered;
    public boolean colorSliderIsDragging;
    public boolean colorPickerIsDragging;

    private float pickerX;
    private float pickerY;
    private float pickerRadius;
    private float pickerPercentX;
    private float pickerPercentY;

    private float sliderX;
    private float sliderY;
    private float sliderWidth;
    private float sliderHeight;
    private float sliderPercentY;
    private final float SLIDER_BORDER_p = 0.01f;

    private float palletItemHeight;
    private float paletteScrollOffset = 0.0f;
    private float palletColorBorder;
    private int activePalletItem = -1;

    private float halfTextHeight;

    private int selectedColor = 0xFF00FFFF;
    private int sliderColor = this.selectedColor;
    private int pickerColor = this.selectedColor;

    private boolean initSPPosition = false;
    private boolean zero_ButtonPressed = false;
    private boolean newPickerColor = false;

    public ColorPickerGUI() {
        addSavedColor("test-1", 0xFFFFFFFF);
        addSavedColor("test-2", 0xFF00FFFF);
        addSavedColor("test-3", 0xFF0000FF);
    }

    private void initSliderPickerPosition(int sw, int sh) {
        this.sliderY = ColorFinderUtil.getRainbowYPositionForColor(this.sliderColor, this.colorSliderY, this.colorSliderHeight);
        if (this.sliderY < 0) { //-1
            this.sliderY = ColorFinderUtil.getRainbowYPositionForColor(0xFFFF0000, this.colorSliderY, this.colorSliderHeight);
            this.sliderColor = 0xFFFF0000;
        }

        ColorFinderUtil.ColorPosition pos = ColorFinderUtil.getBilinearPositionForColor(
                this.selectedColor, this.colorPickerX, this.colorPickerY, this.colorPickerWidth, this.colorPickerHeight,
                0xFFFFFFFF, this.sliderColor, 0xFF000000, 0xFF000000
        );
        this.pickerX = pos.x;
        this.pickerY = pos.y;

        if (this.colorPickerX + 1.0f >= this.pickerX) {
            this.pickerX = this.colorPickerX + 1.0f;
        }

        this.sliderPercentY = this.sliderY / (float)sh;

        this.pickerPercentX = this.pickerX / (float)sw;
        this.pickerPercentY = this.pickerY / (float)sh;
    }

    public void update(FontRenderer fr, int sw, int sh) {
        final float tabWidth_p = 0.6f;
        final float tabHeight_p = 0.65f;
        final float colorPickerHeight_p = tabHeight_p / 2.0f;
        final float colorSliderWidth_p = 0.04f;
        final float colorPickerBorder_p = 0.01f;

        this.colorPickerBorder = (float)sh * colorPickerBorder_p;

        this.tabX = (float)sw * ((1.0f - tabWidth_p) * 0.5f);
        this.tabY = (float)sh * ((1.0f - tabHeight_p) * 0.5f);
        this.tabWidth = (float)sw * tabWidth_p;
        this.tabHeight = (float)sh * tabHeight_p;

        final float colorPickerOffset = 0.167f;
        final float colorPickerOffsetToTabHeight = this.tabHeight * colorPickerOffset;
        this.colorPickerX = this.tabX + colorPickerOffsetToTabHeight;
        this.colorPickerY = this.tabY + colorPickerOffsetToTabHeight;
        this.colorPickerWidth = this.colorPickerHeight = (float)sh * colorPickerHeight_p;

        this.colorSliderX = this.colorPickerX + this.colorPickerWidth + (float)sh * colorSliderWidth_p;
        this.colorSliderY = this.tabY + colorPickerOffsetToTabHeight;
        this.colorSliderWidth = (float)sh * colorSliderWidth_p;
        this.colorSliderHeight = this.colorPickerHeight;

        final float savedPalletXSeparator_p = 0.6f;
        this.savedPalletX = this.colorSliderX + this.colorSliderWidth * 2.0f;
        this.savedPalletY = this.tabY + colorPickerOffsetToTabHeight * 1.5f;
        this.savedPalletWidth = this.tabX + this.tabWidth - (float)sh * colorSliderWidth_p - this.savedPalletX;
        //todo taby could be removed
        this.savedPalletHeight = this.tabY + this.tabHeight - (float)sh * colorSliderWidth_p - this.savedPalletY;
        this.savedPalletXSeparator = this.savedPalletX + this.savedPalletWidth * savedPalletXSeparator_p ;

        final float textHeightPercent = (float)fr.FONT_HEIGHT * (this.LABEL_SCALE/UIHelper.POSITION_CORRECTION) / sh;
        final float centerYLabel = (float)sh * textHeightPercent * 0.5f;

        final float textToShPercent = (float)fr.FONT_HEIGHT * (1.0f/UIHelper.POSITION_CORRECTION) / sh;
        this.halfTextHeight = (float)sh * textToShPercent * 0.5f;

        final float stringLabelWidth = (float)fr.getStringWidth(this.TITLE);
        final float widthRatioToTextHeight = stringLabelWidth / (float)fr.FONT_HEIGHT;

        this.titleX = (float)sw * 0.5f - centerYLabel * widthRatioToTextHeight;
        this.titleY = (this.colorPickerY + this.tabY) * 0.5f - centerYLabel;

        final float sliderHeight_p = 0.3f;
        this.sliderX = this.colorSliderX;
        this.sliderWidth = this.colorSliderWidth;
        this.sliderHeight = this.colorSliderWidth * sliderHeight_p;

        this.pickerRadius  = (float)sh * textHeightPercent * 0.35f;

        final float palletMinHeight_p = 0.02f;
        final float palletHeight_p = textHeightPercent + palletMinHeight_p;
        final float palletBorder_p = 0.05f;
        this.palletItemHeight = palletHeight_p * sh;
        this.palletColorBorder = palletBorder_p * this.palletItemHeight;

        //this.palletY = this.savedPalletY + this.palletHeight * sh;

        if (!this.initSPPosition) {
            initSliderPickerPosition(sw, sh);
            this.initSPPosition = true;
        }

        this.sliderY = this.sliderPercentY * (float)sh;

        this.pickerX = pickerPercentX * (float)sw;
        this.pickerY = pickerPercentY * (float)sh;
    }
    public void render(FontRenderer fr, int sw, int sh, int mx, int my) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof DummyGui)) {
            Minecraft.getMinecraft().displayGuiScreen(new DummyGui());
        }

        update(fr, sw, sh);
        //60% width /65% height

        SDFUtils.drawRoundedRectWithBorder(this.tabX, this.tabY, this.tabWidth, this.tabHeight, 15.0f,
                0x1A000000, // 20% 33 10% 1A alpha BF
                5.0f,          // Border width
                0xFF009DFF  // Border color (white)
        );

        // Draw title
        GL11.glPushMatrix();
        GL11.glScalef(LABEL_SCALE, LABEL_SCALE, LABEL_SCALE);
        fr.drawStringWithShadow(
                this.TITLE,
                this.titleX / LABEL_SCALE,
                this.titleY / LABEL_SCALE,
                0xFFFFFF
        );
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();

        QuadDrawingUtils.drawRect(
                this.colorPickerX - this.colorPickerBorder,
                this.colorPickerY - this.colorPickerBorder,
                this.colorPickerWidth + this.colorPickerBorder * 2.0f,
                this.colorPickerHeight + this.colorPickerBorder * 2.0f,
                0xFF009DFF
        );
        GradientUtils.drawGradientRect(this.colorPickerX, this.colorPickerY, this.colorPickerWidth, this.colorPickerHeight, this.sliderColor);
        GradientUtils.drawSmoothRainbowRect(this.colorSliderX, this.colorSliderY, this.colorSliderWidth, this.colorSliderHeight);

        if (newPickerColor) {
            this.pickerColor = PixelReaderUtils.getPixelColor((int)this.pickerX, (int)this.pickerY);
        }

        colorPickerIsHovered(mx, my);
        colorSliderIsHovered(mx, my);

        onDrag(mx, my);

        handleMouse(mx, my);

        QuadDrawingUtils.drawRect(
                this.sliderX - (this.colorPickerWidth * this.SLIDER_BORDER_p),
                this.sliderY - (this.colorPickerWidth * this.SLIDER_BORDER_p),
                this.sliderWidth + 2.0f * (this.colorPickerWidth * this.SLIDER_BORDER_p),
                this.sliderHeight + 2.0f * (this.colorPickerWidth * this.SLIDER_BORDER_p),
                0xFFFFFFFF
        );

        QuadDrawingUtils.drawRect(
                this.sliderX, this.sliderY, this.sliderWidth, this.sliderHeight, this.sliderColor
        );

        SDFUtils.drawCircle(this.pickerX, this.pickerY, this.pickerRadius + (this.colorPickerWidth * this.SLIDER_BORDER_p), 0xFFFFFFFF);
        SDFUtils.drawCircle(this.pickerX, this.pickerY, this.pickerRadius, this.pickerColor);

        renderSavedPalettes(fr, mx, my, sw ,sh);
    }

    public void checkSelectedColor(int newSelectedColor, int sw, int sh) {
        if (newSelectedColor != this.selectedColor) {
            this.selectedColor = newSelectedColor;
            this.sliderColor = this.selectedColor;
            this.newPickerColor = true;
            initSPPosition = false;
        }
    }

    public void colorPickerIsHovered(int mx, int my) {
        this.colorPickerIsHovered = isHovered(
                (int)this.colorPickerX,
                (int)(this.colorPickerX + this.colorPickerWidth),
                (int)this.colorPickerY,
                (int)(this.colorPickerY + this.colorPickerHeight),
                mx, my
        );
    }
    public void colorSliderIsHovered(int mx, int my) {
        this.colorSliderIsHovered = isHovered(
                (int)this.colorSliderX,
                (int)(this.colorSliderX + this.colorSliderWidth),
                (int)this.colorPickerY,
                (int)(this.colorSliderY + this.colorSliderHeight),
                mx, my
        );
    }

    public boolean isHovered(int startX, int endX, int startY, int endY, int mx, int my) {
        return (
                mx >= startX && mx <= endX &&
                my >= startY && my <= endY
        );
    }

    private void handleMouse(int mx, int my) {
        while (Mouse.next()) {
            int button = Mouse.getEventButton();
            boolean pressed = Mouse.getEventButtonState();

            if (button == -1) continue;

            if (pressed) {
                onMousePressed(mx, my, button);
            } else {
                onMouseReleased(button);
            }

            int scroll = Mouse.getEventDWheel();

            if (scroll != 0) {

                final boolean isMouseOverPaletteArea = isHovered(
                        (int)this.savedPalletX,
                        (int)(this.savedPalletX + this.savedPalletWidth),
                        (int)this.savedPalletY,
                        (int)(this.savedPalletY + this.savedPalletHeight),
                        mx, my
                );

                if (isMouseOverPaletteArea) {
                    this.paletteScrollOffset -= handleScroll(scroll);
                }
            }
        }
    }

    public float handleScroll(int scroll) {
        final float wheelScrollDelta = 120.0f;
        return (float)scroll / wheelScrollDelta;
    }

    public void onMousePressed(int mx, int my, int button) {
        if (button == 0) {
            this.zero_ButtonPressed = true;
            if (this.colorPickerIsHovered) {
                this.colorPickerIsDragging = true;
                updatePicker(mx, my);
            }

            if (this.colorSliderIsHovered) {
                this.colorSliderIsDragging = true;
                updateSlider(my);
            }
        }
    }

    public void onMouseReleased(int button) {
        if (button == 0) {
            this.zero_ButtonPressed = false;
            this.colorPickerIsDragging = false;
            this.colorSliderIsDragging = false;
        }
    }

    public void onDrag(int mx, int my) {
        if (Mouse.isButtonDown(0)) {
            if (this.colorPickerIsDragging) {
                updatePicker(mx, my);
            }

            if (this.colorSliderIsDragging) {
                updateSlider(my);
            }
        }
    }

    private void updatePicker(int mx, int my) {
        // Clamp picker position to within bounds
        this.pickerX = Math.max(this.colorPickerX + 1.0f,
                Math.min(
                        this.colorPickerX + this.colorPickerWidth, (float)mx));
        this.pickerY = Math.max(this.colorPickerY,
                Math.min(this.colorPickerY + this.colorPickerHeight - 1.0f, (float)my));

        this.pickerPercentX = this.pickerX / (float)DisplayUtils.scaledWidth;
        this.pickerPercentY = this.pickerY / (float)DisplayUtils.scaledHeight;

        this.pickerColor = PixelReaderUtils.getPixelColor((int)this.pickerX, (int)this.pickerY);
        this.selectedColor = this.pickerColor;
    }
    private void updateSlider(int my) {
        // Clamp slider position to within bounds
        this.sliderY = Math.max(this.colorSliderY,
                Math.min(this.colorSliderY + this.colorSliderHeight - this.sliderHeight, (float)my));

        this.sliderPercentY = this.sliderY / (float)DisplayUtils.scaledHeight;

        // Sample from the center of the slider width
        final int sampleX = (int)(this.colorSliderX + this.colorSliderWidth * 0.5f);
        this.sliderColor = PixelReaderUtils.getPixelColor(sampleX, (int)this.sliderY);

        this.pickerColor = PixelReaderUtils.getPixelColor((int)this.pickerX, (int)this.pickerY);
        this.selectedColor = this.pickerColor;
    }

    public static class SavedPalette {
        @Getter
        private final String label;
        @Getter
        private final int color;

        public SavedPalette(String label, int color) {
            this.label = label;
            this.color = color;
        }

        @Override
        public String toString() {
            return String.format("SavedPalette{label='%s', color=0x%08X}", label, color);
        }
    }

    public void addSavedColor(String label, int color) {
        this.savedPalettes.add(new SavedPalette(label, color));
    }

    public void removeSavedColor(int index) {
        if (index >= 0 && index < this.savedPalettes.size()) {
            this.savedPalettes.remove(index);
        }
    }

    private void renderSavedPalettes(FontRenderer fr, int mx, int my, int sw , int sh) {
        if (this.savedPalettes.isEmpty()) return;

        final int paletteItemOffset = (int)(this.paletteScrollOffset / this.palletItemHeight);
        final float scrollOffset = this.paletteScrollOffset - (float)paletteItemOffset * this.palletItemHeight;

        final int visibleCount = (int)(this.savedPalletHeight / this.palletItemHeight);

        for (int i = 0; i < Math.min(visibleCount + 2, this.savedPalettes.size()); i++) {
            int paletteIndex = i + paletteItemOffset;
            SavedPalette palette = this.savedPalettes.get(paletteIndex);

            float itemY = this.savedPalletY + (float)i * this.palletItemHeight - scrollOffset;

            // Check if this palette item is hovered
            final float topLine = Math.max(itemY, this.savedPalletY);
            boolean isHovered = isHovered(
                    (int)this.savedPalletX,
                    (int)(this.savedPalletX + this.savedPalletWidth),
                    (int)topLine,
                    (int)(topLine + this.palletItemHeight),
                    mx, my
            );

            if (isHovered) {
                // Draw hover background
                QuadDrawingUtils.drawRect(
                        this.savedPalletX,
                        topLine,
                        this.savedPalletWidth,
                        this.palletItemHeight,
                        0x20FFFFFF // Semi-transparent white
                );
            }

            if (this.zero_ButtonPressed && isHovered) {
                if (this.activePalletItem != paletteIndex) {
                    checkSelectedColor(palette.color, sw ,sh);
                    this.activePalletItem = paletteIndex;
                }
            }

            if (this.activePalletItem == i) {
                final float activePalletBoarder_p = 0.05f;
                final float activePalletBoarder = this.palletItemHeight * activePalletBoarder_p;
                QuadDrawingUtils.drawRect(
                        this.savedPalletX,
                        topLine - activePalletBoarder,
                        this.savedPalletWidth,
                        activePalletBoarder,
                        0xFFFFFFFF
                );
                QuadDrawingUtils.drawRect(
                        this.savedPalletX,
                        topLine + this.palletItemHeight,
                        this.savedPalletWidth,
                        activePalletBoarder,
                        0xFFFFFFFF
                );
                QuadDrawingUtils.drawRect(
                        this.savedPalletX - activePalletBoarder,
                        topLine - activePalletBoarder,
                        activePalletBoarder,
                        this.palletItemHeight + activePalletBoarder * 2.0f,
                        0xFFFFFFFF
                );
                 QuadDrawingUtils.drawRect(
                        this.savedPalletX + this.savedPalletWidth,
                        topLine - activePalletBoarder,
                        activePalletBoarder,
                        this.palletItemHeight + activePalletBoarder * 2.0f,
                        0xFFFFFFFF
                );
            }

            final float offsetFromPalletItemBounds = this.palletItemHeight * 0.5f - this.halfTextHeight;
            // Draw label
            final float itemYCenter = itemY + this.palletItemHeight * 0.5f;
            fr.drawStringWithShadow(
                    palette.getLabel(),
                    savedPalletX + offsetFromPalletItemBounds,
                    itemYCenter - this.halfTextHeight,
                    0xFFFFFF
            );

            // Draw color display with border
            final float palletColorWidth = this.savedPalletX + this.savedPalletWidth - this.savedPalletXSeparator;
            QuadDrawingUtils.drawRect(
                    this.savedPalletXSeparator - this.palletColorBorder - offsetFromPalletItemBounds,
                    itemYCenter - this.halfTextHeight - this.palletColorBorder,
                    palletColorWidth + this.palletColorBorder,
                    (this.halfTextHeight + this.palletColorBorder) * 2.0f,
                    0xFFFFFFFF
            );



            QuadDrawingUtils.drawRect(
                    this.savedPalletXSeparator - offsetFromPalletItemBounds,
                    itemYCenter - this.halfTextHeight,
                    palletColorWidth - this.palletColorBorder,
                    this.halfTextHeight * 2.0f,
                    palette.getColor() // Border
            );
        }

        //// Draw scroll indicators if needed
        //if (paletteScrollOffset > 0) {
        //    // Up arrow indicator
        //    fr.drawStringWithShadow("↑", savedPalletX + savedPalletWidth - 15, savedPalletY - 15, 0xFFFFFF);
        //}
//
        //if (paletteScrollOffset + maxVisiblePalettes < savedPalettes.size()) {
        //    // Down arrow indicator
        //    float downArrowY = savedPalletY + maxVisiblePalettes * (paletteItemHeight + paletteSpacing);
        //    fr.drawStringWithShadow("↓", savedPalletX + savedPalletWidth - 15, downArrowY + 5, 0xFFFFFF);
        //}
    }

    //private static class SavedPallet extends ColorPickerGUI{
    //    @Getter
    //    public String label;
    //    @Getter
    //    public int color;
    //    private int palletNum = 0;
    //    public static float centerYLabel;
//
    //    private float palletHeight_p;
    //    private float palletHeight;
//
    //    private float palletY_p;
    //    private float palletY;
//
    //    public SavedPallet(String label, int color) {
    //        this.label = label;
    //        this.color = color;
    //        this.palletNum += 1;
    //        final float palletMinHeight_p = 0.02f;
    //        this.palletHeight_p = centerYLabel * 2.0f + palletMinHeight_p;
    //        this.palletY_p = this.palletNum * this.palletHeight_p;
    //    }
//
    //    public void update(int sw, int sh) {
    //        this.palletY = this.savedPalletY + this.palletY_p * sh;
    //        this.palletHeight = palletHeight_p * sh;
    //    }
//
    //    public void render(FontRenderer fr, int sw, int sh) {
    //        update(sw, sh);
//
    //        fr.drawStringWithShadow(
    //                this.label,
    //                this.savedPalletX,
    //                this.palletY + this.palletHeight * 0.5f,
    //                0xFFFFFF
    //        );
//
    //        //this.titleY = (this.colorPickerY + this.tabY) * 0.5f - centerYLabel;
    //        //
    //        //this.savedPalletX
    //        //this.savedPalletY
    //        //this.savedPalletWidth
    //        //this.savedPalletHeight
    //    }
    //}
}
