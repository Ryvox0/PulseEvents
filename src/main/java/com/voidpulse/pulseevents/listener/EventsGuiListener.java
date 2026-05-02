package com.voidpulse.pulseevents.listener;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.events.PulseEvent;
import com.voidpulse.pulseevents.manager.EventManager;
import com.voidpulse.pulseevents.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventsGuiListener implements Listener {

    private static final int INVENTORY_SIZE = 27;
    private static final int SUMMARY_SLOT = 26;
    private static final String HOLDER_KEY = "pulseevents-events-menu";

    private final PulseEvents plugin;
    private final EventManager eventManager;
    private final LanguageManager lang;

    public EventsGuiListener(PulseEvents plugin, EventManager eventManager, LanguageManager lang) {
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.lang = lang;
    }

    public boolean openFor(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        player.openInventory(createInventory());
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!isEventsMenu(event.getView().getTopInventory())) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        int slot = event.getSlot();
        List<PulseEvent> events = getSortedEvents();
        if (slot < 0 || slot >= events.size()) {
            return;
        }

        PulseEvent pulseEvent = events.get(slot);

        if (event.getClick() == ClickType.MIDDLE) {
            handleQueueAdd(player, pulseEvent);
            player.openInventory(createInventory());
            return;
        }

        int change = resolveChange(event.getClick());
        if (change == 0) {
            return;
        }

        int updatedChance = Math.max(0, eventManager.getEventChance(pulseEvent) + change);
        eventManager.setEventChance(pulseEvent, updatedChance);

        player.sendMessage(lang.getWithPrefix(
                "gui.events.updated",
                "%event%",
                eventManager.getDisplayName(pulseEvent),
                "%chance%",
                String.valueOf(updatedChance)
        ));
        player.openInventory(createInventory());
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(new EventsMenuHolder(), INVENTORY_SIZE, lang.get("gui.events.title"));
        List<PulseEvent> events = getSortedEvents();

        for (int i = 0; i < Math.min(events.size(), INVENTORY_SIZE); i++) {
            if (i == SUMMARY_SLOT) {
                break;
            }
            inventory.setItem(i, createEventItem(events.get(i)));
        }

        inventory.setItem(SUMMARY_SLOT, createQueueSummaryItem());

        return inventory;
    }

    private ItemStack createEventItem(PulseEvent pulseEvent) {
        ItemStack item = new ItemStack(resolveMaterial(pulseEvent));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(lang.get(
                "gui.events.item-name",
                "%event%",
                eventManager.getDisplayName(pulseEvent)
        ));

        List<String> lore = new ArrayList<>();
        lore.add(lang.get(
                "gui.events.item-chance",
                "%chance%",
                String.valueOf(eventManager.getEventChance(pulseEvent))
        ));
        lore.add(lang.get("gui.events.item-left"));
        lore.add(lang.get("gui.events.item-right"));
        lore.add(lang.get("gui.events.item-shift-left"));
        lore.add(lang.get("gui.events.item-shift-right"));
        lore.add(lang.get("gui.events.item-middle"));
        lore.add(lang.get(
                "gui.events.item-queued",
                "%amount%",
                String.valueOf(countQueuedCopies(pulseEvent))
        ));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createQueueSummaryItem() {
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(lang.get("gui.events.queue-name"));

        List<String> lore = new ArrayList<>();
        lore.add(lang.get(
                "gui.events.queue-size",
                "%amount%",
                String.valueOf(eventManager.getQueuedEventDisplayNames().size())
        ));
        lore.add(lang.get("gui.events.queue-hint"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private Material resolveMaterial(PulseEvent pulseEvent) {
        return switch (eventManager.getConfigKey(pulseEvent)) {
            case "coin-rain" -> Material.SUNFLOWER;
            case "lightning-storm" -> Material.LIGHTNING_ROD;
            case "tnt-rain" -> Material.TNT;
            case "mob-swarm" -> Material.ZOMBIE_HEAD;
            case "random-teleport" -> Material.ENDER_PEARL;
            case "fire-feet" -> Material.FLINT_AND_STEEL;
            case "freeze" -> Material.ICE;
            case "black-hole" -> Material.OBSIDIAN;
            case "random-effects" -> Material.POTION;
            case "target-player" -> Material.CROSSBOW;
            case "spin" -> Material.COMPASS;
            default -> Material.NETHER_STAR;
        };
    }

    private List<PulseEvent> getSortedEvents() {
        List<PulseEvent> events = eventManager.getRegisteredEvents();
        events.sort(Comparator.comparing(eventManager::getDisplayName));
        return events;
    }

    private boolean isEventsMenu(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof EventsMenuHolder;
    }

    private int resolveChange(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> 5;
            case SHIFT_LEFT -> 20;
            case RIGHT -> -5;
            case SHIFT_RIGHT -> -20;
            default -> 0;
        };
    }

    private void handleQueueAdd(Player player, PulseEvent pulseEvent) {
        if (!player.hasPermission("pulseevents.queue.add") && !player.isOp()) {
            player.sendMessage(lang.getWithPrefix("command.no-permission"));
            return;
        }

        if (!eventManager.isEventsSystemEnabled()) {
            player.sendMessage(lang.getWithPrefix("command.system-disabled"));
            return;
        }

        if (!eventManager.enqueueEvent(pulseEvent.getName())) {
            player.sendMessage(lang.getWithPrefix(
                    "command.queue.invalid-event",
                    "%event%",
                    eventManager.getDisplayName(pulseEvent)
            ));
            return;
        }

        player.sendMessage(lang.getWithPrefix(
                "gui.events.queued",
                "%event%",
                eventManager.getDisplayName(pulseEvent),
                "%amount%",
                String.valueOf(countQueuedCopies(pulseEvent))
        ));
    }

    private int countQueuedCopies(PulseEvent pulseEvent) {
        String displayName = eventManager.getDisplayName(pulseEvent);
        int count = 0;

        for (String queuedEvent : eventManager.getQueuedEventDisplayNames()) {
            if (queuedEvent.equals(displayName)) {
                count++;
            }
        }

        return count;
    }

    private static final class EventsMenuHolder implements org.bukkit.inventory.InventoryHolder {
        @Override
        public Inventory getInventory() {
            return Bukkit.createInventory(this, INVENTORY_SIZE, HOLDER_KEY);
        }
    }
}
