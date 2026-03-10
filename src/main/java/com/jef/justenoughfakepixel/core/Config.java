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
        }
    }
}