package org.referix.birthDayReload.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.referix.birthDayReload.inventory.YearInventory;
import org.referix.birthDayReload.playerdata.PlayerManager;

public class MainCommand extends AbstractCommand {

    private YearInventory inventoryData;

    public MainCommand(String command, YearInventory inventoryData) {
        super(command);
        this.inventoryData = inventoryData;
    }


    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "set":
                handleSet(sender, args);
                break;
            case "delete":
                handleDelete(sender, args);
                break;
            case "list":
                handleList(sender, args);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                sender.sendMessage("§cUnknown subcommand. Use /birthday help for more information.");
        }
        return true;
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can set their own birthday.");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /birthday set");
            return;
        }

        Player player = (Player) sender;
        try {
            inventoryData.open(player);
        } catch (Exception e) {
            sender.sendMessage("§cInvalid date format. Use yyyy-mm-dd.");
        }
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("birthday.delete")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /birthday delete <player name>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }

        PlayerManager.getInstance().removePlayerData(target);
        sender.sendMessage("§aBirthday data for " + target.getName() + " has been deleted.");
    }

    private void handleList(CommandSender sender, String[] args) {
        if (!sender.hasPermission("birthday.list")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        sender.sendMessage("§aList of all players' birthdays:");
        PlayerManager.getInstance().getAllPlayerData().forEach((player, data) -> {
            sender.sendMessage("§7" + data.getPlayer().getName() + ": §e" + data.getBirthday());
        });
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§a===== §6Birthday Commands §a=====");
        sender.sendMessage("§e/birthday set <yyyy-mm-dd> §7- Set your birthday.");
        if (sender.hasPermission("birthday.delete")) {
            sender.sendMessage("§e/birthday delete <player> §7- Delete a player's birthday.");
        }
        if (sender.hasPermission("birthday.list")) {
            sender.sendMessage("§e/birthday list §7- List all players' birthdays.");
        }
        sender.sendMessage("§e/birthday help §7- Show this help message.");
        sender.sendMessage("§a================================");
    }
}
