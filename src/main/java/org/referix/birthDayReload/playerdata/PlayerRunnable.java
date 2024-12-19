package org.referix.birthDayReload.playerdata;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;

public class PlayerRunnable extends BukkitRunnable {

    private final JavaPlugin plugin;

    public PlayerRunnable(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Виконати перевірку всіх гравців на день народження
        PlayerManager.getInstance().updateBirthdayPrefixes();
    }

    public void scheduleAtMidnight() {
        LocalTime now = LocalTime.now();
        LocalTime midnight = LocalTime.MIDNIGHT;

        // Обчислюємо затримку до 00:00
        long delaySeconds = now.isBefore(midnight) ?
                midnight.toSecondOfDay() - now.toSecondOfDay() :
                24 * 60 * 60 - now.toSecondOfDay();

        // Плануємо виконання задачі раз на добу
        this.runTaskTimer(plugin, delaySeconds * 20L, 24 * 60 * 60 * 20L); // Затримка і період в тиках
    }
}
