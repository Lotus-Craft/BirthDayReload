package org.referix.birthDayReload.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigUtils {
    private final Plugin plugin;
    private FileConfiguration config;
    private final File configFile;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ConfigUtils(Plugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        loadConfig();
    }

    public void loadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public Component getComponent(String path, String defaultValue) {
        String value = config.getString(path, defaultValue);
        return parseMiniMessage(value);
    }

    public Component getComponent(String path) {
        return getComponent(path, "");
    }

    public void reloadConfig() {
        try {
            if (configFile.exists()) {
                File backupFile = new File(plugin.getDataFolder(), "config_backup.yml");
                Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            loadConfig();
            plugin.getLogger().info("Config reloaded successfully!");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to reload config: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    private Component parseMiniMessage(String input) {
        if (input == null) return Component.empty();
        return miniMessage.deserialize(input);
    }
}
