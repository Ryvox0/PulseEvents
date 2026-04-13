package com.voidpulse.pulseevents.events;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.manager.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class CoinRainEvent implements PulseEvent {

    private static NamespacedKey rewardKey;

    private final JavaPlugin plugin;
    private final EconomyManager economyManager;
    private BukkitTask task;

    public CoinRainEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        this.economyManager = ((PulseEvents) plugin).getEconomyManager();
        rewardKey = new NamespacedKey(plugin, "coin_rain_reward");
    }

    @Override
    public String getName() {
        return "Coin Rain";
    }

    @Override
    public void start() {
        if (!economyManager.isAvailable()) {
            plugin.getLogger().warning("Coin Rain cannot start because no Vault economy provider is available.");
            return;
        }

        double minReward = plugin.getConfig().getDouble("events.coin-rain.reward.min", 1.0D);
        double configuredMaxReward = plugin.getConfig().getDouble("events.coin-rain.reward.max", 5.0D);
        long intervalTicks = plugin.getConfig().getLong("events.coin-rain.drop-interval-ticks", 10L);
        double velocityMultiplier = plugin.getConfig().getDouble("events.coin-rain.velocity-multiplier", 0.4D);
        final double maxReward = Math.max(configuredMaxReward, minReward);
        final double finalVelocityMultiplier = velocityMultiplier;

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                double reward = ThreadLocalRandom.current().nextDouble(minReward, maxReward + 0.0000001D);
                ItemStack stack = new ItemStack(Material.GOLD_NUGGET);
                ItemMeta meta = stack.getItemMeta();

                if (meta != null) {
                    meta.getPersistentDataContainer().set(
                            rewardKey,
                            PersistentDataType.STRING,
                            String.format(Locale.US, "%.2f", reward)
                    );
                    stack.setItemMeta(meta);
                }

                Item item = player.getWorld().dropItem(
                        player.getLocation().add(0, 6, 0),
                        stack
                );

                item.setPickupDelay(0);
                item.setVelocity(new Vector(
                        (Math.random() - 0.5) * finalVelocityMultiplier,
                        0.5,
                        (Math.random() - 0.5) * finalVelocityMultiplier
                ));
            }
        }, 0L, intervalTicks);
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
        return 45;
    }

    public static NamespacedKey getRewardKey() {
        return rewardKey;
    }
}
