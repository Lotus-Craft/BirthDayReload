package org.referix.birthDayReload;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.referix.birthDayReload.command.MainCommand;
import org.referix.birthDayReload.database.Database;
import org.referix.birthDayReload.inventory.YearInventory;
import org.referix.birthDayReload.inventory.InventoryClickHandler;
import org.referix.birthDayReload.inventory.InventoryManager;
import org.referix.birthDayReload.papi.BirthdayPlaceholder;
import org.referix.birthDayReload.utils.ConfigUtils;
import org.referix.birthDayReload.utils.LoggerUtils;
import org.referix.birthDayReload.utils.MessageManager;

import static org.referix.birthDayReload.utils.LoggerUtils.log;

public final class BirthDayReload extends JavaPlugin {

    private static BirthDayReload instance;

    private ConfigUtils configUtils;

    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;
        log("System initialization started...");

        // Ініціалізація бази даних
        try {
            log("Starting DataBase BirthDayReload...");
            Database.getJdbi();
            log("Database initialized successfully.");
        } catch (Exception e) {
            log("Error starting DataBase BirthDayReload: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //config load
        try {
            this.configUtils = new ConfigUtils(this);
            messageManager = new MessageManager(this);
        } catch (Exception e){
            log("Error loading config file: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

        // Створення та реєстрація команди
        try {
            log("Registering commands...");

            // Створення інвентаря
            log("Initializing CustomInventory...");
            YearInventory birthdayInventory = new YearInventory("Select Year your Birthday");
            InventoryManager.registerInventory(birthdayInventory);
            log("CustomInventory initialized successfully.");

            // Створення та реєстрація команди
            log("Creating MainCommand instance...");
            new MainCommand("birthday", birthdayInventory, messageManager);
            log("MainCommand registered successfully as 'birthday'.");

            log("Commands registered successfully.");
        } catch (Exception e) {
            log("Error registering commands: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Перевірка наявності PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new BirthdayPlaceholder().register();
            getLogger().info("BirthdayPlaceholder successfully registered with PlaceholderAPI.");
        } else {
            getLogger().warning("PlaceholderAPI not found. BirthdayPlaceholder not registered.");
        }



        // Реєстрація Listener'ів
        try {
            log("Registering listeners...");
            // Реєстрація обробника кліків
            getServer().getPluginManager().registerEvents(new InventoryClickHandler(), this);

            // Створення та реєстрація кастомних інвентарів
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
        // Plugin shutdown logic
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public static BirthDayReload getInstance() {
        return instance;
    }
}
