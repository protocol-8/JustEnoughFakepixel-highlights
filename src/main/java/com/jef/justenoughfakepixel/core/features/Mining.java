package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class Mining {

    @Expose
    @ConfigOption(name = "Fetchur Overlay", desc = "Settings for the Fetchur item overlay")
    @ConfigEditorAccordion(id = 20)
    public boolean fetchurAccordion = false;

    @Expose
    @ConfigOption(name = "Show Fetchur Overlay", desc = "Shows today's Fetchur item on screen while in Skyblock")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean showFetchurOverlay = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the overlay (alpha controls opacity)")
    @ConfigEditorColour
    @ConfigAccordionId(id = 20)
    public String overlayBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    @ConfigAccordionId(id = 20)
    public int overlayCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Overlay Scale", desc = "Size of the Fetchur overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    @ConfigAccordionId(id = 20)
    public float fetchurOverlayScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Overlay Position", desc = "Drag to reposition the Fetchur overlay")
    @ConfigEditorButton(runnableId = "openFetchurEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 20)
    public boolean editFetchurPosDummy = false;

    @Expose
    public Position fetchurOverlayPos = new Position(4, 4);
}