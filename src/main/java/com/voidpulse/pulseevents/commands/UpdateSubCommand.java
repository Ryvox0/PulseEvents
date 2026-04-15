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
    public String getPermission() {
        return "pulseevents.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!enforceCommandCooldown(sender)) {
            return;
        }

        triggerCommandCooldown(sender);
        sender.sendMessage(lang.getWithPrefix("command.update.checking"));
        updateChecker.check(sender);
    }
}
