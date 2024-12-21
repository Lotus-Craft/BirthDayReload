package org.referix.birthDayReload;


import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.referix.birthDayReload.inventory.InventoryManager;
import org.referix.birthDayReload.playerdata.PlayerData;
import org.referix.birthDayReload.playerdata.PlayerManager;

import java.time.LocalDate;

import static org.referix.birthDayReload.utils.LoggerUtils.log;

public class MainListener implements Listener {

    private final NamespacedKey textureKey;

    public MainListener(NamespacedKey textureKey) {
        this.textureKey = textureKey;
    }

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

            if (PlayerManager.getInstance().isBirthdayToday(data, today)) {
                if (!data.isWished()) {
                    player.sendMessage("§6§lHappy Birthday, " + player.getName() + "! 🎉");
                    player.sendMessage("§aMay your day be filled with joy and celebration!");

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

    //custom head present
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer itemData = meta.getPersistentDataContainer();

            if (itemData.has(textureKey, PersistentDataType.STRING)) {
                String texture = itemData.get(textureKey, PersistentDataType.STRING);


                Block block = event.getBlockPlaced();
                if (block.getState() instanceof Skull) {
                    Skull skull = (Skull) block.getState();


                    PersistentDataContainer blockData = skull.getPersistentDataContainer();
                    blockData.set(textureKey, PersistentDataType.STRING, texture);

                    skull.update();
                }
            }
        }
    }




    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
            Skull skull = (Skull) block.getState();
            PersistentDataContainer data = skull.getPersistentDataContainer();

            if (data.has(textureKey, PersistentDataType.STRING)) {
                Player player = event.getPlayer();

                for (ItemStack item : InventoryManager.getInventory("Present").getInventory().getContents()) {
                    if (item != null) {
                        player.getInventory().addItem(item);
                    }
                }
                Location center = block.getLocation().add(0.5, 0.5, 0.5);

                launchFireworks(center);
                launchFireworks(center.clone().add(1, 0, 0));
                launchFireworks(center.clone().add(-1, 0, 0));
                launchFireworks(center.clone().add(0, 0, 1));
                launchFireworks(center.clone().add(0, 0, -1));
            }
        }
    }

    private void launchFireworks(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.RED, Color.ORANGE, Color.YELLOW)
                .withFade(Color.BLUE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(2);
        firework.setFireworkMeta(meta);
    }

}
