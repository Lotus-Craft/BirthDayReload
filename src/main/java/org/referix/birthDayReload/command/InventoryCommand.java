package org.referix.birthDayReload.command;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.referix.birthDayReload.BirthDayReload;
import org.referix.birthDayReload.inventory.PresentInventory;
import org.referix.birthDayReload.playerdata.PlayerManager;
import org.referix.birthDayReload.playerdata.PlayerData;
import org.referix.birthDayReload.utils.CustomHeadUtil;

import java.time.LocalDate;

public class InventoryCommand {

    private final PresentInventory presentInventory;

    public InventoryCommand(PresentInventory presentInventory) {
        this.presentInventory = presentInventory;
    }

    public void handleInventory(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, Component.text("§cOnly players can use this command."));
            return;
        }

        if (!player.hasPermission("birthday.inventory")) {
            sendMessage(player, Component.text("§cYou don't have permission to use this command."));
            return;
        }

        if (args.length < 2) {
            sendMessage(player, Component.text("§cUsage: /birthday present <open|give>"));
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "open":
                handleOpen(player);
                break;

            case "give":
                handleGive(player);
                break;

            default:
                sendMessage(player, Component.text("§cUnknown action. Usage: /birthday present <open|give>"));
        }
    }

    private void handleOpen(Player player) {
        presentInventory.open(player); // Открываем инвентарь
        sendMessage(player, Component.text("§aYou have opened the birthday present inventory."));
    }

    private void handleGive(Player player) {
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);

        if (playerData == null) {
            sendMessage(player, Component.text("§cYour data could not be loaded."));
            return;
        }

        // Проверяем, совпадает ли дата рождения с сегодняшней
        LocalDate today = LocalDate.now();
        if (!PlayerManager.getInstance().isBirthdayToday(playerData, today)) {
            sendMessage(player, Component.text("§cIt's not your birthday today!"));
            return;
        }

        // Проверяем, может ли игрок получить подарок
        if (playerData.isWished()) {
            sendMessage(player, Component.text("§cYou have already received your birthday present."));
            return;
        }

        // Передаем предметы из инвентаря игроку
//        for (ItemStack item : presentInventory.getInventory().getContents()) {
//            if (item != null) {
//                player.getInventory().addItem(item);
//            }
//        }
        ItemStack customHead = CustomHeadUtil.getNumberHead(1, BirthDayReload.getInstance().getTextureKey());
        player.getInventory().addItem(customHead);

        // Помечаем, что игрок получил подарок
        playerData.setIsWished(true);
        PlayerManager.getInstance().savePlayerData(player);

        sendMessage(player, Component.text("§aYou have received your birthday present!"));
    }



    private void sendMessage(CommandSender sender, Component message) {
        if (sender instanceof Player player) {
            player.sendMessage(message);
        } else {
            sender.sendMessage(message);
        }
    }
}
