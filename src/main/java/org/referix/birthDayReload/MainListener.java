package org.referix.birthDayReload;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.referix.birthDayReload.discord.DiscordHttp;
import org.referix.birthDayReload.inventory.InventoryManager;
import org.referix.birthDayReload.playerdata.PlayerData;
import org.referix.birthDayReload.playerdata.PlayerManager;
import org.referix.birthDayReload.utils.configmannagers.MessageManager;
import org.referix.birthDayReload.utils.luckperm.LuckPerm;

import java.time.LocalDate;

import static org.referix.birthDayReload.utils.LoggerUtils.log;
import static org.referix.birthDayReload.utils.LoggerUtils.logWarning;

public class MainListener implements Listener {

    private final NamespacedKey textureKey;
    private final LuckPerm luckPerm;
    private final MessageManager messageManager;
    private final DiscordHttp discordHttp;


    public MainListener(NamespacedKey textureKey, LuckPerm luckPerm, MessageManager messageManager , DiscordHttp discordHttp) {
        this.textureKey = textureKey;
        this.luckPerm = luckPerm;
        this.messageManager = messageManager;
        this.discordHttp = discordHttp;
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
                    player.sendMessage(messageManager.BIRTHDAY_SELEBRATION_MESSAGE_FIRST
                            .replaceText(builder -> builder.match("%player%").replacement(player.getName())));
                    player.sendMessage(messageManager.BIRTHDAY_SELEBRATION_MESSAGE_SECOND
                            .replaceText(builder -> builder.match("%player%").replacement(player.getName())));

//                    Map<String, String> placeholders = Map.of(
//                            "player", player.getName(),
//                            "date", data.getBirthday().toString()
//                    );
//
//                    DiscordMessage discordMessage = messageManager.getParsedDiscordMessage("Discord.Embedded-messages.happy-birthday", placeholders);
//                    messageService.sendEmbed(discordMessage);
                    if (discordHttp != null) discordHttp.sendHappyBirthdayMessage(player.getName());
                }
                data.setPrefix(BirthDayReload.getInstance().getMessageManager().BIRTHDAY_BOY_PREFIX); // Встановлюємо префікс
            } else {
                // Скидання isWished, якщо сьогодні не день народження
                if (data.isWished()) {
                    discordHttp.resetPlayerMessageStatus(player.getName());
                    data.setIsWished(false);
                    data.setPrefix(null); // Встановлюємо префікс
                    manager.savePlayerData(player);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager.getInstance().savePlayerData(player);
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
                event.setDropItems(false);
                for (ItemStack item : InventoryManager.getInventory("Present").getInventory().getContents()) {
                    if (item != null) {
                        player.getInventory().addItem(item);
                    }
                }
                if (luckPerm != null) {
                    luckPerm.applyLuckPermGroup(player);
                    player.sendMessage(messageManager.BIRTHDAY_LUCKPERMS_MESSAGE);
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

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.getInstance().isWaitingForWishes(player.getUniqueId())) return;
        event.setCancelled(true);
        String wishes = event.getMessage();
        PlayerData data = PlayerManager.getInstance().getPlayerData(player);
        data.setWishes(wishes);
        PlayerManager.getInstance().savePlayerData(player);
        discordHttp.sendSetBirthdayMessage(player.getName(), data.getBirthday().toString(), wishes);
        PlayerManager.getInstance().removeWaitingForWishes(player.getUniqueId());
        player.sendMessage(messageManager.BIRTHDAY_SET_SUCCESS);
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
