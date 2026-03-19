package com.jef.justenoughfakepixel.features.general;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.mixins.FontRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EnchantChromaRenderer {

    private static boolean chromaActive;
    private static boolean renderingShadow;
    private static float baseX;
    private static int[][] chromaRanges;

    private EnchantChromaRenderer() {}

    public static void beginRenderString(String text, boolean shadow) {
        renderingShadow = shadow;
        chromaRanges = null;
        chromaActive = false;

        if (text == null || JefConfig.feature == null || !JefConfig.feature.general.enchantChroma) return;
        if (!text.contains("\u00a7z") && !text.contains("\u00a7Z")) return;

        chromaActive = true;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        baseX = ((FontRendererAccessor) fr).jef$getPosX();

        List<int[]> ranges = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            int zIdx = -1;
            for (int j = i; j < text.length() - 1; j++) {
                if (text.charAt(j) == '\u00a7' && Character.toLowerCase(text.charAt(j + 1)) == 'z') {
                    zIdx = j;
                    break;
                }
            }
            if (zIdx == -1) break;

            int endIdx = text.length();
            for (int j = zIdx + 2; j < text.length() - 1; j++) {
                if (text.charAt(j) == '\u00a7') {
                    char code = Character.toLowerCase(text.charAt(j + 1));
                    if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f') || code == 'r') {
                        endIdx = j;
                        break;
                    }
                }
            }

            ranges.add(new int[]{widthStrippingZ(fr, text, zIdx), widthStrippingZ(fr, text, endIdx)});
            i = endIdx;
        }

        chromaRanges = ranges.isEmpty() ? null : ranges.toArray(new int[0][]);
    }

    public static void changeTextColor() {
        if (!chromaActive || chromaRanges == null || JefConfig.feature == null) return;

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int relX = Math.round(((FontRendererAccessor) fr).jef$getPosX() - baseX);

        boolean inRange = false;
        for (int[] range : chromaRanges) {
            if (relX >= range[0] && relX < range[1]) { inRange = true; break; }
        }
        if (!inRange) return;

        FontRendererAccessor accessor = (FontRendererAccessor) fr;
        int base = ChromaColour.specialToChromaRGB(JefConfig.feature.general.enchantPerfectColor);
        int rgb = applyMode(base, accessor.jef$getPosX(), accessor.jef$getPosY());
        if (renderingShadow) {
            int a = (rgb >>> 24) & 255;
            int r = ((rgb >>> 16) & 255) / 4;
            int g = ((rgb >>> 8) & 255) / 4;
            int b = (rgb & 255) / 4;
            rgb = (a << 24) | (r << 16) | (g << 8) | b;
        }
        GlStateManager.color(((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F, ((rgb >> 24) & 255) / 255F);
    }

    public static void endRenderString() {
        chromaActive = false;
        renderingShadow = false;
        chromaRanges = null;
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private static int widthStrippingZ(FontRenderer fr, String text, int toIndex) {
        int width = 0;
        boolean bold = false;
        int i = 0;
        while (i < toIndex && i < text.length()) {
            char c = text.charAt(i);
            if (c == '\u00a7' && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (code == 'z') {
                    // skip — not a real format code
                } else if (code == 'l') {
                    bold = true;
                } else if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f') || code == 'r') {
                    bold = false;
                }
                i += 2;
            } else {
                int cw = fr.getCharWidth(c);
                if (cw > 0) {
                    width += cw;
                    if (bold) width++;
                }
                i++;
            }
        }
        return width;
    }

    private static int applyMode(int argb, float x, float y) {
        if (JefConfig.feature.general.enchantChromaMode == 0) return argb;
        float size = Math.max(1F, JefConfig.feature.general.enchantChromaSize);
        float shift = ((x + y) / size) % 1F;
        int a = (argb >>> 24) & 255;
        int r = (argb >>> 16) & 255;
        int g = (argb >>> 8) & 255;
        int b = argb & 255;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[0] = (hsb[0] + shift) % 1F;
        return (a << 24) | (Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x00FFFFFF);
    }
}