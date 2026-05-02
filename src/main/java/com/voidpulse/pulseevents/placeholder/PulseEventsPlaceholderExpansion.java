package com.voidpulse.pulseevents.placeholder;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.manager.EventManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PulseEventsPlaceholderExpansion extends PlaceholderExpansion {

    private final PulseEvents plugin;

    public PulseEventsPlaceholderExpansion(PulseEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "pulseevents";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        EventManager eventManager = plugin.getEventManager();

        return switch (params.toLowerCase()) {
            case "current_event" -> {
                String currentEvent = eventManager.getCurrentEventDisplayName();
                yield currentEvent == null ? "none" : currentEvent;
            }
            case "event_active" -> String.valueOf(eventManager.isEventRunning());
            case "events_enabled" -> String.valueOf(eventManager.isEventsSystemEnabled());
            case "queue_size" -> String.valueOf(eventManager.getQueuedEventDisplayNames().size());
            case "registered_events" -> String.valueOf(eventManager.getRegisteredEvents().size());
            default -> null;
        };
    }
}
