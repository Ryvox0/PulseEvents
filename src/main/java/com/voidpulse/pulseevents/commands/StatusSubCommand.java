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
    public String getPermission() {
        return "pulseevents.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!eventManager.isEventsSystemEnabled()) {
            sender.sendMessage(lang.getWithPrefix("command.system-disabled"));
            return;
        }

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
