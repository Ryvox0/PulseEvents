package com.voidpulse.pulseevents.listener;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.manager.EventManager;
import com.voidpulse.pulseevents.manager.LiveUIManager;
import com.voidpulse.pulseevents.update.UpdateChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PulseEvents plugin;
    private final UpdateChecker updateChecker;
    private final EventManager eventManager;
    private final LiveUIManager liveUIManager;

    public JoinListener(
            PulseEvents plugin,
            UpdateChecker updateChecker,
            EventManager eventManager,
            LiveUIManager liveUIManager
    ) {
        this.plugin = plugin;
        this.updateChecker = updateChecker;
        this.eventManager = eventManager;
        this.liveUIManager = liveUIManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (eventManager.isEventRunning()) {
            liveUIManager.addPlayer(event.getPlayer());
        }

        if (!plugin.getConfig().getBoolean("update-check.notify-on-join", true)) {
            return;
        }

        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("pulseevents.admin")) {
            updateChecker.sendUpdateMessage(event.getPlayer());
        }
    }
}
