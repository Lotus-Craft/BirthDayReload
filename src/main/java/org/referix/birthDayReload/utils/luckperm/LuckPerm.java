package org.referix.birthDayReload.utils.luckperm;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.referix.birthDayReload.BirthDayReload;
import org.referix.birthDayReload.utils.MessageManager;

import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuckPerm {

    private final LuckPerms luckPerms;
    private final Plugin plugin = BirthDayReload.getInstance();
    private final MessageManager messageManager;

    public LuckPerm(LuckPerms luckPerms, MessageManager messageManager) {
        this.luckPerms = luckPerms;
        this.messageManager = messageManager;
    }

    /**
     * Публичный метод: добавляет игрока в группу, указанную в конфиге (LUCK_PERM_GROUP),
     * с истечением срока, если (LUCK_PERM_TIME) задан. Поддерживаются d/h/m/s/w/mo/y.
     */
    public void applyLuckPermGroup(Player player) {
        if (luckPerms == null) {
            plugin.getLogger().severe("[LuckPerms] LuckPerms API no connected");
            return;
        }
        if (!messageManager.LUCK_PERM_ENABLED) {
            return;
        } else {
            String group = messageManager.LUCK_PERM_GROUP;
            String time = messageManager.LUCK_PERM_TIME;

            if (group == null || group.isEmpty()) {
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                addGroupWithConfigTime(player, group, time);
            });
        }

    }

    /**
     * Приватный метод, который действительно вносит игрока в группу
     * с использованием Duration (если распарсился) или навсегда (если парсинг = null).
     */
    private void addGroupWithConfigTime(Player player, String groupName, String timeString) {
        User user = getUser(player.getUniqueId());
        if (user == null) {
            return;
        }

        try {
            Duration duration = parseDuration(timeString);

            InheritanceNode node;
            if (duration == null || duration.isZero() || duration.isNegative()) {
                node = InheritanceNode.builder(groupName).build();
            } else {
                node = InheritanceNode.builder(groupName)
                        .expiry(duration)
                        .build();
            }

            if (!user.data().add(node).wasSuccessful()) {
                return;
            }

            luckPerms.getUserManager().saveUser(user).join();
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Парсим строку в Duration.
     * Поддерживаем форматы: d/h/m/s/w/mo/y (дни, часы, минуты, секунды, недели, месяцы, годы).
     * Можно комбинировать: "1d2h30m", "2w1d", "3mo" и т.д.
     * Возвращаем null, если не нашли ни одного валидного фрагмента.
     */
    private Duration parseDuration(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile("(\\d+)(d|h|m|s|w|mo|y)");
        Matcher matcher = pattern.matcher(input);

        Duration total = Duration.ZERO;
        boolean foundAny = false;

        while (matcher.find()) {
            foundAny = true;
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "d":
                    total = total.plusDays(value);
                    break;
                case "h":
                    total = total.plusHours(value);
                    break;
                case "m":
                    total = total.plusMinutes(value);
                    break;
                case "s":
                    total = total.plusSeconds(value);
                    break;
                case "w":
                    total = total.plusDays(value * 7L);
                    break;
                case "mo":
                    total = total.plusDays(value * 30L);
                    break;
                case "y":
                    total = total.plusDays(value * 365L);
                    break;
                default:
                    return null;
            }
        }

        if (!foundAny) {
            return null;
        }
        return total;
    }

    /**
     * Приватный метод: Загружаем LuckPerms-пользователя по UUID
     */
    private User getUser(UUID uuid) {
        try {
            return luckPerms.getUserManager().loadUser(uuid).join();
        } catch (Exception e) {
            return null;
        }
    }
}
