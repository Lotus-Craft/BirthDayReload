package org.referix.birthDayReload.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.referix.birthDayReload.BirthDayReload;
import org.referix.birthDayReload.inventory.YearInventory;
import org.referix.birthDayReload.playerdata.PlayerData;
import org.referix.birthDayReload.playerdata.PlayerManager;
import org.referix.birthDayReload.utils.MessageManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.referix.birthDayReload.utils.LoggerUtils.*;


public class MainCommand extends AbstractCommand {

    private final MessageManager messageManager;

    public MainCommand(String command, YearInventory birthdayInventory, MessageManager messageManager) {
        super(command);
        this.messageManager = messageManager;
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
            case "reload":
                handleReload(sender);
                break;
            default:
                sendMessage(sender, messageManager.BIRTHDAY_UNKNOWN_COMMAND);
        }
        return true;
    }


    // -- reload --
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("birthday.reload")) {
            sendMessage(sender, Component.text("§cYou don't have permission to reload the plugin."));
            return;
        }

        try {
            messageManager.reloadMessages();
            sendMessage(sender, Component.text("§aPlugin configuration and messages reloaded successfully."));
        } catch (Exception e) {
            sendMessage(sender, Component.text("§cAn error occurred while reloading the plugin. Check the console for details."));
            logSevere(e.getMessage());
        }
    }



    private void handleSet(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, messageManager.BIRTHDAY_ONLY_PLAYERS);
            return;
        }

        if (args.length != 2) {
            sendMessage(player, messageManager.BIRTHDAY_SET_FORMAT_ERROR);
            return;
        }

        String dateInput = args[1];

        try {
            LocalDate birthday = LocalDate.parse(dateInput);
            if (isValidBirthday(birthday)) {
                PlayerData data = PlayerManager.getInstance().getPlayerData(player);
                data.setBirthday(birthday);
                PlayerManager.getInstance().savePlayerData(player);

                sendMessage(player, messageManager.BIRTHDAY_SET_SUCCESS
                        .replaceText(builder -> builder.match("%date%").replacement(birthday.toString())));
            } else {
                sendMessage(player, messageManager.BIRTHDAY_SET_FUTURE_ERROR);
            }
        } catch (Exception e) {
            sendMessage(player, messageManager.BIRTHDAY_SET_FORMAT_ERROR);
        }
    }

    private boolean isValidBirthday(LocalDate date) {
        return !date.isAfter(LocalDate.now());
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("birthday.delete")) {
            sendMessage(sender, messageManager.BIRTHDAY_DELETE_NO_PERMISSION);
            return;
        }

        if (args.length != 2) {
            sendMessage(sender, messageManager.BIRTHDAY_DELETE_USAGE);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(sender, messageManager.BIRTHDAY_DELETE_PLAYER_NOT_FOUND);
            return;
        }

        PlayerManager.getInstance().removePlayerData(target);
        sendMessage(sender, messageManager.BIRTHDAY_DELETE_SUCCESS
                .replaceText(builder -> builder.match("%player%").replacement(target.getName())));
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

    private void sendMessage(CommandSender sender, Component message) {
        if (message == null) {
            logWarning("Attempted to send a null message to: " + sender.getName());
            return;
        }

        if (sender instanceof Player player) {
            log("Sending message to player: " + player.getName());
            player.sendMessage(message);
        } else {
            log("Sending message to console...");
            String serializedMessage = LegacyComponentSerializer.legacySection().serialize(message);
            sender.sendMessage(serializedMessage);
        }
    }


    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }


    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Основные подкоманды
            completions.add("set");
            if (sender.hasPermission("birthday.delete")) completions.add("delete");
            if (sender.hasPermission("birthday.list")) completions.add("list");
            if (sender.hasPermission("birthday.reload")) completions.add("reload");

            return filterSuggestions(completions, args[0]);
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "set":
                    // Предлагаем пример формата даты
                    completions.add("yyyy-mm-dd");
                    break;

                case "delete":
                    if (sender.hasPermission("birthday.delete")) {
                        // Предлагаем список онлайн игроков для удаления
                        return filterSuggestions(Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .collect(Collectors.toList()), args[1]);
                    }
                    break;

                default:
                    break;
            }
        }

        return completions;
    }

    private List<String> filterSuggestions(List<String> completions, String input) {
        return completions.stream()
                .filter(suggestion -> suggestion.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
