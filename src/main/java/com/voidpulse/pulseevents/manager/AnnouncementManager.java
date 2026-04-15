package com.voidpulse.pulseevents.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AnnouncementManager {

    private final JavaPlugin plugin;
    private final EventManager eventManager;
    private final LanguageManager lang;
    private final Random random = new Random();
    private final List<BukkitTask> announcementTasks = new ArrayList<>();
    private BukkitTask nextRandomEventTask;
    private BukkitTask queueRetryTask;

    public AnnouncementManager(JavaPlugin plugin, EventManager eventManager, LanguageManager lang) {
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.lang = lang;
    }

    public void start() {
        refreshSchedules();
    }

    public void stop() {
        cancelAnnouncements();
        cancelNextRandomEventTask();
        cancelQueueRetryTask();
    }

    public void refreshSchedules() {
        stop();

        if (!eventManager.isEventsSystemEnabled() || eventManager.isEventRunning()) {
            return;
        }

        if (eventManager.hasQueuedEvents()) {
            if (!eventManager.tryStartNextQueuedEvent()) {
                scheduleQueueRetry();
            }
            return;
        }

        scheduleNextAutomaticEvent();
    }

    public void onEventStarted() {
        cancelAnnouncements();
        cancelNextRandomEventTask();
        cancelQueueRetryTask();
    }

    public void onEventStopped() {
        cancelQueueRetryTask();

        if (!eventManager.isEventsSystemEnabled()) {
            stop();
            return;
        }

        if (eventManager.hasQueuedEvents()) {
            if (!eventManager.tryStartNextQueuedEvent()) {
                scheduleQueueRetry();
            }
            return;
        }

        scheduleNextAutomaticEvent();
    }

    public void onQueueUpdated() {
        cancelAnnouncements();
        cancelNextRandomEventTask();

        if (!eventManager.isEventsSystemEnabled() || eventManager.isEventRunning()) {
            return;
        }

        cancelQueueRetryTask();

        if (!eventManager.tryStartNextQueuedEvent()) {
            scheduleQueueRetry();
        }
    }

    public void scheduleNextAutomaticEvent() {
        cancelAnnouncements();
        cancelNextRandomEventTask();

        if (!eventManager.isEventsSystemEnabled() || eventManager.isEventRunning() || eventManager.hasQueuedEvents()) {
            return;
        }

        int delaySeconds = resolveRandomDelaySeconds();
        scheduleAnnouncements(delaySeconds);

        nextRandomEventTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            nextRandomEventTask = null;

            if (eventManager.isEventRunning()) {
                return;
            }

            if (eventManager.hasQueuedEvents()) {
                onQueueUpdated();
                return;
            }

            if (!eventManager.startRandomEvent()) {
                scheduleNextAutomaticEvent();
            }
        }, delaySeconds * 20L);
    }

    public void scheduleQueueRetry() {
        cancelQueueRetryTask();

        if (!eventManager.isEventsSystemEnabled() || eventManager.isEventRunning() || !eventManager.hasQueuedEvents()) {
            return;
        }

        int retrySeconds = Math.max(5, plugin.getConfig().getInt("queue.retry-delay-seconds", 15));
        queueRetryTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            queueRetryTask = null;

            if (eventManager.isEventRunning() || !eventManager.hasQueuedEvents()) {
                return;
            }

            if (!eventManager.tryStartNextQueuedEvent()) {
                scheduleQueueRetry();
            }
        }, retrySeconds * 20L);
    }

    private int resolveRandomDelaySeconds() {
        int minSeconds = Math.max(0, plugin.getConfig().getInt("events.min-interval"));
        int maxSeconds = Math.max(minSeconds, plugin.getConfig().getInt("events.max-interval", minSeconds));
        return maxSeconds == minSeconds
                ? minSeconds
                : minSeconds + random.nextInt(maxSeconds - minSeconds + 1);
    }

    private void scheduleAnnouncements(int delaySeconds) {
        List<Integer> times = plugin.getConfig().getIntegerList("announcements.times");
        if (times.isEmpty()) {
            return;
        }

        times.sort(Comparator.reverseOrder());

        for (int minutes : times) {
            int announcementDelaySeconds = delaySeconds - (minutes * 60);
            if (minutes <= 0 || announcementDelaySeconds < 0) {
                continue;
            }

            BukkitTask task = Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> Bukkit.broadcastMessage(
                            lang.getWithPrefix("announcement.starting-in", "%time%", String.valueOf(minutes))
                    ),
                    announcementDelaySeconds * 20L
            );

            announcementTasks.add(task);
        }
    }

    private void cancelAnnouncements() {
        for (BukkitTask task : announcementTasks) {
            task.cancel();
        }
        announcementTasks.clear();
    }

    private void cancelNextRandomEventTask() {
        if (nextRandomEventTask != null) {
            nextRandomEventTask.cancel();
            nextRandomEventTask = null;
        }
    }

    private void cancelQueueRetryTask() {
        if (queueRetryTask != null) {
            queueRetryTask.cancel();
            queueRetryTask = null;
        }
    }
}
