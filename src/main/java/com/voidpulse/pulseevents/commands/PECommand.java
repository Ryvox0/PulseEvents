package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.manager.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class PECommand implements CommandExecutor {

    private final LanguageManager lang;
    private final Map<String, PESubCommand> subCommands = new LinkedHashMap<>();

    public PECommand(PulseEvents plugin) {
        this.lang = plugin.getLang();

        register(new HelpSubCommand(plugin));
        register(new StartSubCommand(plugin));
        register(new StopSubCommand(plugin));
        register(new StatusSubCommand(plugin));
        register(new ReloadSubCommand(plugin));
        register(new UpdateSubCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            subCommands.get("help").execute(sender, new String[0]);
            return true;
        }

        PESubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(lang.getWithPrefix("command.unknown"));
            subCommands.get("help").execute(sender, new String[0]);
            return true;
        }

        if (subCommand.requiresAdmin()
                && !(sender instanceof ConsoleCommandSender)
                && !sender.hasPermission("pulseevents.admin")) {
            sender.sendMessage(lang.getWithPrefix("command.no-permission"));
            return true;
        }

        String[] subArgs = new String[Math.max(0, args.length - 1)];
        if (args.length > 1) {
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        }

        subCommand.execute(sender, subArgs);
        return true;
    }

    private void register(PESubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }
}
