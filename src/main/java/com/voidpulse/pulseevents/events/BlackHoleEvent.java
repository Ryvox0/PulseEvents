package com.voidpulse.pulseevents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
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
        Player anchor = Bukkit.getOnlinePlayers().iterator().next();
        center = anchor.getLocation().clone().add(0.0, 6.0, 0.0);

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (center == null || center.getWorld() == null) {
                return;
            }

            spawnParticles();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getWorld().equals(center.getWorld())) {
                    continue;
                }

                Vector offset = center.toVector().subtract(p.getLocation().toVector());
                double distance = Math.max(1.0, offset.length());
                Vector pull = offset.normalize().multiply(Math.min(0.08, 0.02 + (0.12 / distance)));
                pull.setY(Math.max(-0.04, Math.min(0.05, pull.getY())));

                Vector velocity = p.getVelocity().add(pull);
                if (velocity.lengthSquared() > 1.2) {
                    velocity = velocity.normalize().multiply(1.1);
                }

                p.setVelocity(velocity);
            }
        }, 0L, 2L);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        center = null;
    }

    @Override
    public int getDuration() {
        return 25;
    }

    private void spawnParticles() {
        center.getWorld().spawnParticle(Particle.PORTAL, center, 24, 0.7, 0.7, 0.7, 0.05);
        center.getWorld().spawnParticle(Particle.SMOKE_NORMAL, center, 10, 0.3, 0.3, 0.3, 0.01);
        center.getWorld().spawnParticle(Particle.SPELL_WITCH, center, 4, 0.15, 0.15, 0.15, 0.0);

        for (int i = 0; i < 10; i++) {
            double angle = (Math.PI * 2D * i) / 10D;
            Location ringPoint = center.clone().add(Math.cos(angle) * 1.4, 0.0, Math.sin(angle) * 1.4);
            center.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, ringPoint, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
