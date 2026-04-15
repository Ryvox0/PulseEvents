package com.voidpulse.pulseevents.commands;

import org.bukkit.command.CommandSender;

public interface PESubCommand {

    String getName();

    String getPermission();

    void execute(CommandSender sender, String[] args);
}
