package com.jef.justenoughfakepixel.features.mining;

import com.jef.justenoughfakepixel.config.JefConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Pattern;

public class FetchurHelper {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final String PREFIX = EnumChatFormatting.DARK_GREEN + "[JEF]" + EnumChatFormatting.AQUA + " ";

    // Fetchur chat triggers
    private static final Pattern ALREADY_GIVEN  = Pattern.compile("\\[NPC\\] Fetchur: Come back another time");
    private static final Pattern WRONG_ITEM     = Pattern.compile("\\[NPC\\] Fetchur: why you giving me something completely different");
    private static final Pattern WRONG_AMOUNT   = Pattern.compile("\\[NPC\\] Fetchur: but wrong amount");

    // Chat
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (JefConfig.feature == null || !JefConfig.feature.mining.fetchurHelper) return;

        String clean = net.minecraft.util.StringUtils.stripControlCodes(
                event.message.getFormattedText()).trim();

        if (!clean.startsWith("[NPC] Fetchur:")) return;

        String clue = matchClue(clean);
        if (clue != null) {
            send("Fetchur wants: " + EnumChatFormatting.YELLOW + clue);
            return;
        }

        if (ALREADY_GIVEN.matcher(clean).find()) {
            send("Fetchur: already completed today");
            return;
        }

        if (WRONG_ITEM.matcher(clean).find()) {
            send("Fetchur: " + EnumChatFormatting.RED + "wrong item");
            return;
        }

        if (WRONG_AMOUNT.matcher(clean).find()) {
            send("Fetchur: " + EnumChatFormatting.RED + "correct item, wrong amount");
        }
    }

    // Maps Fetchur's clue to item + amount. Returns null if line is not a clue.
    private static String matchClue(String clean) {
        if (clean.contains("theyre expensive minerals"))         return "Mithril x20";
        if (clean.contains("its tall and can be opened"))        return "Any Door x1  (Oak / Spruce / Birch / Dark Oak / Acacia / Jungle / Iron)";
        if (clean.contains("theyre red and soft"))               return "Red Wool x50";
        if (clean.contains("its shiny and makes sparks"))        return "Flint and Steel x1";
        if (clean.contains("its hot and gives energy"))          return "Coffee x1  (Cheap / Decent / Black)";
        if (clean.contains("theyre yellow and see-through"))     return "Yellow Stained Glass x20";
        if (clean.contains("its useful during celebrations"))    return "Firework Rocket x1";
        if (clean.contains("its circular and sometimes moves"))  return "Compass x1";
        if (clean.contains("its wearable and grows"))            return "Pumpkin x1";
        if (clean.contains("theyre brown and fluffy"))           return "Rabbit's Foot x3";
        if (clean.contains("its explosive, more than usual"))    return "Superboom TNT x1";
        if (clean.contains("theyre green and some dudes trade")) return "Emerald x50";
        return null;
    }

    private static void send(String msg) {
        if (mc.thePlayer != null)
            mc.thePlayer.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.AQUA + msg));
    }
}