package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.manager.EventManager;
import com.voidpulse.pulseevents.manager.LanguageManager;
import com.voidpulse.pulseevents.manager.CooldownManager;
import com.voidpulse.pulseevents.update.UpdateChecker;
import org.bukkit.command.CommandSender;

public abstract class BaseSubCommand implements PESubCommand {

    protected final PulseEvents plugin;
    protected final LanguageManager lang;
    protected final EventManager eventManager;
    protected final UpdateChecker updateChecker;
    protected final CooldownManager cooldownManager;

    protected BaseSubCommand(PulseEvents plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.eventManager = plugin.getEventManager();
        this.updateChecker = plugin.getUpdateChecker();
        this.cooldownManager = plugin.getCooldownManager();
    }

    @Override
    public String getPermission() {
        return null;
    }

    protected boolean enforceCommandCooldown(CommandSender sender) {
        long remaining = cooldownManager.getRemainingPlayerCommandSeconds(sender);
        if (remaining > 0L) {
            sender.sendMessage(lang.getWithPrefix("command.cooldown.command", "%time%", String.valueOf(remaining)));
            return false;
        }

        return true;
    }

    protected void triggerCommandCooldown(CommandSender sender) {
        cooldownManager.triggerCommandCooldown(sender);
    }

    protected boolean enforceEventStartCooldown(CommandSender sender) {
        long globalRemaining = cooldownManager.getRemainingGlobalEventStartSeconds();
        if (globalRemaining > 0L) {
            sender.sendMessage(lang.getWithPrefix(
                    "command.cooldown.event-start-global",
                    "%time%",
                    String.valueOf(globalRemaining)
            ));
            return false;
        }

        long playerRemaining = cooldownManager.getRemainingPlayerEventStartSeconds(sender);
        if (playerRemaining > 0L) {
            sender.sendMessage(lang.getWithPrefix(
                    "command.cooldown.event-start-player",
                    "%time%",
                    String.valueOf(playerRemaining)
            ));
            return false;
        }

        return true;
    }

    protected void triggerEventStartCooldown(CommandSender sender) {
        cooldownManager.triggerEventStartCooldown(sender);
    }
}
