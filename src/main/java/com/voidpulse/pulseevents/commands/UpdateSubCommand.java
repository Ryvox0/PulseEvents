package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import org.bukkit.command.CommandSender;

public class UpdateSubCommand extends BaseSubCommand {

    public UpdateSubCommand(PulseEvents plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public boolean requiresAdmin() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(lang.getWithPrefix("command.update.checking"));
        updateChecker.check(sender);
    }
}
