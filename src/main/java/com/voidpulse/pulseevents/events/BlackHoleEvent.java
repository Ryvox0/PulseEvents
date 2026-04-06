package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class BlackHoleEvent implements PulseEvent {

    private final JavaPlugin plugin;
    private BukkitTask task;
    private Location center;

    public BlackHoleEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Black Hole";
    }

    @Override
    public void start() {
        center = Bukkit.getOnlinePlayers().iterator().next().getLocation();

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {

                Vector v = center.toVector().subtract(p.getLocation().toVector()).normalize().multiply(0.5);
                p.setVelocity(p.getVelocity().add(v));
            }
        }, 0L, 5L);
    }

    @Override
    public void stop() {
        if (task != null) task.cancel();
    }

    @Override
    public int getDuration() {
        return 25;
    }
}