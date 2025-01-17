package org.referix.birthDayReload.utils.configmannagers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
        return getComponent(path, "<red>Message not found: " + path + "</red>");
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

    private Component parseMiniMessage(String input) {
        if (input == null) return Component.empty();
        return miniMessage.deserialize(input);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public List<String> getStringList(String path) { return config.getStringList(path);}
}
