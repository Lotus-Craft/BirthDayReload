package org.referix.birthDayReload.utils.configmannagers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemManagerConfig {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;

    public ItemManagerConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "items.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info("File path: " + file.getAbsolutePath());
        plugin.getLogger().info("Config loaded: " + (config != null));
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        if (!file.exists()) {
            try {
                if (file.getParentFile().mkdirs()) {
                    plugin.getLogger().info("Created plugin data folder.");
                }
                if (file.createNewFile()) {
                    plugin.getLogger().info("Created items.yml file.");
                }

                // Додаємо базовий список предметів
                initializeDefaultItems();
                config.save(file);
                plugin.getLogger().info("Initialized items.yml with default items.");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create items.yml: " + e.getMessage());
            }
        }
    }

    private void initializeDefaultItems() {
        List<ItemStack> defaultItems = new ArrayList<>();
        defaultItems.add(new ItemStack(Material.DIAMOND, 5)); // 5 діамантів як стартовий приклад
        config.set("items", defaultItems);
    }

    // Зберегти список предметів у items.yml
    public void saveItems(List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            plugin.getLogger().warning("Tried to save empty or null item list to items.yml.");
            return;
        }

        config.set("items", items); // Bukkit вміє серіалізувати ItemStack
        try {
            config.save(file);
            plugin.getLogger().info("Items saved to items.yml.");
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save items.yml: " + e.getMessage());
        }
    }

    public List<ItemStack> getItems() {
        List<?> itemList = config.getList("items", Collections.emptyList());
        if (itemList == null || itemList.isEmpty()) {
            plugin.getLogger().info("No items found in items.yml.");
            return new ArrayList<>();
        }

        List<ItemStack> items = new ArrayList<>();
        for (Object obj : itemList) {
            if (obj instanceof ItemStack) {
                items.add((ItemStack) obj);
            } else {
                plugin.getLogger().warning("Invalid item found in items.yml, skipping...");
            }
        }
        return items;
    }
}
