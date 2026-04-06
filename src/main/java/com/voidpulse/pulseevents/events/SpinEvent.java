package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SpinEvent implements PulseEvent {

    private final JavaPlugin plugin;
    private BukkitTask task;

    public SpinEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Spin";
    }

    @Override
    public void start() {

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (Player p : Bukkit.getOnlinePlayers()) {

                p.setRotation(p.getLocation().getYaw() + 20f, p.getLocation().getPitch());
            }

        }, 0L, 2L);
    }

    @Override
    public void stop() {
        if (task != null) task.cancel();
    }

    @Override
    public int getDuration() {
        return 15;
    }
}