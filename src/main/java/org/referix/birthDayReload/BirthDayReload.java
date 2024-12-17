package org.referix.birthDayReload;

import org.bukkit.plugin.java.JavaPlugin;
import org.referix.birthDayReload.command.MainCommand;
import org.referix.birthDayReload.database.Database;
import org.referix.birthDayReload.utils.LoggerUtils;

public final class BirthDayReload extends JavaPlugin {

    private static BirthDayReload instance;


    @Override
    public void onEnable() {
        instance = this;
        LoggerUtils.log("System initialization started...");
        try {
            LoggerUtils.log("Starting DataBase BirthDayReload...");
            Database.getJdbi();
        } catch (Exception e) {
            LoggerUtils.log("Error starting DataBase BirthDayReload: " + e.getMessage());
            getServer().getLogger().warning(e.getMessage());
        }
        new MainCommand("birthday");

        getServer().getPluginManager().registerEvents(new MainListener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BirthDayReload getInstance() {
        return instance;
    }
}
