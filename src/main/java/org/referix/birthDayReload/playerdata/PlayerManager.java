package org.referix.birthDayReload.playerdata;

import org.bukkit.entity.Player;
import org.referix.birthDayReload.database.Database;
import org.referix.birthDayReload.database.PlayerDataDAO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static final PlayerManager instance = new PlayerManager();
    private final Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final PlayerDataDAO dao = new PlayerDataDAO(Database.getJdbi());

    public static PlayerManager getInstance() {
        return instance;
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getName(), name -> dao.loadPlayerData(player)
                .orElse(new PlayerData(player)));
    }

    public void savePlayerData(Player player) {
        PlayerData data = playerDataMap.get(player.getName());
        if (data != null) {
            dao.savePlayerData(data);
        }
    }

    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getName());
        dao.deletePlayerData(player);
    }

    public boolean isPlayerDataCached(Player player) {
        return playerDataMap.containsKey(player.getName());
    }

    /**
     * Отримати всі PlayerData з кешу.
     */
    public Map<String, PlayerData> getAllPlayerData() {
        return Collections.unmodifiableMap(playerDataMap);
    }

    /**
     * Отримати всі PlayerData з бази даних.
     */
    public List<PlayerData> getAllPlayerDataFromDatabase() {
        return dao.loadAllPlayerData();
    }
}
