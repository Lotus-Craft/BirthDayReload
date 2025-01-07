package org.referix.birthDayReload.utils.configmannagers;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class DiscordConfig {

    private final boolean enabled;
    private final String token;
    private final String channelId;

    private String setBirthdayTitle;
    private String setBirthdayColor;
    private List<String> setBirthdayMessage;

    private String happyBirthdayTitle;
    private String happyBirthdayColor;
    private List<String> happyBirthdayMessage;

    private String adminDeleteBirthdayTitle;
    private String adminDeleteBirthdayColor;
    private List<String> adminDeleteBirthdayMessage;

    public DiscordConfig(FileConfiguration config) {
        this.enabled = config.getBoolean("Discord.enabled");
        this.token = config.getString("Discord.token");
        this.channelId = config.getString("Discord.channel-id");

        ConfigurationSection embeddedMessages = config.getConfigurationSection("Discord.Embedded-messages");
        if (embeddedMessages != null) {
            loadSetBirthday(embeddedMessages.getConfigurationSection("set-birthday"));
            loadHappyBirthday(embeddedMessages.getConfigurationSection("happy-birthday"));
            loadAdminDeleteBirthday(embeddedMessages.getConfigurationSection("admin-delete-birthday"));
        }
    }

    private void loadSetBirthday(ConfigurationSection section) {
        if (section != null) {
            this.setBirthdayTitle = section.getString("title", "");
            this.setBirthdayColor = section.getString("color", "#FFFFFF");
            this.setBirthdayMessage = section.getStringList("message");
        }
    }

    private void loadHappyBirthday(ConfigurationSection section) {
        if (section != null) {
            this.happyBirthdayTitle = section.getString("title", "");
            this.happyBirthdayColor = section.getString("color", "#FFFFFF");
            this.happyBirthdayMessage = section.getStringList("message");
        }
    }

    private void loadAdminDeleteBirthday(ConfigurationSection section) {
        if (section != null) {
            this.adminDeleteBirthdayTitle = section.getString("title", "");
            this.adminDeleteBirthdayColor = section.getString("color", "#FFFFFF");
            this.adminDeleteBirthdayMessage = section.getStringList("message");
        }
    }

    // Геттери для основних параметрів
    public boolean isEnabled() {
        return enabled;
    }

    public String getToken() {
        return token;
    }

    public String getChannelId() {
        return channelId;
    }

    // Геттери для "set-birthday"
    public String getSetBirthdayTitle() {
        return setBirthdayTitle;
    }

    public String getSetBirthdayColor() {
        return setBirthdayColor;
    }

    public List<String> getSetBirthdayMessage() {
        return setBirthdayMessage;
    }

    // Геттери для "happy-birthday"
    public String getHappyBirthdayTitle() {
        return happyBirthdayTitle;
    }

    public String getHappyBirthdayColor() {
        return happyBirthdayColor;
    }

    public List<String> getHappyBirthdayMessage() {
        return happyBirthdayMessage;
    }

    // Геттери для "admin-delete-birthday"
    public String getAdminDeleteBirthdayTitle() {
        return adminDeleteBirthdayTitle;
    }

    public String getAdminDeleteBirthdayColor() {
        return adminDeleteBirthdayColor;
    }

    public List<String> getAdminDeleteBirthdayMessage() {
        return adminDeleteBirthdayMessage;
    }
}

