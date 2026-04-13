package com.voidpulse.pulseevents.events;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ALL")
public class TargetPlayerEvent implements PulseEvent {

    private final JavaPlugin plugin;
    private Player target;

    public TargetPlayerEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Target Player";
    }

    @Override
    public void start() {
        List<Player> players = List.copyOf(Bukkit.getOnlinePlayers());
        LanguageManager lang = ((PulseEvents) plugin).getLang();

        if (players.isEmpty()) {
            Bukkit.broadcastMessage(lang.getWithPrefix("event.target.no-players"));
            return;
        }

        target = players.get(ThreadLocalRandom.current().nextInt(players.size()));
        Bukkit.broadcastMessage(
                lang.getWithPrefix("event.target.selected", "%player%", target.getName())
        );
    }

    @Override
    public void stop() {
        target = null;
    }

    @Override
    public int getDuration() {
        return 40;
    }
}
