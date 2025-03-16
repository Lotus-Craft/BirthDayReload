package org.referix.birthDayReload.utils.configmannagers;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class MessageManager {
    private final ConfigUtils configUtils;
    private final Plugin plugin;

    // Повідомлення
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
    // LuckPerms
    public boolean LUCK_PERM_ENABLED;
    public String LUCK_PERM_GROUP;
    public String LUCK_PERM_TIME;

    // Формат дати
    private String dateFormat;
    private DateTimeFormatter dateFormatter;

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

        // luckperms
        LUCK_PERM_ENABLED = configUtils.getBoolean("birthday-luckPerm.enable", false);
        LUCK_PERM_GROUP = configUtils.getString("birthday-luckPerm.group", "");
        LUCK_PERM_TIME = configUtils.getString("birthday-luckPerm.time", "1d");
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
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendPattern(dateFormat);
            // Якщо у шаблоні не задано рік, встановлюємо значення за замовчуванням 2000
            if (!dateFormat.toLowerCase().contains("y")) {
                builder.parseDefaulting(ChronoField.YEAR, 2000);
            }
            dateFormatter = builder.toFormatter();
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

    public LocalDate parseDate(String date) {
        // Нормалізація дати: видалення зайвих пробілів та заміна роздільників на дефіс
        String normalizedDate = normalizeDate(date);
        try {
            return LocalDate.parse(normalizedDate, dateFormatter);
        } catch (DateTimeParseException e) {
            plugin.getLogger().warning("Unable to parse date: " + date + " with format: " + dateFormat);
            throw new IllegalArgumentException("Supported date format is: " + dateFormat);
        }
    }

    /**
     * Нормалізація дати:
     * - Видаляємо зайві пробіли.
     * - Визначаємо очікуваний роздільник за конфігурацією (наприклад, '-' або '.').
     * - Замінюємо усі послідовності нецифрових символів на очікуваний роздільник.
     */
    private String normalizeDate(String date) {
        date = date.trim();
        char expectedDelimiter = '-';
        // Шукаємо перший символ, що не є буквою чи цифрою у dateFormat як роздільник
        for (int i = 0; i < dateFormat.length(); i++) {
            char ch = dateFormat.charAt(i);
            if (!Character.isLetterOrDigit(ch)) {
                expectedDelimiter = ch;
                break;
            }
        }
        // Замінюємо будь-які послідовності нецифрових символів на очікуваний роздільник
        return date.replaceAll("\\D+", String.valueOf(expectedDelimiter));
    }

    public String formatDate(LocalDate date) {
        return date.format(dateFormatter);
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
