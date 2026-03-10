package com.jef.justenoughfakepixel.features.diana;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;


public class DianaStats {


    private static DianaStats INSTANCE;

    public static DianaStats getInstance() {
        if (INSTANCE == null) INSTANCE = new DianaStats();
        return INSTANCE;
    }

    private DianaStats() {}


    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private File      file = null;
    private DianaData data = new DianaData();

    // Transient (not persisted) — written by DianaTracker, read by DianaOverlay
    public volatile long   lastSpadeUseMs = 0L;
    public volatile String lastDropType   = null;   // "feather" | "souvenir" | "crown" | "coins"
    public volatile long   lastDropAmount = 0L;

    // File I/O

    /** Call once during pre-init with the JEF config directory. */
    public void initFile(File configDir) {
        this.file = new File(configDir, "diana_stats.json");
    }

    public void load() {
        if (file == null || !file.exists()) return;
        try (Reader r = new FileReader(file)) {
            DianaData loaded = GSON.fromJson(r, DianaData.class);
            if (loaded != null) data = loaded;
        } catch (Exception e) {
            System.err.println("[JEF/Diana] Failed to load diana_stats.json: " + e.getMessage());
        }
    }

    public void save() {
        if (file == null) return;
        try (Writer w = new FileWriter(file)) {
            GSON.toJson(data, w);
        } catch (Exception e) {
            System.err.println("[JEF/Diana] Failed to save diana_stats.json: " + e.getMessage());
        }
    }

    // Reset

    public void reset() {
        data          = new DianaData();
        lastSpadeUseMs = 0L;
        lastDropType   = null;
        lastDropAmount = 0L;
    }

    // Accessors
    public DianaData getData() {
        return data;
    }

    /**
     * Returns {@code true} when the player right-clicked the Ancestral Spade
     * within the last 5 minutes, meaning active tracking is appropriate.
     */
    public boolean isTracking() {
        return System.currentTimeMillis() - lastSpadeUseMs < 5 * 60_000L;
    }

    // Computed Stats

    /**
     * Burrows per hour based on total burrows and session start time.
     * Returns 0 if no session has started yet.
     */
    public double getBph() {
        if (data.sessionStartMs <= 0 || data.totalBurrows == 0) return 0.0;
        long elapsed = System.currentTimeMillis() - data.sessionStartMs;
        if (elapsed < 1_000L) return 0.0;
        return data.totalBurrows / (elapsed / 3_600_000.0);
    }

    /**
     * Cumulative probability (0–100 %) that at least one Inquisitor should
     * have spawned by now, using a ~1% per-mob baseline.
     */
    public double getInqChance() {
        int n = data.mobsSinceInq;
        if (n <= 0) return 0.0;
        return (1.0 - Math.pow(0.99, n)) * 100.0;
    }
}