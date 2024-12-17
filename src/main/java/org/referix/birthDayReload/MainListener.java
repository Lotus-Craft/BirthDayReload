package org.referix.birthDayReload;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.referix.birthDayReload.playerdata.PlayerManager;

import static org.referix.birthDayReload.utils.LoggerUtils.log;

public class MainListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerManager manager = PlayerManager.getInstance();

        // Перевірка, чи гравець вже є в кеші
        if (!manager.isPlayerDataCached(player)) {
            manager.getPlayerData(player); // Завантаження з бази
            log("Player data loaded from database for: " + player.getName());
        } else {
            log("Player data loaded from cache for: " + player.getName());
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Збереження даних гравця при виході
        PlayerManager.getInstance().savePlayerData(event.getPlayer());
        log("Player data saved for: " + event.getPlayer().getName());
    }
}
