package com.jef.justenoughfakepixel.utils;

import com.jef.justenoughfakepixel.features.diana.DianaTracker;
import com.jef.justenoughfakepixel.features.dungeons.DungeonStats;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartyCommands {

    private static final Pattern PARTY_MSG =
            Pattern.compile("^Party > (?:\\[[^]]*])?\\s*(\\w{1,16}):\\s*(.+)$");

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getFormattedText());

        Matcher matcher = PARTY_MSG.matcher(message);
        if (!matcher.matches()) return;

        String content = matcher.group(2);
        if (content == null) return;

        String[] parts = content.trim().split("\\s+");
        String   cmd   = parts[0].toLowerCase();

        switch (cmd) {

            case "!pb": {
                String arg1 = parts.length >= 2 ? parts[1] : null;
                String arg2 = parts.length >= 3 ? parts[2] : null;
                if (arg1 == null) {
                    respond("Usage: !pb <floor> | !pb <floor> br | !pb p1-p5");
                    return;
                }
                respond(DungeonStats.getFormattedPb(arg1, arg2));
                break;
            }


            case "!bph":
                respond(DianaTracker.getBphMessage());
                break;

            case "!inq":
                respond(DianaTracker.getInqMessage());
                break;

            case "!stick":
                respond(DianaTracker.getStickMessage());
                break;

            case "!relic":
                respond(DianaTracker.getRelicMessage());
                break;

            default:
                break;
        }
    }

    private void respond(String msg) {
        if (mc.thePlayer == null) return;
        scheduler.schedule(() -> {
            if (mc.thePlayer != null)
                mc.thePlayer.sendChatMessage("/pc " + msg);
        }, 1500, TimeUnit.MILLISECONDS);
    }
}