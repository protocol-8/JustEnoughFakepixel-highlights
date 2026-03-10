package com.jef.justenoughfakepixel.features.diana;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listens for Ancestral Spade use, burrow digs, mob spawns, rare drops, and
 * burrow treasure messages, then updates {@link DianaStats}
 *
 * Static helper methods are called by {@link com.jef.justenoughfakepixel.utils.PartyCommands}
 * to respond to party chat commands (!bph, !inq, !stick, !relic).
 */
public class DianaTracker {

    // Patterns (matched against unformatted text)

    /** "You dug out a Griffin Burrow! (4/4)" */
    private static final Pattern BURROW_DIG =
            Pattern.compile("You dug out a Griffin Burrow! \\((\\d+)/(\\d+)\\)");

    /** "Uh oh! You dug out Minos Inquisitor" etc. */
    private static final Pattern MOB_SPAWN =
            Pattern.compile("Uh oh! You dug out (.+)");

    // Rare mob drops
    private static final Pattern RARE_STICK    = Pattern.compile("RARE DROP! Daedalus Stick");
    private static final Pattern RARE_RELIC    = Pattern.compile("RARE DROP! Minos Relic");
    private static final Pattern RARE_CHIMERA  = Pattern.compile("RARE DROP! Chimera [IVX]+");
    private static final Pattern RARE_SHELMET  = Pattern.compile("RARE DROP! Dwarf Turtle Shelmet");
    private static final Pattern RARE_REMEDIES = Pattern.compile("RARE DROP! Antique Remedies");
    private static final Pattern RARE_PLUSHIE  = Pattern.compile("RARE DROP! Crochet Tiger Plushie");

    // Burrow treasure drops
    private static final Pattern DROP_FEATHER  = Pattern.compile("RARE DROP! You dug out a Griffin Feather");
    private static final Pattern DROP_SOUVENIR = Pattern.compile("RARE DROP! You dug out a Washed-up Souvenir");
    private static final Pattern DROP_CROWN    = Pattern.compile("RARE DROP! You dug out a Crown of Greed");
    private static final Pattern DROP_COINS    = Pattern.compile("RARE DROP! You dug out ([\\d,]+) Coins");
    private static final Pattern GRIFFIN_DOUBLED = Pattern.compile("Your Griffin doubled your rewards!");

    private final Minecraft mc = Minecraft.getMinecraft();

