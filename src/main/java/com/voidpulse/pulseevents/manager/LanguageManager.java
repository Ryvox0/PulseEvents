package com.voidpulse.pulseevents.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class LanguageManager {

    private final JavaPlugin plugin;
    private FileConfiguration lang;

    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        saveLanguageFile("en");
        saveLanguageFile("pl");

        String langName = plugin.getConfig().getString(
                "settings.language",
                plugin.getConfig().getString("language", "en")
        );

        File file = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("Language file '" + langName + ".yml' not found. Falling back to en.yml.");
            file = new File(plugin.getDataFolder(), "lang/en.yml");
            langName = "en";
        }

        lang = YamlConfiguration.loadConfiguration(file);

        if (plugin.getResource("lang/" + langName + ".yml") != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(plugin.getResource("lang/" + langName + ".yml"), StandardCharsets.UTF_8)
            );
            lang.setDefaults(defaults);
            lang.options().copyDefaults(true);

            try {
                lang.save(file);
            } catch (IOException exception) {
                plugin.getLogger().warning("Could not update language file '" + file.getName() + "'.");
            }
        }
    }

    public String get(String key, String... replacements) {
        return colorize(applyPlaceholders(getRaw(key, "&cMissing message: " + key), replacements));
    }

    public String getOrDefault(String key, String defaultValue, String... replacements) {
        return colorize(applyPlaceholders(getRaw(key, defaultValue), replacements));
    }

    public String getWithPrefix(String key, String... replacements) {
        String prefix = getRaw("prefix", "");
        String message = getRaw(key, "&cMissing message: " + key);
        return colorize(applyPlaceholders(prefix + message, replacements));
    }

    private String getRaw(String key, String defaultValue) {
        return lang.getString(key, defaultValue);
    }

    private String applyPlaceholders(String input, String... replacements) {
        String result = input;

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            result = result.replace(replacements[i], replacements[i + 1]);
        }

        return result;
    }

    private String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private void saveLanguageFile(String languageCode) {
        File file = new File(plugin.getDataFolder(), "lang/" + languageCode + ".yml");
        if (!file.exists()) {
            plugin.saveResource("lang/" + languageCode + ".yml", false);
        }
    }
}
