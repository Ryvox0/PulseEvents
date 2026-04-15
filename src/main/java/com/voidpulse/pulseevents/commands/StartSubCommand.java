package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import org.bukkit.command.CommandSender;

public class StartSubCommand extends BaseSubCommand {

    public StartSubCommand(PulseEvents plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getPermission() {
        return "pulseevents.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!enforceCommandCooldown(sender) || !enforceEventStartCooldown(sender)) {
            return;
        }

        if (!eventManager.isEventsSystemEnabled()) {
            sender.sendMessage(lang.getWithPrefix("command.system-disabled"));
            return;
        }

        if (!eventManager.hasRegisteredEvents()) {
            sender.sendMessage(lang.getWithPrefix("command.start.no-events"));
            return;
        }

        if (eventManager.isEventRunning()) {
            sender.sendMessage(lang.getWithPrefix(
                    "command.start.already-running",
                    "%event%",
                    eventManager.getCurrentEventDisplayName()
            ));
            return;
        }

        if (eventManager.startRandomEvent()) {
            triggerEventStartCooldown(sender);
            triggerCommandCooldown(sender);
            sender.sendMessage(lang.getWithPrefix("command.start.success"));
        } else {
            sender.sendMessage(lang.getWithPrefix("command.start.failed"));
        }
    }
}
