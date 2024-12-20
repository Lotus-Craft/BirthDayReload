package org.referix.birthDayReload;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.referix.birthDayReload.command.MainCommand;
import org.referix.birthDayReload.database.Database;
import org.referix.birthDayReload.discord.DiscordManager;
import org.referix.birthDayReload.discord.DiscordSettings;
import org.referix.birthDayReload.inventory.PresentInventory;
import org.referix.birthDayReload.inventory.YearInventory;
import org.referix.birthDayReload.inventory.InventoryClickHandler;
import org.referix.birthDayReload.inventory.InventoryManager;
import org.referix.birthDayReload.papi.BirthdayPlaceholder;
import org.referix.birthDayReload.utils.ConfigUtils;
import org.referix.birthDayReload.utils.ItemManagerConfig;
import org.referix.birthDayReload.utils.LoggerUtils;
import org.referix.birthDayReload.utils.MessageManager;

import static org.referix.birthDayReload.utils.LoggerUtils.log;
import static org.referix.birthDayReload.utils.LoggerUtils.logWarning;

public final class BirthDayReload extends JavaPlugin {

    private static BirthDayReload instance;

    private MessageManager messageManager;
    private DiscordManager discordManager;

    private ItemManagerConfig itemConfig;

    @Override
    public void onEnable() {
        instance = this;
        log("System initialization started...");

        // Инициализация базы данных
        try {
            log("Starting DataBase BirthDayReload...");
            Database.getJdbi();
            log("Database initialized successfully.");
        } catch (Exception e) {
            log("Error starting DataBase BirthDayReload: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Загрузка конфигурации
        try {
            ConfigUtils configUtils = new ConfigUtils(this);
            this.itemConfig = new ItemManagerConfig(this); // Инициализируем здесь
            this.messageManager = new MessageManager(this);
            this.itemConfig = new ItemManagerConfig(this);
            log("ItemManagerConfig initialized: " + (itemConfig != null));
        } catch (Exception e) {
            log("ItemManagerConfig is null: " + (itemConfig == null));
            log("Error loading config file: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            MessageManager messageManager = new MessageManager(this);

            DiscordSettings discordSettings = new DiscordSettings(messageManager);
            discordManager = new DiscordManager(discordSettings);
            if (discordSettings.getIsEnabled()) {
                discordManager.start();

//                discordManager.getMessageService().sendMessage("Плагин BirthDayReload успешно запущен!");

                log("The Discord bot has been successfully started");
            }
        } catch (Exception e) {
            log("Error when launching the plugin: " + e.getMessage());
        }

        // Регистрация инвентарей и команды
        try {
            log("Registering commands...");

            // Создаем инвентари
            log("Initializing CustomInventory...");
            PresentInventory presentInventory = new PresentInventory("Present", 45, itemConfig);
            InventoryManager.registerInventory(presentInventory);
            log("CustomInventory initialized successfully.");

            // Создаем и регистрируем команды
            log("Creating MainCommand instance...");
            new MainCommand("birthday", null, messageManager, presentInventory);
            log("MainCommand registered successfully as 'birthday'.");

            log("Commands registered successfully.");
        } catch (Exception e) {
            log("Error registering commands: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Регистрация PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new BirthdayPlaceholder().register();
            log("BirthdayPlaceholder successfully registered with PlaceholderAPI.");
        } else {
            logWarning("PlaceholderAPI not found. BirthdayPlaceholder not registered.");
        }

        // Регистрация событий
        try {
            log("Registering listeners...");
            getServer().getPluginManager().registerEvents(new InventoryClickHandler(), this);
            getServer().getPluginManager().registerEvents(new MainListener(), this);
            log("Listeners registered successfully.");
        } catch (Exception e) {
            log("Error registering listeners: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

        log("BirthDayReload successfully enabled!");
    }



    @Override
    public void onDisable() {
        if (discordManager != null) {
            discordManager.stop();
        }
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public static BirthDayReload getInstance() {
        return instance;
    }

}
