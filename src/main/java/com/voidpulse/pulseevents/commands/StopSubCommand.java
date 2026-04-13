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
    public boolean requiresAdmin() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!eventManager.isEventRunning()) {
            sender.sendMessage(lang.getWithPrefix("command.stop.no-event"));
            return;
        }

        String eventName = eventManager.getCurrentEventDisplayName();
        eventManager.stopCurrent();
        sender.sendMessage(lang.getWithPrefix("command.stop.success", "%event%", eventName));
    }
}
