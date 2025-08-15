package com.example.examplemod.mixins;

import com.example.examplemod.features.CustomColorUtil;
import com.example.examplemod.utils.BlockedUserManager;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {

    @Shadow private int[] colorCode;
    @Shadow private boolean randomStyle;
    @Shadow private boolean boldStyle;
    @Shadow private boolean strikethroughStyle;
    @Shadow private boolean underlineStyle;
    @Shadow private boolean italicStyle;
    @Shadow private boolean unicodeFlag;
    @Shadow
    private Random fontRandom;
    @Shadow private float red, green, blue, alpha, posX, posY;
    @Shadow private int textColor;

    @Shadow protected abstract float renderChar(char ch, boolean italic);
    @Shadow protected abstract int getCharWidth(char character);
    @Shadow protected abstract void doDraw(float advance);
    @Shadow protected abstract void setColor(float r, float g, float b, float a);

    private static final Pattern HEX_PATTERN = Pattern.compile("<#{1,2}([A-Fa-f0-9]{6})>");

    private static int savedFormatting;
    private static int savedTextColor;
    private static float savedAlpha;

    private static boolean hasColorCode;

    private void saveFormatting() {
        if (this.randomStyle)         this.savedFormatting |= (1 << 0);
        if (this.boldStyle)           this.savedFormatting |= (1 << 1);
        if (this.strikethroughStyle)  this.savedFormatting |= (1 << 2);
        if (this.underlineStyle)      this.savedFormatting |= (1 << 3);
        if (this.italicStyle)         this.savedFormatting |= (1 << 4);
        this.savedAlpha = this.alpha;
        this.savedTextColor = this.textColor;
    }

    private void restoreFormatting(boolean shadow) {
        this.randomStyle        = (this.savedFormatting & (1 << 0)) != 0;
        this.boldStyle          = (this.savedFormatting & (1 << 1)) != 0;
        this.strikethroughStyle = (this.savedFormatting & (1 << 2)) != 0;
        this.underlineStyle     = (this.savedFormatting & (1 << 3)) != 0;
        this.italicStyle        = (this.savedFormatting & (1 << 4)) != 0;
        this.alpha = this.savedAlpha;
        this.textColor = this.savedTextColor;

        if (!hasColorCode) {
            setColor(this.red, this.blue, this.green, this.alpha);
            return;
        }

        float r = (this.textColor >> 16 & 255) / 255f;
        float g = (this.textColor >> 8 & 255) / 255f;
        float b = (this.textColor & 255) / 255f;

        if (shadow) {
            float factor = 0.25f;
            setColor(r * factor, g * factor, b * factor, this.alpha);
        } else {
            setColor(r, g, b, this.alpha);
        }
    }

    @Inject(method = "renderStringAtPos", at = @At("HEAD"), cancellable = true)
    //TODO:LENGTH OF HOVER IS WRONG BECAUSE IT TAKES THE LENGTH OF THE TEXT BEFORE
    private void onRenderStringAtPos(String text, boolean shadow, CallbackInfo ci) {
        this.hasColorCode = false;
        Collection<String> keywords = BlockedUserManager.getFlaggedNames();
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                int starIndex = text.indexOf(keyword);
                if (
                        //text.length() >= (CustomColorUtil.IGNOREHEX.length() + keyword.length())
                        /*&&*/ starIndex - CustomColorUtil.IGNOREHEX.length() >= 0
                        && text.substring(starIndex - CustomColorUtil.IGNOREHEX.length(), starIndex).equals(CustomColorUtil.IGNOREHEX)
                ) {
                    continue;
                }
                text = text.replaceAll(Pattern.quote(keyword), CustomColorUtil.HEXCODE_BLOCKEDUSER + keyword + CustomColorUtil.RESETFORMATTING);
            }
        }

        if (text.contains(CustomColorUtil.IGNOREHEX)) {
            text = text.replaceAll(Pattern.quote(CustomColorUtil.IGNOREHEX), "");
        }

        Matcher matcher = HEX_PATTERN.matcher(text);
        Matcher resetMatcher = Pattern.compile(Pattern.quote(CustomColorUtil.RESETFORMATTING)).matcher(text);
        int textLength = text.length();
        int i = 0;

        while (i < textLength) {
            char c0 = text.charAt(i);

            // § code
            if (c0 == 167 && i + 1 < textLength) {
                this.hasColorCode = true;
                char next = Character.toLowerCase(text.charAt(i + 1));
                int i1 = "0123456789abcdefklmnor".indexOf(next);

                // Handle color/style reset
                if (i1 >= 0 && i1 <= 15) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    int j1 = this.colorCode[i1 + (shadow ? 16 : 0)];
                    this.textColor = j1;
                    setColor((j1 >> 16 & 255) / 255f, (j1 >> 8 & 255) / 255f, (j1 & 255) / 255f, this.alpha);
                } else if (i1 == 16) this.randomStyle = true;
                else if (i1 == 17) this.boldStyle = true;
                else if (i1 == 18) this.strikethroughStyle = true;
                else if (i1 == 19) this.underlineStyle = true;
                else if (i1 == 20) this.italicStyle = true;
                else if (i1 == 21) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    setColor(this.red, this.green, this.blue, this.alpha);
                }

                i += 2;
                continue;
            }

            // Hex color tag
            if (text.charAt(i) == '<') {
                resetMatcher.region(i, text.length());
                if (resetMatcher.lookingAt()) {
                    restoreFormatting(shadow);

                    i += resetMatcher.end() - resetMatcher.start(); // skip over tag
                    continue;
                }

                matcher.region(i, text.length());
                if (matcher.lookingAt()) {
                    saveFormatting();

                    String hex = matcher.group(1);
                    int color = Integer.parseInt(hex, 16);
                    this.textColor = color;
                    float r = (color >> 16 & 255) / 255f;
                    float g = (color >> 8 & 255) / 255f;
                    float b = (color & 255) / 255f;

                    if (shadow) {
                        float factor = 0.25f;
                        setColor(r * factor, g * factor, b * factor, this.alpha);
                    } else {
                        setColor(r, g, b, this.alpha);
                    }

                    i += matcher.end() - matcher.start(); // skip over tag
                    continue;
                }
            }

            // Render character
            char toRender = text.charAt(i);
            int j = toRender;

            if (this.randomStyle && j != -1) {
                int k = this.getCharWidth(toRender);
                char c1;
                do {
                    j = this.fontRandom.nextInt(256);
                    c1 = (char) j;
                } while (k != this.getCharWidth(c1));
                toRender = c1;
            }

            float f1 = 1f;
            boolean flag = (toRender == 0 || j == -1 || this.unicodeFlag) && shadow;

            if (flag) {
                this.posX -= f1;
                this.posY -= f1;
            }

            float f = this.renderChar(toRender, this.italicStyle);

            if (flag) {
                this.posX += f1;
                this.posY += f1;
            }

            if (this.boldStyle) {
                this.posX += f1;
                if (flag) {
                    this.posX -= f1;
                    this.posY -= f1;
                }

                this.renderChar(toRender, this.italicStyle);
                this.posX -= f1;

                if (flag) {
                    this.posX += f1;
                    this.posY += f1;
                }

                ++f;
            }

            this.doDraw(f);
            i++;
        }

        ci.cancel(); // We fully override the method
    }
}