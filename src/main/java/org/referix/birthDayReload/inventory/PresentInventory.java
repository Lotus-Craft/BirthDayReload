package org.referix.birthDayReload.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.referix.birthDayReload.utils.ItemManagerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PresentInventory extends BaseInventory {

    private final ItemManagerConfig itemManagerConfig;

    public PresentInventory(String title, int size, ItemManagerConfig itemManagerConfig) {
        super(title, size);
        this.itemManagerConfig = itemManagerConfig;

        // Загружаем предметы из конфигурации
        loadItems();
    }

    @Override
    protected void setupInventory() {
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        // Сохраняем предметы при закрытии инвентаря
        saveItems();
    }

    private void loadItems() {
        // Загружаем предметы из конфигурации
        List<ItemStack> items = itemManagerConfig.getItems();
        if (items == null || items.isEmpty()) return;
        for (int i = 0; i < items.size() && i < inventory.getSize(); i++) {
            inventory.setItem(i, items.get(i));
        }
    }

    private void saveItems() {
        if (itemManagerConfig == null) {
            throw new IllegalStateException("ItemManagerConfig is not initialized!");
        }

        // Фильтруем содержимое инвентаря, исключая null
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                items.add(item);
            }
        }

        // Сохраняем только если список не пустой
        if (items.isEmpty()) {
            itemManagerConfig.saveItems(Collections.emptyList());
        } else {
            itemManagerConfig.saveItems(items);
        }
    }

}
