package org.referix.birthDayReload;

import org.bukkit.plugin.java.JavaPlugin;
import org.referix.birthDayReload.command.MainCommand;
import org.referix.birthDayReload.database.Database;
import org.referix.birthDayReload.inventory.YearInventory;
import org.referix.birthDayReload.inventory.InventoryClickHandler;
import org.referix.birthDayReload.inventory.InventoryManager;
import org.referix.birthDayReload.utils.LoggerUtils;

public final class BirthDayReload extends JavaPlugin {

    private static BirthDayReload instance;


    @Override
    public void onEnable() {
        instance = this;
        LoggerUtils.log("System initialization started...");

        // Ініціалізація бази даних
        try {
            LoggerUtils.log("Starting DataBase BirthDayReload...");
            Database.getJdbi();
            LoggerUtils.log("Database initialized successfully.");
        } catch (Exception e) {
            LoggerUtils.log("Error starting DataBase BirthDayReload: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return; // Зупиняємо ініціалізацію плагіна
        }

        // Створення та реєстрація команди
        try {
            LoggerUtils.log("Registering commands...");

            // Створення інвентаря
            LoggerUtils.log("Initializing CustomInventory...");
            YearInventory birthdayInventory = new YearInventory("Select Year your Birthday");
            InventoryManager.registerInventory(birthdayInventory);
            LoggerUtils.log("CustomInventory initialized successfully.");

            // Створення та реєстрація команди
            LoggerUtils.log("Creating MainCommand instance...");
            new MainCommand("birthday", birthdayInventory);
            LoggerUtils.log("MainCommand registered successfully as 'birthday'.");

            LoggerUtils.log("Commands registered successfully.");
        } catch (Exception e) {
            LoggerUtils.log("Error registering commands: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        // Реєстрація Listener'ів
        try {
            LoggerUtils.log("Registering listeners...");
            // Реєстрація обробника кліків
            getServer().getPluginManager().registerEvents(new InventoryClickHandler(), this);

            // Створення та реєстрація кастомних інвентарів
            getServer().getPluginManager().registerEvents(new MainListener(), this);
            LoggerUtils.log("Listeners registered successfully.");
        } catch (Exception e) {
            LoggerUtils.log("Error registering listeners: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        LoggerUtils.log("BirthDayReload successfully enabled!");
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BirthDayReload getInstance() {
        return instance;
    }
}
