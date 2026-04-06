package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class RandomEffectsEvent implements PulseEvent {

    private final JavaPlugin plugin;
    private BukkitTask task;
    private final Random random = new Random();

    public RandomEffectsEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Random Effects";
    }

    @Override
    public void start() {

        PotionEffectType[] effects = {
                PotionEffectType.SPEED,
                PotionEffectType.JUMP,
                PotionEffectType.SLOW,
                PotionEffectType.BLINDNESS,
                PotionEffectType.REGENERATION
        };

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (Player p : Bukkit.getOnlinePlayers()) {

                PotionEffectType type = effects[random.nextInt(effects.length)];

                p.addPotionEffect(new PotionEffect(type, 60, 1));
            }

        }, 0L, 20L);
    }

    @Override
    public void stop() {
        if (task != null) task.cancel();
    }

    @Override
    public int getDuration() {
        return 30;
    }
}