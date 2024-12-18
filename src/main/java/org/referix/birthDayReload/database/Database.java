package org.referix.birthDayReload.database;

import org.jdbi.v3.core.Jdbi;
import org.referix.birthDayReload.BirthDayReload;

import java.io.File;
import java.util.logging.Level;

public class Database {

    private static Jdbi jdbi;

    public static Jdbi getJdbi() {
        if (jdbi == null) {
            try {
                String databasePath = getDatabasePath();
                BirthDayReload.getInstance().getLogger().info("Connecting to database at: " + databasePath);

                jdbi = Jdbi.create("jdbc:sqlite:" + databasePath);

                setupDatabase();
                BirthDayReload.getInstance().getLogger().info("Database setup completed successfully.");
            } catch (Exception e) {
                BirthDayReload.getInstance().getLogger().log(Level.SEVERE, "Error initializing the database", e);
                throw new RuntimeException("Failed to initialize the database", e);
            }
        }
        return jdbi;
    }

    private static String getDatabasePath() {
        File dataFolder = BirthDayReload.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs(); // Створення папки плагіна, якщо вона ще не існує
            if (created) {
                BirthDayReload.getInstance().getLogger().info("Data folder created successfully: " + dataFolder.getAbsolutePath());
            } else {
                BirthDayReload.getInstance().getLogger().severe("Failed to create data folder: " + dataFolder.getAbsolutePath());
            }
        }
        return new File(dataFolder, "birthdays.db").getAbsolutePath();
    }

    private static void setupDatabase() {
        getJdbi().useHandle(handle -> {
            try {
                BirthDayReload.getInstance().getLogger().info("Creating or verifying the database table...");
                handle.execute("CREATE TABLE IF NOT EXISTS player_data (" +
                        "player_uuid TEXT PRIMARY KEY," +
                        "birthday TEXT," +
                        "is_wished INTEGER," +
                        "wished TEXT" +
                        ");");
                BirthDayReload.getInstance().getLogger().info("Database table verified.");
            } catch (Exception e) {
                BirthDayReload.getInstance().getLogger().log(Level.SEVERE, "Error setting up the database table", e);
                throw e;
            }
        });
    }
}
