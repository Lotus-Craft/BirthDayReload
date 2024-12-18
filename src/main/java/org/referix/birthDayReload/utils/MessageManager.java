package org.referix.birthDayReload.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;

public class MessageManager {
    private final ConfigUtils configUtils;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Plugin plugin;

    public final Component BIRTHDAY_BOY_PREFIX;
    public final Component USER_NO_ENTER_DATA;
    public final Component BIRTHDAY_SET_SUCCESS;
    public final Component BIRTHDAY_SET_FUTURE_ERROR;
    public final Component BIRTHDAY_SET_FORMAT_ERROR;
    public final Component BIRTHDAY_DELETE_NO_PERMISSION;
    public final Component BIRTHDAY_DELETE_USAGE;
    public final Component BIRTHDAY_DELETE_SUCCESS;
    public final Component BIRTHDAY_DELETE_PLAYER_NOT_FOUND;
    public final Component BIRTHDAY_UNKNOWN_COMMAND;
    public final Component BIRTHDAY_ONLY_PLAYERS;

    public MessageManager(Plugin plugin) {
        this.plugin = plugin;
        this.configUtils = new ConfigUtils(plugin);

        // Загружаем все сообщения из конфига
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

    public void reloadMessages() {
        configUtils.reloadConfig();
        plugin.getLogger().info("Messages reloaded successfully.");
    }
}
