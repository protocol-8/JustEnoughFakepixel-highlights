package com.jef.justenoughfakepixel.features.farming;

import com.jef.justenoughfakepixel.core.config.command.SimpleCommand;
import net.minecraft.command.ICommandSender;

public class LockMouseCommand extends SimpleCommand {

    @Override public String getName()  { return "lockyp"; }
    @Override public String getUsage() { return "/lockyp"; }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        LockMouse.setLocked(!LockMouse.isLocked());
    }
}