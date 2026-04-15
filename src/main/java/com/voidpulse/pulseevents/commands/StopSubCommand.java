package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import org.bukkit.command.CommandSender;

public class StopSubCommand extends BaseSubCommand {

    public StopSubCommand(PulseEvents plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "stop";
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

        if (!eventManager.isEventRunning()) {
            sender.sendMessage(lang.getWithPrefix("command.stop.no-event"));
            return;
        }

        String eventName = eventManager.getCurrentEventDisplayName();
        eventManager.stopCurrent();
        triggerCommandCooldown(sender);
        sender.sendMessage(lang.getWithPrefix("command.stop.success", "%event%", eventName));
    }
}
