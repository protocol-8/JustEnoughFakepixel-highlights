package com.jef.justenoughfakepixel.features.diana;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the Diana stats
 * Static renderOverlay(boolean preview) is called by GuiPositionEditor
 */
public class DianaOverlay {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final int LINE_HEIGHT = 10;
    private static final int PADDING     = 3;
    private static final int BASE_WIDTH  = 160;

    private static int lastW = BASE_WIDTH;
    private static int lastH = LINE_HEIGHT * 10 + PADDING * 2;

    public static int getOverlayWidth()  { return lastW; }
    public static int getOverlayHeight() { return lastH; }


    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (JefConfig.feature == null || !JefConfig.feature.diana.enabled
                || !JefConfig.feature.diana.showOverlay) return;
        renderOverlay(false);
    }

    public static void renderOverlay(boolean preview) {
        if (JefConfig.feature == null) return;

        List<String> lines = buildLines(preview);
        if (lines.isEmpty()) return;

        float scale = JefConfig.feature.diana.overlayScale;

        int w = BASE_WIDTH;
        for (String line : lines)
            w = Math.max(w, mc.fontRendererObj.getStringWidth(line) + PADDING * 2);
        int h = lines.size() * LINE_HEIGHT + PADDING * 2;
        lastW = w;
        lastH = h;

        ScaledResolution sr  = new ScaledResolution(mc);
        Position         pos = JefConfig.feature.diana.overlayPos;
        int x = pos.getAbsX(sr, (int)(w * scale));
        int y = pos.getAbsY(sr, (int)(h * scale));
        if (pos.isCenterX()) x -= (int)(w * scale / 2);
        if (pos.isCenterY()) y -= (int)(h * scale / 2);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, 1f);

        if (JefConfig.feature.diana.overlayBackground)
            Gui.drawRect(-PADDING, -PADDING, w, h - PADDING, 0x88000000);

        int dy = 0;
        for (String line : lines) {
            mc.fontRendererObj.drawStringWithShadow(line, 0, dy, 0xFFFFFF);
            dy += LINE_HEIGHT;
        }

        GL11.glPopMatrix();
    }


    static List<String> buildLines(boolean preview) {
        List<String> lines = new ArrayList<>();

        if (preview) {
            lines.add("\u00a76\u00a7lDiana Tracker");
            lines.add("\u00a77Burrows: \u00a7b42  \u00a77(\u00a7a120.0\u00a77/hr)");
            lines.add("\u00a77Mobs since Inq: \u00a7b7  \u00a77[\u00a7c9.3%\u00a77 due]");
            lines.add("\u00a77Inqs since Chimera: \u00a7b2");
            lines.add("\u00a77Minotaurs since Stick: \u00a7b15");
            lines.add("\u00a77Champs since Relic: \u00a7b30");
            lines.add("\u00a77Burrow Drops:");
            lines.add("\u00a77  Feathers: \u00a7b3  \u00a77Souvenirs: \u00a7b1");
            lines.add("\u00a77  Crowns: \u00a7b0  \u00a77Coins: \u00a7b1.50M");
            lines.add("\u00a77Mob Drops:");
            lines.add("\u00a77  Shelmets: \u00a7b2  \u00a77Remedies: \u00a7b1  \u00a77Plushies: \u00a7b0");
            return lines;
        }

        DianaStats stats = DianaStats.getInstance();
        if (!stats.isTracking()) return lines;   // empty → nothing drawn

        DianaData d      = stats.getData();
        double bph       = stats.getBph();
        double inqChance = stats.getInqChance();

        lines.add("\u00a76\u00a7lDiana Tracker");
        lines.add(String.format("\u00a77Burrows: \u00a7b%d  \u00a77(\u00a7a%.1f\u00a77/hr)",
                d.totalBurrows, bph));
        lines.add(String.format("\u00a77Mobs since Inq: \u00a7b%d  \u00a77[\u00a7c%.1f%%\u00a77 due]",
                d.mobsSinceInq, inqChance));
        lines.add(String.format("\u00a77Inqs since Chimera: \u00a7b%d",
                d.inqsSinceChimera));
        lines.add(String.format("\u00a77Minotaurs since Stick: \u00a7b%d",
                d.minotaursSinceStick));
        lines.add(String.format("\u00a77Champs since Relic: \u00a7b%d",
                d.champsSinceRelic));
        lines.add("\u00a77Burrow Drops:");
        lines.add(String.format("\u00a77  Feathers: \u00a7b%d  \u00a77Souvenirs: \u00a7b%d",
                d.griffinFeathers, d.souvenirs));
        lines.add(String.format("\u00a77  Crowns: \u00a7b%d  \u00a77Coins: \u00a7b%s",
                d.crownsOfGreed, formatCoins(d.totalCoins)));
        lines.add("\u00a77Mob Drops:");
        lines.add(String.format("\u00a77  Shelmets: \u00a7b%d  \u00a77Remedies: \u00a7b%d  \u00a77Plushies: \u00a7b%d",
                d.dwarfTurtleShelmets, d.antiqueRemedies, d.crochetTigerPlushies));
        return lines;
    }


    private static String formatCoins(long coins) {
        if (coins >= 1_000_000) return String.format("%.2fM", coins / 1_000_000.0);
        if (coins >= 1_000)     return String.format("%.1fk", coins / 1_000.0);
        return String.valueOf(coins);
    }
}