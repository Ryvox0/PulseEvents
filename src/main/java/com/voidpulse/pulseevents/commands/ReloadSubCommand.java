package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends BaseSubCommand {

    public ReloadSubCommand(PulseEvents plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
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

        plugin.reloadPlugin();
        triggerCommandCooldown(sender);
        sender.sendMessage(lang.getWithPrefix(
                "command.reload.success",
                "%language%",
                plugin.getConfig().getString("settings.language", "en")
        ));
    }
}
