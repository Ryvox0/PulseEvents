package com.voidpulse.pulseevents.manager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> playerEventStartCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerCommandCooldowns = new ConcurrentHashMap<>();
    private long globalEventStartAvailableAt;
    private int eventStartCooldownSeconds;
    private int commandCooldownSeconds;

    public CooldownManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        eventStartCooldownSeconds = Math.max(0, plugin.getConfig().getInt("cooldown.event-start", 300));
        commandCooldownSeconds = Math.max(0, plugin.getConfig().getInt("cooldown.command", 5));
    }

    public long getRemainingGlobalEventStartSeconds() {
        return getRemainingSeconds(globalEventStartAvailableAt);
    }

    public long getRemainingPlayerEventStartSeconds(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return 0L;
        }

        return getRemainingSeconds(playerEventStartCooldowns.get(player.getUniqueId()));
    }

    public long getRemainingPlayerCommandSeconds(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return 0L;
        }

        return getRemainingSeconds(playerCommandCooldowns.get(player.getUniqueId()));
    }

    public void triggerEventStartCooldown(CommandSender sender) {
        long availableAt = System.currentTimeMillis() + (eventStartCooldownSeconds * 1000L);
        globalEventStartAvailableAt = availableAt;

        if (sender instanceof Player player) {
            playerEventStartCooldowns.put(player.getUniqueId(), availableAt);
        }
    }

    public void triggerCommandCooldown(CommandSender sender) {
        if (commandCooldownSeconds <= 0 || !(sender instanceof Player player)) {
            return;
        }

        long availableAt = System.currentTimeMillis() + (commandCooldownSeconds * 1000L);
        playerCommandCooldowns.put(player.getUniqueId(), availableAt);
    }

    private long getRemainingSeconds(Long availableAt) {
        if (availableAt == null) {
            return 0L;
        }

        long remainingMillis = availableAt - System.currentTimeMillis();
        if (remainingMillis <= 0L) {
            return 0L;
        }

        return (remainingMillis + 999L) / 1000L;
    }
}
