package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class TNTRainEvent implements PulseEvent {

    private final JavaPlugin plugin;
    private BukkitTask task;

    public TNTRainEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "TNT Rain";
    }

    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Location loc = p.getLocation().add(0, 10, 0);
                TNTPrimed tnt = p.getWorld().spawn(loc, TNTPrimed.class);
                tnt.setFuseTicks(60);
            }
        }, 0L, 50L);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public int getDuration() {
        return 35;
    }
}
