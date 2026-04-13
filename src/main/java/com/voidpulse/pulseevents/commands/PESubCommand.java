package com.voidpulse.pulseevents.commands;

import org.bukkit.command.CommandSender;

public interface PESubCommand {

    String getName();

    boolean requiresAdmin();

    void execute(CommandSender sender, String[] args);
}
