package com.jef.justenoughfakepixel.features.diana;

import com.jef.justenoughfakepixel.core.config.command.SimpleCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;


public class DianaCommand extends SimpleCommand {

    private static final String PREFIX = EnumChatFormatting.DARK_AQUA + "[Diana] " + EnumChatFormatting.RESET;

    @Override public String getName()  { return "resetdiana"; }
    @Override public String getUsage() { return "/resetdiana"; }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        DianaStats s = DianaStats.getInstance();
        s.reset();
        s.save();
        sender.addChatMessage(new ChatComponentText(
                PREFIX + EnumChatFormatting.GREEN + "Diana stats have been reset."));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return Collections.emptyList();
    }
}