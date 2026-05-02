# Changelog

## Unreleased

### Added

- PlaceholderAPI soft dependency in plugin metadata and Maven.
- Optional `pulseevents` PlaceholderAPI expansion. The plugin still loads normally without PlaceholderAPI installed.
- New placeholders:
  - `%pulseevents_current_event%`
  - `%pulseevents_event_active%`
  - `%pulseevents_events_enabled%`
  - `%pulseevents_queue_size%`
  - `%pulseevents_registered_events%`
- Event chance editor GUI opened with `/pe events` for players.
- Faster queue management from the event GUI with instant event queueing.
- New GUI translation entries in both `lang/en.yml` and `lang/pl.yml` for:
  - menu title
  - event item name
  - current chance display
  - left/right click controls
  - shift click controls
  - update feedback message
- Per-event `chance` config entries in `config.yml` for all registered events.

### Changed

- `/pe events` now opens the event chance menu for players instead of only printing the event list.
- Event chances can now be adjusted directly in-game:
  - left click: `+5`
  - right click: `-5`
  - shift + left click: `+20`
  - shift + right click: `-20`
- Events can now be added to the queue directly from the GUI with middle click.
- The GUI now shows queued copies per event and a queue summary item.
- Black Hole event now renders particle effects around the pull center for better visibility.
- Fire Feet now places fire behind the player based on movement direction instead of only around the feet.

### Config and Language

- `config.yml` now includes chance values for:
  - `coin-rain`
  - `lightning-storm`
  - `tnt-rain`
  - `mob-swarm`
  - `random-teleport`
  - `fire-feet`
  - `freeze`
  - `black-hole`
  - `random-effects`
  - `target-player`
  - `spin`
- `lang/en.yml` and `lang/pl.yml` now include the strings required for the event GUI and chance update feedback.
