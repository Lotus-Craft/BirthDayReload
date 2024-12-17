package org.referix.birthDayReload.database;

import org.jdbi.v3.core.Jdbi;
import org.referix.birthDayReload.BirthDayReload;

import java.io.File;

public class Database {

    private static Jdbi jdbi;

    public static Jdbi getJdbi() {
        if (jdbi == null) {
            String databasePath = getDatabasePath();
            jdbi = Jdbi.create("jdbc:sqlite:" + databasePath);
            setupDatabase();
        }
        return jdbi;
    }

    private static String getDatabasePath() {
        File dataFolder = BirthDayReload.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs(); // Створення папки плагіна, якщо вона ще не існує
        }
        return new File(dataFolder, "birthdays.db").getAbsolutePath();
    }

    private static void setupDatabase() {
        getJdbi().useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS player_data (" +
                    "player_uuid TEXT PRIMARY KEY," +
                    "birthday TEXT," +
                    "is_wished INTEGER," +
                    "wished TEXT" +
                    ");");
        });
    }
}
