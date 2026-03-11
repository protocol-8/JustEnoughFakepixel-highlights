package com.jef.justenoughfakepixel.core;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.categories.*;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class Config {

    @Expose
    @Category(name = "General", desc = "General features")
    public final GeneralConfig general = new GeneralConfig();

    @Expose
    @Category(name = "Misc", desc = "Misc features")
    public final MiscConfig misc = new MiscConfig();

    @Expose
    @Category(name = "Waypoints", desc = "Waypoints config & GUI")
    public final WaypointsConfig waypoints = new WaypointsConfig();

    @Expose
    @Category(name = "Mining", desc = "Mining features")
    public final MiningConfig mining = new MiningConfig();

    @Expose
    @Category(name = "Diana", desc = "Diana event tracking & overlay")
    public final DianaCategory diana = new DianaCategory();

    public static class DianaCategory {

        @Expose
        @ConfigOption(name = "Diana Tracker", desc = "Enables tracking when Ancestral Spade is used")
        @ConfigEditorBoolean
        public boolean enabled = true;

        @Expose
        @ConfigOption(name = "Show Overlay", desc = "Show the Diana stats HUD while tracking")
        @ConfigEditorBoolean
        public boolean showOverlay = true;

        @Expose
        @ConfigOption(name = "Overlay Background", desc = "Draw a dark background behind the overlay")
        @ConfigEditorBoolean
        public boolean overlayBackground = true;

        @Expose
        @ConfigOption(name = "Edit Overlay Position", desc = "Drag the overlay to reposition it")
        @ConfigEditorButton(runnableId = "openDianaEditor", buttonText = "Edit")
        public boolean editPosDummy = false;

        @Expose
        @ConfigOption(name = "Overlay Scale", desc = "Size of the Diana stats overlay")
        @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
        public float overlayScale = 1f;

        @Expose
        public Position overlayPos = new Position(4, 200);
    }

    @Expose
    @Category(name = "Dungeons", desc = "Dungeon features")
    public final DungeonsConfig dungeons = new DungeonsConfig();

    public void executeRunnable(String runnableId) {
        switch (runnableId) {
            case "openWaypointGroupGui":
                JefConfig.openWaypointGroupGui();
                break;
            case "openStatsEditor":
                JefConfig.openStatsEditor();
                break;
            case "openHudEditor":
                JefConfig.openHudEditor();
                break;
            case "openDianaEditor":
                JefConfig.openDianaEditor();   // implement in JefConfig alongside openStatsEditor()
                break;
        }
    }
}