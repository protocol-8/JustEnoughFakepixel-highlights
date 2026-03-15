package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scoreboard {

    @Expose
    @ConfigOption(name = "Custom Scoreboard", desc = "Settings for the custom scoreboard overlay")
    @ConfigEditorAccordion(id = 20)
    public boolean scoreboardAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Replace the vanilla sidebar with a custom scoreboard")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the scoreboard")
    @ConfigEditorColour
    @ConfigAccordionId(id = 20)
    public String scoreboardBg = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the scoreboard corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 20f, minStep = 1f)
    @ConfigAccordionId(id = 20)
    public float cornerRadius = 8f;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the scoreboard")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 2.5f, minStep = 0.1f)
    @ConfigAccordionId(id = 20)
    public float scale = 1.0f;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag to reposition the scoreboard")
    @ConfigEditorButton(runnableId = "openScoreboardEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 20)
    public boolean editPosDummy = false;

    @Expose
    @ConfigOption(name = "Scoreboard Lines", desc = "Choose which lines to show and drag to reorder. Lines not found on the scoreboard are hidden automatically.")
    @ConfigEditorDraggableList(exampleText = {
            "\u00a7e03/15/26 \u00a78dh-1",           // 0  SERVER
            "\u00a7fLate Summer \u00a7b11th",             // 1  SEASON
            "\u00a7f10:40pm",                        // 2  TIME
            "\u00a7b⏣ Village",                          // 3  LOCATION
            "\u00a7fPurse: \u00a7652,763,737",            // 4  PURSE
            "\u00a7fBank: \u00a76249M",                   // 5  BANK
            "\u00a7fBits: \u00a7b59,364",                 // 6  BITS
            "\u00a7fPower: \u00a7dSighted \u00a78(1,863)",     // 7  POWER
            "\u00a7fFetchur: \u00a7eSand",                // 8  FETCHUR
            "\u00a7fSlayer Quest",                   // 9  SLAYER
            "\u00a7fGems: \u00a7a57,873",                 // 10 GEMS
            "\u00a77♲ Ironman",                      // 11 PROFILE_TYPE
            "\u00a76Fishing Festival \u00a7f12m 30s",     // 12 EVENT
            "\u00a76Cookie Buff: \u00a7f3d 17h",           // 13 COOKIE
            "\u00a78─────────────────", // 14 EMPTY LINE
            "\u00a78─────────────────", // 15 EMPTY LINE
            "\u00a78─────────────────",// 16 EMPTY LINE
            "\u00a78─────────────────",// 17 EMPTY LINE
            "\u00a78─────────────────",// 18 EMPTY LINE
            "\u00a78─────────────────",// 19 EMPTY LINE
            "\u00a78─────────────────", // 20 EMPTY LINE
            "\u32D6\u00a76 Hub"
    })
    @ConfigAccordionId(id = 20)
    public List<Integer> scoreboardLines =
            new ArrayList<>(Arrays.asList(0, 1, 2, 3, 14, 4, 5, 6, 14, 7, 8, 14, 9, 10, 11, 12, 13, 21));

    @Expose
    public Position position = new Position(-2, 10);
}