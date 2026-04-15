package com.voidpulse.pulseevents.manager;

import com.voidpulse.pulseevents.events.PulseEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

@SuppressWarnings({"deprecation", "unused"})
public class EventManager {

    private final JavaPlugin plugin;
    private final LiveUIManager liveUIManager;
    private final LanguageManager lang;
    private final List<PulseEvent> events = new ArrayList<>();
    private final Map<String, PulseEvent> eventsByKey = new LinkedHashMap<>();
    private final Queue<String> eventQueue = new LinkedList<>();
    private final Random random = new Random();
    private PulseEvent current;
    private AnnouncementManager announcementManager;
    private BukkitTask stopTask;
    private boolean eventsSystemEnabled;

    public EventManager(JavaPlugin plugin, LiveUIManager liveUIManager, LanguageManager lang) {
        this.plugin = plugin;
        this.liveUIManager = liveUIManager;
        this.lang = lang;
        this.eventsSystemEnabled = plugin.getConfig().getBoolean("events.enabled", true);
    }

    public void registerEvent(PulseEvent event) {
        events.add(event);
        eventsByKey.put(normalizeEventKey(event.getName()), event);
    }

    public void setAnnouncementManager(AnnouncementManager announcementManager) {
        this.announcementManager = announcementManager;
    }

    public boolean isEventRunning() {
        return current != null;
    }

    public boolean isEventsSystemEnabled() {
        return eventsSystemEnabled;
    }

    public void reloadState() {
        eventsSystemEnabled = plugin.getConfig().getBoolean("events.enabled", true);
    }

    public void setEventsSystemEnabled(boolean enabled) {
        eventsSystemEnabled = enabled;
        plugin.getConfig().set("events.enabled", enabled);
        plugin.saveConfig();

        if (!enabled) {
            if (current != null) {
                stopCurrent();
                return;
            }

            if (announcementManager != null) {
                announcementManager.stop();
            }
            return;
        }

        if (announcementManager != null) {
            announcementManager.refreshSchedules();
        }
    }

    public PulseEvent getCurrentEvent() {
        return current;
    }

    public boolean startRandomEvent() {
        if (!eventsSystemEnabled) {
            return false;
        }

        if (events.isEmpty()) {
            Bukkit.broadcastMessage(lang.getWithPrefix("event.no-events"));
            return false;
        }

        if (current != null) {
            return false;
        }

        if (Bukkit.getOnlinePlayers().isEmpty()) {
            plugin.getLogger().fine("Skipping event start because no players are online.");
            return false;
        }

        return startEvent(events.get(random.nextInt(events.size())));
    }

    public boolean enqueueEvent(String eventName) {
        if (!eventsSystemEnabled) {
            return false;
        }

        PulseEvent event = findEvent(eventName);
        if (event == null) {
            return false;
        }

        eventQueue.offer(normalizeEventKey(event.getName()));

        if (announcementManager != null) {
            announcementManager.onQueueUpdated();
        }

        return true;
    }

    public boolean hasQueuedEvents() {
        return !eventQueue.isEmpty();
    }

    public List<String> getQueuedEventDisplayNames() {
        List<String> queueEntries = new ArrayList<>();

        for (String eventKey : eventQueue) {
            PulseEvent event = eventsByKey.get(eventKey);
            queueEntries.add(event == null ? eventKey : getDisplayName(event));
        }

        return queueEntries;
    }

    public boolean removeQueuedEvent(int index) {
        if (index < 0 || index >= eventQueue.size()) {
            return false;
        }

        List<String> snapshot = new ArrayList<>(eventQueue);
        snapshot.remove(index);
        eventQueue.clear();
        eventQueue.addAll(snapshot);

        if (announcementManager != null) {
            announcementManager.onQueueUpdated();
        }

        return true;
    }

    public void clearQueue() {
        eventQueue.clear();

        if (announcementManager != null) {
            announcementManager.refreshSchedules();
        }
    }

    public PulseEvent findEvent(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        return eventsByKey.get(normalizeEventKey(input));
    }

    public boolean tryStartNextQueuedEvent() {
        if (!eventsSystemEnabled || current != null) {
            return false;
        }

        while (!eventQueue.isEmpty()) {
            String nextKey = eventQueue.peek();
            PulseEvent nextEvent = eventsByKey.get(nextKey);

            if (nextEvent == null) {
                eventQueue.poll();
                plugin.getLogger().warning("Removed unknown event from queue: " + nextKey);
                continue;
            }

            if (Bukkit.getOnlinePlayers().isEmpty()) {
                plugin.getLogger().fine("Queue is waiting because no players are online.");
                return false;
            }

            eventQueue.poll();
            return startEvent(nextEvent);
        }

        return false;
    }

    public boolean stopCurrent() {
        if (current == null) {
            return false;
        }

        cancelStopTask();

        PulseEvent eventToStop = current;
        String eventName = getDisplayName(eventToStop);

        try {
            eventToStop.stop();
        } catch (Exception exception) {
            plugin.getLogger().severe("Failed to stop event " + eventToStop.getClass().getSimpleName() + ": " + exception.getMessage());
        }

        liveUIManager.stop();
        current = null;

        Bukkit.broadcastMessage(lang.getWithPrefix("event.end", "%event%", eventName));

        if (announcementManager != null) {
            announcementManager.onEventStopped();
        }

        return true;
    }

    public boolean hasRegisteredEvents() {
        return !events.isEmpty();
    }

    public List<String> getRegisteredEventDisplayNames() {
        List<String> names = new ArrayList<>();

        for (PulseEvent event : events) {
            names.add(getDisplayName(event));
        }

        return names;
    }

    public List<String> getRegisteredEventInputNames() {
        List<String> names = new ArrayList<>();

        for (PulseEvent event : events) {
            names.add(event.getName());
        }

        return names;
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

    private boolean startEvent(PulseEvent event) {
        if (!eventsSystemEnabled || event == null || current != null) {
            return false;
        }

        current = event;
        cancelStopTask();

        try {
            current.start();
        } catch (Exception exception) {
            plugin.getLogger().severe("Failed to start event " + event.getClass().getSimpleName() + ": " + exception.getMessage());
            current = null;

            if (announcementManager != null) {
                announcementManager.refreshSchedules();
            }

            return false;
        }

        String eventName = getDisplayName(event);
        Bukkit.broadcastMessage(lang.getWithPrefix("event.start", "%event%", eventName));
        liveUIManager.start(eventName, Math.max(0, current.getDuration()));

        if (announcementManager != null) {
            announcementManager.onEventStarted();
        }

        int duration = Math.max(0, current.getDuration());
        stopTask = Bukkit.getScheduler().runTaskLater(
                plugin,
                this::stopCurrent,
                Math.max(1L, duration * 20L)
        );

        return true;
    }

    private void cancelStopTask() {
        if (stopTask != null) {
            stopTask.cancel();
            stopTask = null;
        }
    }

    private String normalizeEventKey(String input) {
        return input.toLowerCase(Locale.ROOT).replaceAll("[\\s_-]+", "");
    }
}
