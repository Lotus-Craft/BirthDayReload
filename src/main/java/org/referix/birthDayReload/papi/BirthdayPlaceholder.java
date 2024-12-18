package org.referix.birthDayReload.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.referix.birthDayReload.playerdata.PlayerData;
import org.referix.birthDayReload.playerdata.PlayerManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BirthdayPlaceholder extends PlaceholderExpansion {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public @NotNull String getIdentifier() {
        return "birthday"; // Префікс плейсхолдера
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
        return true; // Не реєструвати плейсхолдер після перезавантаження
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        PlayerData data = PlayerManager.getInstance().getPlayerData(player);

        // Плейсхолдери
        switch (identifier) {
            case "date": // %birthday_date%
                LocalDate birthday = data.getBirthday();
                return (birthday != null) ? birthday.format(DATE_FORMAT) : "Not set";

            case "is_today": // %birthday_is_today%
                LocalDate today = LocalDate.now();
                if (data.getBirthday() != null && data.getBirthday().getMonth() == today.getMonth() &&
                        data.getBirthday().getDayOfMonth() == today.getDayOfMonth()) {
                    return "Yes";
                }
                return "No";

            case "wished": // %birthday_wished%
                return data.isWished() ? "Yes" : "No";

            case "prefix": {
                if (data.getPrefix() != null) {
                    return data.getPrefix();
                }
                return "";
            } // %birthday_prefix%

            default:
                return null;
        }
    }
}
