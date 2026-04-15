package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class QueueSubCommand extends BaseSubCommand {

    public QueueSubCommand(PulseEvents plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(lang.getWithPrefix("command.queue.usage"));
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add" -> handleAdd(sender, args);
            case "list" -> handleList(sender);
            case "remove" -> handleRemove(sender, args);
            case "clear" -> handleClear(sender);
            default -> sender.sendMessage(lang.getWithPrefix("command.queue.usage"));
        }
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("pulseevents.queue.add") && !sender.isOp()) {
            sender.sendMessage(lang.getWithPrefix("command.no-permission"));
            return;
        }

        if (!enforceCommandCooldown(sender)) {
            return;
        }

        if (!eventManager.isEventsSystemEnabled()) {
            sender.sendMessage(lang.getWithPrefix("command.system-disabled"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(lang.getWithPrefix("command.queue.add-usage"));
            return;
        }

        String eventName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (!eventManager.enqueueEvent(eventName)) {
            sender.sendMessage(lang.getWithPrefix("command.queue.invalid-event", "%event%", eventName));
            return;
        }

        triggerCommandCooldown(sender);
        sender.sendMessage(lang.getWithPrefix(
                "command.queue.add-success",
                "%event%",
                eventManager.getDisplayName(eventManager.findEvent(eventName))
        ));
    }

    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("pulseevents.queue.list") && !sender.isOp()) {
            sender.sendMessage(lang.getWithPrefix("command.no-permission"));
            return;
        }

        List<String> queue = eventManager.getQueuedEventDisplayNames();
        if (queue.isEmpty()) {
            sender.sendMessage(lang.getWithPrefix("command.queue.empty"));
            return;
        }

        sender.sendMessage(lang.getWithPrefix("command.queue.header"));
        for (int i = 0; i < queue.size(); i++) {
            sender.sendMessage(lang.get(
                    "command.queue.line",
                    "%index%",
                    String.valueOf(i + 1),
                    "%event%",
                    queue.get(i)
            ));
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("pulseevents.queue.remove") && !sender.isOp()) {
            sender.sendMessage(lang.getWithPrefix("command.no-permission"));
            return;
        }

        if (!enforceCommandCooldown(sender)) {
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(lang.getWithPrefix("command.queue.remove-usage"));
            return;
        }

        int index;
        try {
            index = Integer.parseInt(args[1]) - 1;
        } catch (NumberFormatException exception) {
            sender.sendMessage(lang.getWithPrefix("command.queue.invalid-index"));
            return;
        }

        List<String> queueBefore = eventManager.getQueuedEventDisplayNames();
        if (index < 0 || index >= queueBefore.size()) {
            sender.sendMessage(lang.getWithPrefix("command.queue.invalid-index"));
            return;
        }

        String removedEvent = queueBefore.get(index);
        if (!eventManager.removeQueuedEvent(index)) {
            sender.sendMessage(lang.getWithPrefix("command.queue.invalid-index"));
            return;
        }

        triggerCommandCooldown(sender);
        sender.sendMessage(lang.getWithPrefix("command.queue.remove-success", "%event%", removedEvent));
    }

    private void handleClear(CommandSender sender) {
        if (!sender.hasPermission("pulseevents.queue.clear") && !sender.isOp()) {
            sender.sendMessage(lang.getWithPrefix("command.no-permission"));
            return;
        }

        if (!enforceCommandCooldown(sender)) {
            return;
        }

        eventManager.clearQueue();
        triggerCommandCooldown(sender);
        sender.sendMessage(lang.getWithPrefix("command.queue.clear-success"));
    }
}
