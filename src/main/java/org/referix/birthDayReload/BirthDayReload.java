package org.referix.birthDayReload;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.referix.birthDayReload.command.MainCommand;
import org.referix.birthDayReload.database.Database;
import org.referix.birthDayReload.discord.DiscordHttp;
import org.referix.birthDayReload.inventory.PresentInventory;
import org.referix.birthDayReload.inventory.InventoryClickHandler;
import org.referix.birthDayReload.inventory.InventoryManager;
import org.referix.birthDayReload.papi.BirthdayPlaceholder;
import org.referix.birthDayReload.utils.configmannagers.ConfigUtils;
import org.referix.birthDayReload.utils.configmannagers.DiscordConfig;
import org.referix.birthDayReload.utils.configmannagers.ItemManagerConfig;
import org.referix.birthDayReload.utils.configmannagers.MessageManager;
import org.referix.birthDayReload.utils.luckperm.LuckPerm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.referix.birthDayReload.utils.LoggerUtils.log;
import static org.referix.birthDayReload.utils.LoggerUtils.logWarning;

public final class BirthDayReload extends JavaPlugin {

    private static BirthDayReload instance;

    private MessageManager messageManager;

    private ItemManagerConfig itemConfig;

    private LuckPerm luckPermUtils;

    private NamespacedKey textureKey;
    private DiscordHttp discordHttp;

    private File configFile;

    @Override
    public void onEnable() {
        // Завантаження та синхронізація конфігурації з дефолтним файлом
        this.configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig(); // Зберігаємо дефолтний конфіг якщо його немає
        }

        syncConfigWithDefaults(); // Викликаємо метод синхронізації конфігурацій
        int pluginId = 24407; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        instance = this;
        log("System initialization started...");

        // Инициализация базы данных
        try {
            textureKey = new NamespacedKey(this, "custom_texture");
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
            log("ItemManagerConfig initialized: " + true);
        } catch (Exception e) {
            log("ItemManagerConfig is null: " + (itemConfig == null));
            log("Error loading config file: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Проверяем, доступен ли LuckPerms API
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            getLogger().info("LuckPerms detected. Enabling related features.");
            this.luckPermUtils = new LuckPerm(luckPerms,messageManager);
        } catch (Exception e) {
            this.luckPermUtils = null;
        }

        try {
            log("Try to start Discord Bot...");
            DiscordConfig discordSettings = new DiscordConfig(getConfig());
            if (discordSettings.isEnabled()) {
                this.discordHttp = new DiscordHttp(discordSettings);
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
            new MainCommand("birthday", null, messageManager, presentInventory, discordHttp);
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
            getServer().getPluginManager().registerEvents(new MainListener(textureKey,luckPermUtils,messageManager,discordHttp), this);
            log("Listeners registered successfully.");
        } catch (Exception e) {
            log("Error registering listeners: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

        log("BirthDayReload successfully enabled!");
    }



    @Override
    public void onDisable() {
        discordHttp.close();
    }



    public LuckPerm getLuckPermUtils() {
        return this.luckPermUtils;
    }

    public NamespacedKey getTextureKey() {
        return textureKey;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public static BirthDayReload getInstance() {
        return instance;
    }


    /**
     * Синхронізація конфігурації на сервері з дефолтною конфігурацією
     */
    public void syncConfigWithDefaults() {
        try {
            // Завантажуємо дефолтну конфігурацію з JAR
            InputStream defaultConfigStream = getResource("config.yml");
            if (defaultConfigStream == null) {
                return;
            }

            // Завантажуємо конфігурацію з потоку
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));

            // Завантажуємо конфігурацію, яка на сервері (з файлу)
            FileConfiguration serverConfig = YamlConfiguration.loadConfiguration(configFile);
            getLogger().info("Server config loaded successfully.");

            // Порівнюємо конфігурації та додаємо відсутні елементи з дефолтного конфігу в серверний
            boolean updated = syncSections(defaultConfig, serverConfig, "");

            if (updated) {
                // Зберігаємо оновлений конфіг на сервері
                serverConfig.save(configFile);
            }
        } catch (Exception e) {
        }
    }

    private boolean syncSections(FileConfiguration defaultConfig, FileConfiguration serverConfig, String path) {
        boolean updated = false;

        // Логування для порівняння файлів

        // Перевіряємо всі ключі в секціях на всіх рівнях
        for (String key : defaultConfig.getConfigurationSection(path).getKeys(true)) {
            String fullPath = path.isEmpty() ? key : path + "." + key;

            // Логування для порівняння шляху

            // Якщо це секція, перевіряємо, чи вона відсутня на сервері
            if (defaultConfig.isConfigurationSection(fullPath)) {
                if (!serverConfig.isConfigurationSection(fullPath)) {
                    serverConfig.createSection(fullPath); // Якщо секція відсутня — створюємо її
                    updated = true;
                }
                updated |= syncSections(defaultConfig, serverConfig, fullPath); // Рекурсивно перевіряємо вкладені секції
            } else {
                // Якщо це ключ, перевіряємо його відсутність
                if (!serverConfig.contains(fullPath)) {
                    serverConfig.set(fullPath, defaultConfig.get(fullPath)); // Додаємо відсутній ключ
                    updated = true;
                }
            }
        }

        return updated;
    }










}
