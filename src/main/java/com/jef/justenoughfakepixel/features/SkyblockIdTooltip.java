package com.jef.justenoughfakepixel.features;

import com.jef.justenoughfakepixel.config.JefConfig;
import com.jef.justenoughfakepixel.utils.RomanNumeralParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

public class SkyblockIdTooltip {

    private int tickCounter = 0;

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        if (e.toolTip == null || e.itemStack == null) return;
        if (JefConfig.feature == null) return;

        boolean doRoman    = JefConfig.feature.general.romanNumerals;
        boolean doSkyblock = JefConfig.feature.general.showSkyblockId;

        if (doRoman) {
            for (int i = 1; i < e.toolTip.size(); i++) {
                String replaced = RomanNumeralParser.replaceInString(e.toolTip.get(i));
                if (!replaced.equals(e.toolTip.get(i))) e.toolTip.set(i, replaced);
            }
        }

        if (doSkyblock) {
            try {
                NBTTagCompound extra = e.itemStack.getSubCompound("ExtraAttributes", false);
                if (extra != null && extra.hasKey("id", 8)) {
                    String id = extra.getString("id");
                    if (id != null && !id.isEmpty()) {
                        String line = EnumChatFormatting.DARK_GRAY + "skyblock:" + id;
                        if (!e.toolTip.contains(line)) e.toolTip.add(line);
                    }
                }
            } catch (Throwable ignored) {}
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        if (JefConfig.feature == null || !JefConfig.feature.general.romanNumerals) return;
        if (++tickCounter % 20 != 0) return;

        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer == null || mc.thePlayer.sendQueue == null) return;
            Collection<NetworkPlayerInfo> infos = mc.thePlayer.sendQueue.getPlayerInfoMap();
            if (infos == null) return;
            for (NetworkPlayerInfo info : infos) {
                try {
                    if (info.getDisplayName() != null) {
                        String name     = info.getDisplayName().getFormattedText();
                        String replaced = RomanNumeralParser.replaceInString(name);
                        if (!replaced.equals(name)) info.setDisplayName(new ChatComponentText(replaced));
                    } else if (info.getGameProfile() != null) {
                        String name     = info.getGameProfile().getName();
                        String replaced = RomanNumeralParser.replaceInString(name);
                        if (!replaced.equals(name)) info.setDisplayName(new ChatComponentText(replaced));
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }
}