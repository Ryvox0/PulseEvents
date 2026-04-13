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
    public boolean requiresAdmin() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadPlugin();
        sender.sendMessage(lang.getWithPrefix(
                "command.reload.success",
                "%language%",
                plugin.getConfig().getString("settings.language", "en")
        ));
    }
}
