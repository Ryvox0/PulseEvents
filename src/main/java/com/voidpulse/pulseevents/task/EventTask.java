package com.voidpulse.pulseevents.task;

import com.voidpulse.pulseevents.manager.EventManager;
import com.voidpulse.pulseevents.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class EventTask {

    private static final Random RANDOM = new Random();

    public static void start(JavaPlugin plugin, EventManager manager, LanguageManager lang) {
        scheduleNext(plugin, manager, lang);
    }

    private static void scheduleNext(JavaPlugin plugin, EventManager manager, LanguageManager lang) {
        int minSeconds = plugin.getConfig().getInt("events.min-interval");
        int maxSeconds = plugin.getConfig().getInt("events.max-interval");

        if (maxSeconds < minSeconds) {
            maxSeconds = minSeconds;
        }

        int delaySeconds = maxSeconds == minSeconds
                ? minSeconds
                : minSeconds + RANDOM.nextInt(maxSeconds - minSeconds + 1);

        scheduleAnnouncements(plugin, lang, delaySeconds);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            manager.startRandomEvent();

            if (manager.getCurrentEvent() == null) {
                scheduleNext(plugin, manager, lang);
                return;
            }

            long nextDelayTicks = (manager.getCurrentEvent().getDuration() * 20L) + 1L;
            Bukkit.getScheduler().runTaskLater(plugin, () -> scheduleNext(plugin, manager, lang), nextDelayTicks);
        }, delaySeconds * 20L);
    }

    private static void scheduleAnnouncements(JavaPlugin plugin, LanguageManager lang, int delaySeconds) {
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

            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> Bukkit.broadcastMessage(
                            lang.getWithPrefix("announcement.starting-in", "%time%", String.valueOf(minutes))
                    ),
                    announcementDelaySeconds * 20L
            );
        }
    }
}
