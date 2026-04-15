package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import org.bukkit.command.CommandSender;

public class ToggleSubCommand extends BaseSubCommand {

    public ToggleSubCommand(PulseEvents plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "toggle";
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

        if (args.length != 1) {
            sender.sendMessage(lang.getWithPrefix("command.toggle.usage"));
            return;
        }

        String mode = args[0].toLowerCase();
        switch (mode) {
            case "on" -> {
                eventManager.setEventsSystemEnabled(true);
                triggerCommandCooldown(sender);
                sender.sendMessage(lang.getWithPrefix("command.toggle.enabled"));
            }
            case "off" -> {
                eventManager.setEventsSystemEnabled(false);
                triggerCommandCooldown(sender);
                sender.sendMessage(lang.getWithPrefix("command.toggle.disabled"));
            }
            default -> sender.sendMessage(lang.getWithPrefix("command.toggle.usage"));
        }
    }
}
