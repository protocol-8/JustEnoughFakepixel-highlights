package com.jef.justenoughfakepixel.features;

import com.jef.justenoughfakepixel.config.JefConfig;
import com.jef.justenoughfakepixel.config.Position;
import com.jef.justenoughfakepixel.events.PacketReceiveTimeUpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

// Ported from https://github.com/odtheking/OdinLegacy/blob/main/src/main/kotlin/me/odinmain/features/impl/render/PerformanceHUD.kt

public class PerformanceHUD {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static final int OVERLAY_WIDTH  = 100;
    public static final int OVERLAY_HEIGHT = 45;
    private static final int LINE_HEIGHT   = 10;
    private static final int PADDING       = 3;

    // Colors
    private static final String C_LABEL = EnumChatFormatting.AQUA.toString();
    private static final String C_VAL   = EnumChatFormatting.WHITE.toString();

    // TPS tracking
    private static long prevTpsTime = 0;
    private static float currentTps = 20f;

    // TPS packet event — fired by mixin on every S03PacketTimeUpdate
    @SubscribeEvent
    public void onTimeUpdate(PacketReceiveTimeUpdateEvent event) {
        long now = System.currentTimeMillis();
        if (prevTpsTime != 0) {
            float tps = 20_000f / Math.max(1, now - prevTpsTime);
            currentTps = Math.max(0f, Math.min(20f, tps));
        }
        prevTpsTime = now;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        prevTpsTime = 0;
        currentTps  = 20f;
    }

    // Overlay
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (JefConfig.feature == null || !JefConfig.feature.misc.performanceHud) return;
        renderOverlay(false);
    }

    public static void renderOverlay(boolean preview) {
        if (JefConfig.feature == null) return;
        List<String> lines = buildLines(preview);
        if (lines.isEmpty()) return;

        boolean vertical = JefConfig.feature.misc.hudVertical;
        int w = estimateWidth(lines, vertical);
        int h = vertical ? lines.size() * LINE_HEIGHT + PADDING * 2 : LINE_HEIGHT + PADDING * 2;

        ScaledResolution sr = new ScaledResolution(mc);
        Position pos = JefConfig.feature.misc.hudPos;
        int x = pos.getAbsX(sr, w);
        int y = pos.getAbsY(sr, h);
        if (pos.isCenterX()) x -= w / 2;
        if (pos.isCenterY()) y -= h / 2;

        if (JefConfig.feature.misc.hudBackground)
            Gui.drawRect(x - PADDING, y - PADDING, x + w, y + h - PADDING, 0x88000000);

        if (vertical) {
            for (String line : lines) {
                mc.fontRendererObj.drawStringWithShadow(line, x, y, 0xFFFFFF);
                y += LINE_HEIGHT;
            }
        } else {
            int cx = x;
            for (String line : lines) {
                mc.fontRendererObj.drawStringWithShadow(line, cx, y, 0xFFFFFF);
                cx += mc.fontRendererObj.getStringWidth(line) + 6;
            }
        }
    }

    private static List<String> buildLines(boolean preview) {
        if (JefConfig.feature == null) return new ArrayList<>();
        List<String> out = new ArrayList<>();

        if (JefConfig.feature.misc.hudShowFps) {
            int fps = preview ? 60 : getFps();
            out.add(C_LABEL + "FPS: " + C_VAL + fps);
        }
        if (JefConfig.feature.misc.hudShowTps) {
            String tps = preview ? "20.0" : String.format("%.1f", currentTps);
            out.add(C_LABEL + "TPS: " + C_VAL + tps);
        }
        if (JefConfig.feature.misc.hudShowPing) {
            int ping = preview ? 42 : getPing();
            out.add(C_LABEL + "Ping: " + C_VAL + ping + "ms");
        }

        return out;
    }

    private static int estimateWidth(List<String> lines, boolean vertical) {
        if (vertical) {
            int max = OVERLAY_WIDTH;
            for (String l : lines) max = Math.max(max, mc.fontRendererObj.getStringWidth(l) + PADDING * 2);
            return max;
        }
        int total = 0;
        for (String l : lines) total += mc.fontRendererObj.getStringWidth(l) + 6;
        return Math.max(total, OVERLAY_WIDTH);
    }

    private static int getFps() {
        return Minecraft.getDebugFPS();
    }

    private static int getPing() {
        if (mc.thePlayer == null || mc.getNetHandler() == null) return 0;
        net.minecraft.client.network.NetworkPlayerInfo info =
                mc.getNetHandler().getPlayerInfo(mc.thePlayer.getGameProfile().getId());
        return info != null ? info.getResponseTime() : 0;
    }

    // Singleton for static renderOverlay
    private static PerformanceHUD instance;
    public PerformanceHUD() { instance = this; }
    public static PerformanceHUD getInstance() { return instance; }
}