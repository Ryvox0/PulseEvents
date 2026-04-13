package com.voidpulse.pulseevents.commands;

import com.voidpulse.pulseevents.PulseEvents;
import com.voidpulse.pulseevents.manager.EventManager;
import com.voidpulse.pulseevents.manager.LanguageManager;
import com.voidpulse.pulseevents.update.UpdateChecker;

public abstract class BaseSubCommand implements PESubCommand {

    protected final PulseEvents plugin;
    protected final LanguageManager lang;
    protected final EventManager eventManager;
    protected final UpdateChecker updateChecker;

    protected BaseSubCommand(PulseEvents plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.eventManager = plugin.getEventManager();
        this.updateChecker = plugin.getUpdateChecker();
    }
}
