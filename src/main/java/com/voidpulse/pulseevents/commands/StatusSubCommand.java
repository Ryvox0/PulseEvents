package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import org.bukkit.command.CommandSender;

public class StatusSubCommand extends BaseSubCommand {

    public StatusSubCommand(PulseEvents plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public boolean requiresAdmin() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (eventManager.isEventRunning()) {
            sender.sendMessage(lang.getWithPrefix(
                    "command.status.running",
                    "%event%",
                    eventManager.getCurrentEventDisplayName()
            ));
            return;
        }

        sender.sendMessage(lang.getWithPrefix("command.status.idle"));
    }
}
