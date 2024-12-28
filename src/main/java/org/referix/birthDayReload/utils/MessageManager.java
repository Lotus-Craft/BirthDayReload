package org.referix.birthDayReload.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MessageManager {
    private final ConfigUtils configUtils;
    private final Plugin plugin;

    // Messages
    public Component BIRTHDAY_BOY_PREFIX;
    public Component USER_NO_ENTER_DATA;
    public Component BIRTHDAY_SET_SUCCESS;
    public Component BIRTHDAY_SET_FUTURE_ERROR;
    public Component BIRTHDAY_SET_FORMAT_ERROR;
    public Component BIRTHDAY_DELETE_NO_PERMISSION;
    public Component BIRTHDAY_DELETE_USAGE;
    public Component BIRTHDAY_DELETE_SUCCESS;
    public Component BIRTHDAY_DELETE_PLAYER_NOT_FOUND;
    public Component BIRTHDAY_UNKNOWN_COMMAND;
    public Component BIRTHDAY_ONLY_PLAYERS;
    public Component BIRTHDAY_ALREADY_SET;
    public Component BIRTHDAY_LUCKPERMS_MESSAGE;
    //LuckPerm
    public boolean LUCK_PERM_ENABLED;
    public String LUCK_PERM_GROUP;
    public String LUCK_PERM_TIME;


    // Date format
    private String dateFormat;
    private DateTimeFormatter dateFormatter;

    // Discord
    public boolean DISCORD_ENABLED;
    public String DISCORD_TOKEN;
    public String DISCORD_CHANNEL_ID;

    public MessageManager(Plugin plugin) {
        this.plugin = plugin;
        this.configUtils = new ConfigUtils(plugin);
        loadMessages();
    }

    private void loadMessages() {
        BIRTHDAY_BOY_PREFIX = logComponentLoad("Birthday-boy-prefix");
        USER_NO_ENTER_DATA = logComponentLoad("Messages.user-no-enter-data");
        BIRTHDAY_SET_SUCCESS = logComponentLoad("Messages.birthday-set-success");
        BIRTHDAY_SET_FUTURE_ERROR = logComponentLoad("Messages.birthday-set-future-error");
        BIRTHDAY_SET_FORMAT_ERROR = logComponentLoad("Messages.birthday-set-format-error");
        BIRTHDAY_DELETE_NO_PERMISSION = logComponentLoad("Messages.birthday-delete-no-permission");
        BIRTHDAY_DELETE_USAGE = logComponentLoad("Messages.birthday-delete-usage");
        BIRTHDAY_DELETE_SUCCESS = logComponentLoad("Messages.birthday-delete-success");
        BIRTHDAY_DELETE_PLAYER_NOT_FOUND = logComponentLoad("Messages.birthday-delete-player-not-found");
        BIRTHDAY_UNKNOWN_COMMAND = logComponentLoad("Messages.birthday-unknown-command");
        BIRTHDAY_ONLY_PLAYERS = logComponentLoad("Messages.birthday-only-players");
        BIRTHDAY_ALREADY_SET = logComponentLoad("Messages.birthday-already-set");

        BIRTHDAY_LUCKPERMS_MESSAGE = logComponentLoad("Messages.birthday-luckperm-message");

        // Зчитування формату дати з конфігурації
        dateFormat = configUtils.getString("Format-Data", "yyyy-MM-dd");
        updateDateFormatter();

        //luckperms
        LUCK_PERM_ENABLED = configUtils.getBoolean("birthday-luckPerm.enable", false);
        LUCK_PERM_GROUP = configUtils.getString("birthday-luckPerm.group", "");
        LUCK_PERM_TIME = configUtils.getString("birthday-luckPerm.time", "1d");

        DISCORD_ENABLED = configUtils.getBoolean("Discord.enabled", false);
        DISCORD_TOKEN = configUtils.getString("Discord.token", "");
        DISCORD_CHANNEL_ID = configUtils.getString("Discord.channel-id", "");
    }

    private Component logComponentLoad(String path) {
        Component component = configUtils.getComponent(path);
        if (component == null) {
            plugin.getLogger().warning("Message not found in config: " + path);
        } else {
            plugin.getLogger().info("Message loaded successfully: " + path);
        }
        return component;
    }

    private void updateDateFormatter() {
        try {
            dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
            plugin.getLogger().info("Date format updated to: " + dateFormat);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid date format in config: " + dateFormat + ". Defaulting to yyyy-MM-dd.");
            dateFormat = "yyyy-MM-dd";
            dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        }
    }

    public void reloadMessages() {
        configUtils.reloadConfig();
        loadMessages();
        plugin.getLogger().info("Messages reloaded successfully.");
    }

    public boolean isValidDate(String date) {
        try {
            parseDate(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public LocalDate parseDate(String date) {
        String normalizedDate = normalizeDate(date);

        try {
            // Перевіряємо, чи введена повна дата у форматі yyyy-MM-dd або yyyy.MM.dd
            if (normalizedDate.matches("\\d{4}[-.]\\d{2}[-.]\\d{2}")) {
                // Якщо формат yyyy.MM.dd, замінюємо "." на "-"
                normalizedDate = normalizedDate.replace(".", "-");
                DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(normalizedDate, fullDateFormatter);
            }

            // Якщо формат MM-dd або MM.dd, додаємо рік 2000
            if (normalizedDate.matches("\\d{2}[-.]\\d{2}")) {
                normalizedDate = "2000-" + normalizedDate.replace(".", "-");
                DateTimeFormatter partialDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(normalizedDate, partialDateFormatter);
            }

            // Якщо формат не підтримується
            throw new DateTimeParseException("Unsupported date format", date, 0);
        } catch (DateTimeParseException e) {
            plugin.getLogger().warning("Unable to parse date: " + date + " with formats: yyyy-MM-dd, yyyy.MM.dd, MM-dd, or MM.dd.");
            throw new IllegalArgumentException("Дата повинна бути у форматі yyyy-MM-dd, yyyy.MM.dd, MM-dd або MM.dd.");
        }
    }





    private String normalizeDate(String date) {
        // Видалення зайвих пробілів
        date = date.trim();

        // Заміна будь-яких роздільників на дефіс
        return date.replaceAll("[./,;\\s-]", "-");
    }




    public String formatDate(LocalDate date) {
        return date.format(dateFormatter);
    }

    public String getDateFormat() {
        return dateFormat;
    }
}

