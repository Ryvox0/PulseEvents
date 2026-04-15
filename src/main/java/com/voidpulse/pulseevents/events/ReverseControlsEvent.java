package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReverseControlsEvent implements PulseEvent, Listener {

    private final JavaPlugin plugin;
    private final Set<UUID> bypassPlayers = new HashSet<>();
    private boolean active;

    public ReverseControlsEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Reverse Controls";
    }

    @Override
    public void start() {
        active = true;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop() {
        active = false;
        bypassPlayers.clear();
        HandlerList.unregisterAll(this);
    }

    @Override
    public int getDuration() {
        return 30;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!active || event.getTo() == null) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (bypassPlayers.remove(playerId)) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();

        if (Math.abs(deltaX) < 0.001D && Math.abs(deltaZ) < 0.001D) {
            return;
        }

        Location reversed = from.clone();
        reversed.setX(from.getX() - deltaX);
        reversed.setY(to.getY());
        reversed.setZ(from.getZ() - deltaZ);
        reversed.setYaw(to.getYaw());
        reversed.setPitch(to.getPitch());

        bypassPlayers.add(playerId);
        event.setTo(reversed);
    }
}
