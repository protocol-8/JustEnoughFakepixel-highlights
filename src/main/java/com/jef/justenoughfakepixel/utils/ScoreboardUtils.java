package com.jef.justenoughfakepixel.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ScoreboardUtils {

    private ScoreboardUtils() {}

    public static List<String> getScoreboardLines() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return Collections.emptyList();

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) return Collections.emptyList();

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return Collections.emptyList();

        List<Score> scores;
        try {
            scores = scoreboard.getSortedScores(objective).stream()
                    .filter(s -> s != null && s.getPlayerName() != null && !s.getPlayerName().startsWith("#"))
                    .collect(Collectors.toList());
        } catch (ConcurrentModificationException e) {
            return Collections.emptyList();
        }

        int size = scores.size();
        return IntStream.range(Math.max(0, size - 15), size)
                .mapToObj(i -> {
                    Score score = scores.get(i);
                    ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                    return ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
                })
                .collect(Collectors.toList());
    }

    /** Returns raw unformatted scoreboard lines (no color codes). */
    public static List<String> getCleanScoreboardLines() {
        return getScoreboardLines().stream()
                .map(s -> net.minecraft.util.StringUtils.stripControlCodes(s).trim())
                .collect(Collectors.toList());
    }

    public static boolean isOnSkyblock() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return false;
        Scoreboard sb = mc.theWorld.getScoreboard();
        if (sb == null) return false;
        ScoreObjective obj = sb.getObjectiveInDisplaySlot(1);
        if (obj == null) return false;
        String title = net.minecraft.util.StringUtils.stripControlCodes(obj.getDisplayName());
        return title.contains("SKYBLOCK");
    }

    public static boolean isInDungeon() {
        return getScoreboardLines().stream()
                .anyMatch(l -> l.contains("The Catacombs") || l.contains("Master Mode"));
    }
}