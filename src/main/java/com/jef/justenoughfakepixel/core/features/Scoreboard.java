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
    @ConfigOption(name = "Scoreboard Lines", desc = "Choose which lines to show and drag to reorder")
    @ConfigEditorDraggableList(exampleText = {
            "Server / Date",
            "Season / Day",
            "Time",
            "Location",
            "Purse",
            "Bank",
            "Bits",
            "Power",
            "Fetchur"
    })
    @ConfigAccordionId(id = 20)
    public List<Integer> scoreboardLines =
            new ArrayList<>(Arrays.asList(0,1,3,2,4,5,6,7,8));

    @Expose
    public Position position = new Position(-2, 10);
}