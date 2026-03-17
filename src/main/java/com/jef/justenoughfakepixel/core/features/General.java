package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class General {

    @Expose
    @ConfigOption(name = "Damage Splashes", desc = "Settings for damage number nametags")
    @ConfigEditorAccordion(id = 0)
    public boolean damageSplashesAccordion = false;

    @Expose
    @ConfigOption(name = "Hide Crit Splashes", desc = "Hides crit damage nametags (\u2727 stars)")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean hideCritSplashes = false;

    @Expose
    @ConfigOption(name = "Hide Non-Crit Splashes", desc = "Hides gray and fire-aspect damage numbers")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean hideNonCritSplashes = false;

    @Expose
    @ConfigOption(name = "Gyro Wand Helper", desc = "Settings for the Gyrokinetic Wand helper")
    @ConfigEditorAccordion(id = 20)
    public boolean gyroWandAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Shows the area of effect ring when holding the Gyrokinetic Wand")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean gyroWand = true;

    @Expose
    @ConfigOption(name = "Ring Thickness", desc = "Thickness of the area of effect ring")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 5f, minStep = 0.5f)
    @ConfigAccordionId(id = 20)
    public float gyroWandThickness = 2f;

    @Expose
    @ConfigOption(name = "Cooldown Timer", desc = "Shows a cooldown timer overlay when the Gyro Wand ability is on cooldown")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean gyroWandTimer = true;

    @Expose
    @ConfigOption(name = "Always Show Timer", desc = "Shows the cooldown timer even when the Gyro Wand is not held")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean gyroWandTimerAlways = false;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the cooldown timer overlay")
    @ConfigEditorColour
    @ConfigAccordionId(id = 20)
    public String gyroWandBgColor = "0:0:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the cooldown timer overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    @ConfigAccordionId(id = 20)
    public int gyroWandCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the cooldown timer overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    @ConfigAccordionId(id = 20)
    public float gyroWandScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Timer Position", desc = "Drag the cooldown timer overlay to reposition it")
    @ConfigEditorButton(runnableId = "openGyroWandEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 20)
    public boolean gyroWandEditPosDummy = false;

    @Expose
    public Position gyroWandPos = new Position(-295, -162);

    @Expose
    @ConfigOption(name = "Roman Numerals", desc = "Converts Roman numerals to integers in tooltips and tab list")
    @ConfigEditorBoolean
    public boolean romanNumerals = true;

    @Expose
    @ConfigOption(name = "Prevent Cursor Reset", desc = "Prevents the mouse cursor from resetting when opening GUIs")
    @ConfigEditorBoolean
    public boolean preventCursorReset = true;

    @Expose
    @ConfigOption(name = "Skyblock ID", desc = "Shows the skyblock item ID at the bottom of item tooltips")
    @ConfigEditorBoolean
    public boolean showSkyblockId = true;

    @Expose
    @ConfigOption(name = "Disable Enchant Glint", desc = "Removes the enchantment glint effect")
    @ConfigEditorBoolean
    public boolean disableEnchantGlint = false;

    @Expose
    @ConfigOption(name = "Brewing helper", desc = "Highlights brewing stands when done brewing")
    @ConfigEditorBoolean
    public boolean colorBrewingStands = true;
}