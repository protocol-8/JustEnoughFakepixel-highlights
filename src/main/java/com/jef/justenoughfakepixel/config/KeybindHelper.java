package com.jef.justenoughfakepixel.config;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;


public final class KeybindHelper {

    private KeybindHelper() {}

    public static String getKeyName(int keyCode) {
        if (keyCode == 0) return "NONE";
        if (keyCode < 0)  return "Button " + (keyCode + 101);
        try {
            String name = Keyboard.getKeyName(keyCode);
            if (name == null)                       return "???";
            if (name.equalsIgnoreCase("LMENU"))     return "LALT";
            if (name.equalsIgnoreCase("RMENU"))     return "RALT";
            return name;
        } catch (Exception e) {
            return "???";
        }
    }

    public static boolean isKeyValid(int keyCode) {
        return keyCode != 0;
    }

    /** Returns {@code true} while the key/button is held down (poll-based). */
    public static boolean isKeyDown(int keyCode) {
        if (!isKeyValid(keyCode)) return false;
        return keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
    }

    /** Returns {@code true} on the exact event frame the key/button is pressed (event-based). */
    public static boolean isKeyPressed(int keyCode) {
        if (!isKeyValid(keyCode)) return false;
        return keyCode < 0
                ? Mouse.getEventButtonState() && Mouse.getEventButton() == keyCode + 100
                : Keyboard.getEventKeyState() && Keyboard.getEventKey() == keyCode;
    }
}