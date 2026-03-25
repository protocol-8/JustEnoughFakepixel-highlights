package com.jef.justenoughfakepixel.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ActionBarXpGainEvent extends Event {

    private final String formattedText;

    public ActionBarXpGainEvent(String formattedText) {
        this.formattedText = formattedText;
    }

    public String getFormattedText() {
        return formattedText;
    }
}