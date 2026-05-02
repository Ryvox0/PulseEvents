package com.voidpulse.pulseevents.update;

import com.voidpulse.pulseevents.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("deprecation")
public class UpdateChecker {
    private static final String DOWNLOAD_URL = "https://modrinth.com/plugin/pulseevents";

    private final JavaPlugin plugin;
    private final LanguageManager lang;
    private final String repo;
    private volatile String latestVersion;

    public UpdateChecker(JavaPlugin plugin, LanguageManager lang, String repo) {
        this.plugin = plugin;
        this.lang = lang;
        this.repo = repo;
    }

    public void check() {
        check(null);
    }

    public void check(CommandSender requester) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String fetchedVersion = fetchLatestVersion();
                if (fetchedVersion == null || fetchedVersion.isBlank()) {
                    handleFailure(requester);
                    return;
                }

                latestVersion = fetchedVersion;
                Bukkit.getScheduler().runTask(plugin, () -> handleSuccess(requester));
            } catch (Exception exception) {
                handleFailure(requester);
            }
        });
    }

    public void sendUpdateMessage(CommandSender sender) {
        if (!isUpdateAvailable()) {
            return;
        }

        sender.sendMessage(lang.getWithPrefix(
                "update.available.line-1",
                "%latest%",
                latestVersion,
                "%current%",
                plugin.getDescription().getVersion()
        ));
        sender.sendMessage(lang.getWithPrefix(
                "update.available.line-2",
                "%url%",
                getReleaseUrl()
        ));
    }

    public boolean isUpdateAvailable() {
        return latestVersion != null
                && !normalizeVersion(plugin.getDescription().getVersion()).equalsIgnoreCase(normalizeVersion(latestVersion));
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    private void handleSuccess(CommandSender requester) {
        if (isUpdateAvailable()) {
            if (requester != null) {
                sendUpdateMessage(requester);
            } else {
                notifyOnlineAdmins();
            }

            plugin.getLogger().info("New update available: " + latestVersion + " (" + getReleaseUrl() + ")");
            return;
        }

        if (requester != null) {
            requester.sendMessage(lang.getWithPrefix(
                    "update.up-to-date",
                    "%current%",
                    plugin.getDescription().getVersion()
            ));
        }
    }

    private void handleFailure(CommandSender requester) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (requester != null) {
                requester.sendMessage(lang.getWithPrefix("update.check-failed"));
            }

            plugin.getLogger().warning(lang.get("update.console-check-failed"));
        });
    }

    private void notifyOnlineAdmins() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission("pulseevents.admin")) {
                sendUpdateMessage(player);
            }
        }
    }

    private String fetchLatestVersion() throws Exception {
        URL url = new URL("https://api.github.com/repos/" + repo + "/releases/latest");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            String data = json.toString();
            int index = data.indexOf("\"tag_name\":\"");
            if (index == -1) {
                return null;
            }

            int start = index + 12;
            int end = data.indexOf("\"", start);
            return data.substring(start, end);
        }
    }

    private String normalizeVersion(String version) {
        return version == null ? "" : version.replaceFirst("^v", "");
    }

    private String getReleaseUrl() {
        return DOWNLOAD_URL;
    }
}