    // Spade Right-Click Detection

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (event.entityPlayer != mc.thePlayer) return;
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK &&
                event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
        if (!isHoldingSpade()) return;
        DianaStats.getInstance().lastSpadeUseMs = System.currentTimeMillis();
    }

    private boolean isHoldingSpade() {
        if (mc.thePlayer == null) return false;
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null || !held.hasDisplayName()) return false;
        return StringUtils.stripControlCodes(held.getDisplayName()).contains("Ancestral Spade");
    }


    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (mc.thePlayer == null) return;
        // Use unformatted text — color codes stripped, raw message content
        String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
        DianaStats stats = DianaStats.getInstance();

        handleBurrowDig(msg, stats);
        handleMobSpawn(msg, stats);
        handleRareMobDrops(msg, stats);
        handleBurrowDrops(msg, stats);
    }

    // Burrow Dig

    private void handleBurrowDig(String msg, DianaStats stats) {
        if (!stats.isTracking()) return;
        if (!BURROW_DIG.matcher(msg).find()) return;

        DianaData d = stats.getData();
        d.totalBurrows++;
        if (d.sessionStartMs < 0) d.sessionStartMs = System.currentTimeMillis();
        stats.save();
    }

    // Mob Spawns

    private void handleMobSpawn(String msg, DianaStats stats) {
        if (!stats.isTracking()) return;
        Matcher m = MOB_SPAWN.matcher(msg);
        if (!m.find()) return;

        String mobName = m.group(1).trim();
        DianaData d = stats.getData();
        d.totalMobs++;

        switch (mobName) {
            case "Minos Inquisitor":
                d.mobsSinceInq = 0;
                d.inqsSinceChimera++;
                d.totalInqs++;
                break;
            case "Minotaur":
                d.mobsSinceInq++;
                d.minotaursSinceStick++;
                d.totalMinotaurs++;
                break;
            case "Minos Champion":
                d.mobsSinceInq++;
                d.champsSinceRelic++;
                d.totalChamps++;
                break;
            default:
                // Minos Hunter, Gaia Construct, Siamese Lynxes, etc.
                d.mobsSinceInq++;
                break;
        }
        stats.save();
    }


    private void handleRareMobDrops(String msg, DianaStats stats) {
        DianaData d = stats.getData();
        boolean changed = false;

        if (RARE_STICK.matcher(msg).find()) {
            d.minotaursSinceStick = 0;
            changed = true;
        }
        if (RARE_RELIC.matcher(msg).find()) {
            d.champsSinceRelic = 0;
            changed = true;
        }
        if (RARE_CHIMERA.matcher(msg).find()) {
            d.inqsSinceChimera = 0;
            changed = true;
        }
        if (RARE_SHELMET.matcher(msg).find()) {
            d.dwarfTurtleShelmets++;
            changed = true;
        }
        if (RARE_REMEDIES.matcher(msg).find()) {
            d.antiqueRemedies++;
            changed = true;
        }
        if (RARE_PLUSHIE.matcher(msg).find()) {
            d.crochetTigerPlushies++;
            changed = true;
        }

        if (changed) stats.save();
    }


    private void handleBurrowDrops(String msg, DianaStats stats) {
        DianaData d = stats.getData();

        // "Your Griffin doubled your rewards!" always immediately follows the drop line
        if (GRIFFIN_DOUBLED.matcher(msg).find()) {
            applyDoubledReward(stats);
            return;
        }

        // Reset last-drop state before detecting a new drop
        stats.lastDropType   = null;
        stats.lastDropAmount = 0L;
        boolean changed = false;

        if (DROP_FEATHER.matcher(msg).find()) {
            d.griffinFeathers++;
            stats.lastDropType   = "feather";
            stats.lastDropAmount = 1L;
            changed = true;

        } else if (DROP_SOUVENIR.matcher(msg).find()) {
            d.souvenirs++;
            stats.lastDropType   = "souvenir";
            stats.lastDropAmount = 1L;
            changed = true;

        } else if (DROP_CROWN.matcher(msg).find()) {
            d.crownsOfGreed++;
            stats.lastDropType   = "crown";
            stats.lastDropAmount = 1L;
            changed = true;

        } else {
            Matcher coins = DROP_COINS.matcher(msg);
            if (coins.find()) {
                long amount = parseLong(coins.group(1));
                d.totalCoins += amount;
                stats.lastDropType   = "coins";
                stats.lastDropAmount = amount;
                changed = true;
            }
        }

        if (changed) stats.save();
    }

    /** Applies a second copy of the last recorded drop when Griffin doubles rewards. */
    private void applyDoubledReward(DianaStats stats) {
        if (stats.lastDropType == null) return;
        DianaData d = stats.getData();

        switch (stats.lastDropType) {
            case "feather":  d.griffinFeathers++;               break;
            case "souvenir": d.souvenirs++;                     break;
            case "crown":    d.crownsOfGreed++;                 break;
            case "coins":    d.totalCoins += stats.lastDropAmount; break;
        }

        stats.lastDropType   = null;
        stats.lastDropAmount = 0L;
        stats.save();
    }

    // ── Party Command Reply Helpers (called from PartyCommands.java

    public static String getBphMessage() {
        DianaStats s = DianaStats.getInstance();
        DianaData  d = s.getData();
        return String.format("[Diana] BPH: %.1f  (%d burrows total)", s.getBph(), d.totalBurrows);
    }

    public static String getInqMessage() {
        DianaStats s = DianaStats.getInstance();
        DianaData  d = s.getData();
        return String.format("[Diana] Mobs since Inq: %d | Inqs since Chimera: %d | Due: %.1f%%",
                d.mobsSinceInq, d.inqsSinceChimera, s.getInqChance());
    }

    public static String getStickMessage() {
        return "[Diana] Minotaurs since Daedalus Stick: "
                + DianaStats.getInstance().getData().minotaursSinceStick;
    }

    public static String getRelicMessage() {
        return "[Diana] Champs since Minos Relic: "
                + DianaStats.getInstance().getData().champsSinceRelic;
    }

    public static String getDropsMessage() {
        DianaData d = DianaStats.getInstance().getData();
        return String.format(
                "Drops — Feathers: %d | Souvenirs: %d | Crowns: %d | Shelmets: %d | Remedies: %d | Plushies: %d | Coins: %s",
                d.griffinFeathers, d.souvenirs, d.crownsOfGreed,
                d.dwarfTurtleShelmets, d.antiqueRemedies, d.crochetTigerPlushies,
                formatCoinsStatic(d.totalCoins));
    }

    private static String formatCoinsStatic(long coins) {
        if (coins >= 1_000_000) return String.format("%.2fM", coins / 1_000_000.0);
        if (coins >= 1_000)     return String.format("%.1fk", coins / 1_000.0);
        return String.valueOf(coins);
    }

    // Utility

    private long parseLong(String s) {
        try {
            return Long.parseLong(s.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}