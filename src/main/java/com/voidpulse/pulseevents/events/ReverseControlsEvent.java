package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ReverseControlsEvent implements PulseEvent, Listener {

    private final JavaPlugin plugin;
    private boolean active = false;

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
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!active) return;

        Player p = e.getPlayer();

        if (e.getFrom().getDirection() == null) return;

        p.setVelocity(p.getVelocity().multiply(-1));
    }

    @Override
    public int getDuration() {
        return 20;
    }
}