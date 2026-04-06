package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TargetPlayerEvent implements PulseEvent {

    private final JavaPlugin plugin;
    private Player target;

    public TargetPlayerEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Target Player";
    }

    @Override
    public void start() {

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        target = players.get(new Random().nextInt(players.size()));

        Bukkit.broadcastMessage("§cTARGET: §e" + target.getName());
    }

    @Override
    public void stop() {
        target = null;
    }

    @Override
    public int getDuration() {
        return 40;
    }
}