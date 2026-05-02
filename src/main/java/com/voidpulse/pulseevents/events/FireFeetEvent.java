package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class FireFeetEvent implements PulseEvent {

    private final JavaPlugin plugin;
    private BukkitTask task;

    public FireFeetEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Fire Feet";
    }

    @Override
    public void start() {

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (Player p : Bukkit.getOnlinePlayers()) {

                if (!p.isOnline()) continue;

                Block blockBelow = p.getLocation().clone().subtract(0, 1, 0).getBlock();

                if (!blockBelow.getType().isSolid()) continue;

                Block fireBlock = getTrailBlock(p);
                if (fireBlock != null && fireBlock.getType().isAir()) {
                    fireBlock.setType(Material.FIRE);
                }

                p.getWorld().spawnParticle(
                        Particle.FLAME,
                        p.getLocation().add(0, 0.1, 0),
                        8,
                        0.2, 0.0, 0.2,
                        0.01
                );
            }

        }, 0L, 2L);
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
        return 30;
    }

    private Block getTrailBlock(Player player) {
        Vector direction = player.getLocation().getDirection().setY(0);
        if (direction.lengthSquared() < 0.0001D) {
            return null;
        }

        direction.normalize().multiply(-0.8D);
        Block target = player.getLocation().clone().add(direction).getBlock();
        Block support = target.getRelative(0, -1, 0);

        if (!target.getType().isAir() || !support.getType().isSolid()) {
            return null;
        }

        return target;
    }
}
