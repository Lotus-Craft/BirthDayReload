package org.referix.birthDayReload.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.referix.birthDayReload.playerdata.PlayerData;
import org.referix.birthDayReload.playerdata.PlayerManager;
import org.referix.birthDayReload.utils.configmannagers.MessageManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BirthdayPlaceholder extends PlaceholderExpansion {

    private final DateTimeFormatter DATE_FORMAT;

    public BirthdayPlaceholder(MessageManager messageManager) {
        DATE_FORMAT = DateTimeFormatter.ofPattern(messageManager.getDateFormat());
    }

    @Override
    public @NotNull String getIdentifier() {
        return "birthday"; // Префикс плейсхолдера
    }

    @Override
    public @NotNull String getAuthor() {
        return "Referix";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true; // Плейсхолдер будет сохраняться между перезагрузками
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        PlayerData data = PlayerManager.getInstance().getPlayerData(player);
        if (data == null) {
            return "";
        }

        LocalDate today = LocalDate.now();

        return switch (identifier) {
            case "date" -> // %birthday_date%
                    (data.getBirthday() != null) ? data.getBirthday().format(DATE_FORMAT) : "Not set";
            case "is_today" -> // %birthday_is_today%
                    (data.getBirthday() != null &&
                            data.getBirthday().getMonth() == today.getMonth() &&
                            data.getBirthday().getDayOfMonth() == today.getDayOfMonth()) ? "Yes" : "No";
            case "wished" -> // %birthday_wished%
                    data.isWished() ? "Yes" : "No";
            case "prefix" -> {
                if (data.getPrefix() != null) {
                    Component prefixComponent = data.getPrefix();
                    // Сериализуем компонент в строку
                    String rawPrefix = LegacyComponentSerializer.legacyAmpersand().serialize(prefixComponent);
                    // Обрабатываем вложенные плейсхолдеры (если есть)
                    String resolvedPrefix = PlaceholderAPI.setPlaceholders(player, rawPrefix);
                    yield resolvedPrefix + "&r"; // Возвращаем обработанную строку
                }
                yield "";
            }

            default -> null;
        };
    }
}
