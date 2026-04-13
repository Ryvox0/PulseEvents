package com.voidpulse.pulseevents;

import com.voidpulse.pulseevents.commands.PECommand;
import com.voidpulse.pulseevents.events.BlackHoleEvent;
import com.voidpulse.pulseevents.events.CoinRainEvent;
import com.voidpulse.pulseevents.events.FireFeetEvent;
import com.voidpulse.pulseevents.events.FreezeEvent;
import com.voidpulse.pulseevents.events.LightningStormEvent;
import com.voidpulse.pulseevents.events.LowGravityEvent;
import com.voidpulse.pulseevents.events.MobSwarmEvent;
import com.voidpulse.pulseevents.events.RandomEffectsEvent;
import com.voidpulse.pulseevents.events.RandomTeleportEvent;
import com.voidpulse.pulseevents.events.ReverseControlsEvent;
import com.voidpulse.pulseevents.events.SpinEvent;
import com.voidpulse.pulseevents.events.TNTRainEvent;
import com.voidpulse.pulseevents.events.TargetPlayerEvent;
import com.voidpulse.pulseevents.listener.CoinRainListener;
import com.voidpulse.pulseevents.listener.JoinListener;
import com.voidpulse.pulseevents.listener.MilkBlockListener;
import com.voidpulse.pulseevents.manager.EconomyManager;
import com.voidpulse.pulseevents.manager.EventManager;
import com.voidpulse.pulseevents.manager.LanguageManager;
import com.voidpulse.pulseevents.manager.LiveUIManager;
import com.voidpulse.pulseevents.manager.WorldCheck;
import com.voidpulse.pulseevents.task.EventTask;
import com.voidpulse.pulseevents.update.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public final class PulseEvents extends JavaPlugin {

    private EventManager eventManager;
    private LiveUIManager liveUIManager;
    private LanguageManager lang;
    private WorldCheck worldCheck;
    private UpdateChecker updateChecker;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        initManagers();
        registerEvents();
        registerCommands();
        registerGameEvents();
        startSystems();

        getLogger().info("PulseEvents enabled.");
    }

    @Override
    public void onDisable() {
        if (eventManager != null) {
            eventManager.stopCurrent();
        }

        if (liveUIManager != null) {
            liveUIManager.stop();
        }
    }

    private void initManagers() {
        lang = new LanguageManager(this);
        liveUIManager = new LiveUIManager(this, lang);
        eventManager = new EventManager(this, liveUIManager, lang);
        worldCheck = new WorldCheck(this);
        updateChecker = new UpdateChecker(this, lang, "Ryvox0/PulseEvents");
        economyManager = new EconomyManager(this);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(
                new MilkBlockListener(eventManager, worldCheck, lang),
                this
        );

        getServer().getPluginManager().registerEvents(
                new JoinListener(this, updateChecker, eventManager, liveUIManager),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CoinRainListener(economyManager, lang),
                this
        );
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("pe")).setExecutor(new PECommand(this));
    }

    private void registerGameEvents() {
        eventManager.registerEvent(new LowGravityEvent(this));

        if (economyManager.isAvailable()) {
            eventManager.registerEvent(new CoinRainEvent(this));
        } else {
            getLogger().warning("Coin Rain event was not registered because Vault Economy is unavailable.");
        }

        eventManager.registerEvent(new LightningStormEvent(this));
        eventManager.registerEvent(new TNTRainEvent(this));
        eventManager.registerEvent(new MobSwarmEvent(this));
        eventManager.registerEvent(new RandomTeleportEvent(this));
        eventManager.registerEvent(new FireFeetEvent(this));
        eventManager.registerEvent(new FreezeEvent(this));
        eventManager.registerEvent(new ReverseControlsEvent(this));
        eventManager.registerEvent(new BlackHoleEvent(this));
        eventManager.registerEvent(new RandomEffectsEvent(this));
        eventManager.registerEvent(new TargetPlayerEvent(this));
        eventManager.registerEvent(new SpinEvent(this));
    }

    private void startSystems() {
        EventTask.start(this, eventManager, lang);

        if (getConfig().getBoolean("update-check.enabled", true)
                && getConfig().getBoolean("update-check.check-on-startup", true)) {
            updateChecker.check();
        }
    }

    public void reloadPlugin() {
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        lang.load();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public LiveUIManager getLiveUIManager() {
        return liveUIManager;
    }

    public LanguageManager getLang() {
        return lang;
    }

    public WorldCheck getWorldCheck() {
        return worldCheck;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
