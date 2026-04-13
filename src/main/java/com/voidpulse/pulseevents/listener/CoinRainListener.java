package com.voidpulse.pulseevents.listener;

import com.voidpulse.pulseevents.events.CoinRainEvent;
import com.voidpulse.pulseevents.manager.EconomyManager;
import com.voidpulse.pulseevents.manager.LanguageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CoinRainListener implements Listener {

    private final EconomyManager economyManager;
    private final LanguageManager lang;

    public CoinRainListener(EconomyManager economyManager, LanguageManager lang) {
        this.economyManager = economyManager;
        this.lang = lang;
    }

    @EventHandler
    public void onCoinPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = event.getItem().getItemStack();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }

        String rewardValue = meta.getPersistentDataContainer().get(
                CoinRainEvent.getRewardKey(),
                PersistentDataType.STRING
        );

        if (rewardValue == null) {
            return;
        }

        event.setCancelled(true);
        event.getItem().remove();

        double reward;
        try {
            reward = Double.parseDouble(rewardValue);
        } catch (NumberFormatException exception) {
            return;
        }

        if (!economyManager.deposit(player, reward)) {
            player.sendMessage(lang.getWithPrefix("event.coin-rain.deposit-failed"));
            return;
        }

        player.sendMessage(lang.getWithPrefix(
                "event.coin-rain.reward-received",
                "%amount%",
                economyManager.format(reward)
        ));
    }
}
