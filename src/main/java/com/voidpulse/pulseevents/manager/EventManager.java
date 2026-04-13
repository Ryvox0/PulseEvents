package com.voidpulse.pulseevents.manager;

import com.voidpulse.pulseevents.events.PulseEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SuppressWarnings({"deprecation", "unused"})
public class EventManager {

    private final JavaPlugin plugin;
    private final LiveUIManager liveUIManager;
    private final LanguageManager lang;
    private final List<PulseEvent> events = new ArrayList<>();
    private final Random random = new Random();
    private PulseEvent current;

    public EventManager(JavaPlugin plugin, LiveUIManager liveUIManager, LanguageManager lang) {
        this.plugin = plugin;
        this.liveUIManager = liveUIManager;
        this.lang = lang;
    }

    public void registerEvent(PulseEvent event) {
        events.add(event);
    }

    public boolean isEventRunning() {
        return current != null;
    }

    public PulseEvent getCurrentEvent() {
        return current;
    }

    public boolean startRandomEvent() {
        if (events.isEmpty()) {
            Bukkit.broadcastMessage(lang.getWithPrefix("event.no-events"));
            return false;
        }

        if (Bukkit.getOnlinePlayers().isEmpty()) {
            plugin.getLogger().fine("Skipping event start because no players are online.");
            return false;
        }

        if (current != null) {
            current.stop();
            liveUIManager.stop();
        }

        current = events.get(random.nextInt(events.size()));
        String eventName = getDisplayName(current);

        Bukkit.broadcastMessage(lang.getWithPrefix("event.start", "%event%", eventName));

        current.start();
        liveUIManager.start(eventName, current.getDuration());

        Bukkit.getScheduler().runTaskLater(
                plugin,
                this::stopCurrent,
                current.getDuration() * 20L
        );

        return true;
    }

    public boolean stopCurrent() {
        if (current == null) {
            return false;
        }

        String eventName = getDisplayName(current);

        current.stop();
        liveUIManager.stop();

        Bukkit.broadcastMessage(lang.getWithPrefix("event.end", "%event%", eventName));
        current = null;
        return true;
    }

    public boolean hasRegisteredEvents() {
        return !events.isEmpty();
    }

    public String getCurrentEventDisplayName() {
        return current == null ? null : getDisplayName(current);
    }

    public String getDisplayName(PulseEvent event) {
        String className = event.getClass().getSimpleName();
        String baseName = className.endsWith("Event")
                ? className.substring(0, className.length() - "Event".length())
                : className;

        String translationKey = "events."
                + baseName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase(Locale.ROOT)
                + ".name";

        return lang.getOrDefault(translationKey, toTitleCase(baseName));
    }

    private String toTitleCase(String text) {
        return text.replaceAll("([a-z])([A-Z])", "$1 $2");
    }
}
