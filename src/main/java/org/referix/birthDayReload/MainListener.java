package org.referix.birthDayReload;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.referix.birthDayReload.playerdata.PlayerData;
import org.referix.birthDayReload.playerdata.PlayerManager;

import java.time.LocalDate;

import static org.referix.birthDayReload.utils.LoggerUtils.log;

public class MainListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerManager manager = PlayerManager.getInstance();

        // Завантаження даних гравця
        PlayerData data;
        if (!manager.isPlayerDataCached(player)) {
            data = manager.getPlayerData(player);
            log("Player data loaded from database for: " + player.getName());
        } else {
            data = manager.getPlayerData(player);
            log("Player data loaded from cache for: " + player.getName());
        }

        // Перевірка на день народження
        if (data.getBirthday() != null) {
            LocalDate today = LocalDate.now();
            LocalDate birthday = data.getBirthday();

            if (birthday.getDayOfMonth() == today.getDayOfMonth() && birthday.getMonth() == today.getMonth()) {
                if (!data.isWished()) {
                    player.sendMessage("§6§lHappy Birthday, " + player.getName() + "! 🎉");
                    player.sendMessage("§aMay your day be filled with joy and celebration!");

                    data.setIsWished(true); // Встановлюємо прапорець
                    log("Birthday prefix and wish set for: " + player.getName());
                } else {
                    player.sendMessage("§eWelcome back and Happy Birthday once again! 🎂");
                    manager.savePlayerData(player); // Зберігаємо дані
                }
                data.setPrefix(BirthDayReload.getInstance().getMessageManager().BIRTHDAY_BOY_PREFIX); // Встановлюємо префікс
            } else {
                // Скидання isWished, якщо сьогодні не день народження
                if (data.isWished()) {
                    data.setIsWished(false);
                    data.setPrefix(null); // Встановлюємо префікс
                    manager.savePlayerData(player);
                    log("Birthday flag reset for: " + player.getName());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager.getInstance().savePlayerData(player);
        log("Player data saved for: " + player.getName());
    }
}
