package org.referix.birthDayReload.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class BaseInventory {

    private final String title;
    protected final Inventory inventory;

    public BaseInventory(String title, int size) {
        this.title = title;
        this.inventory = Bukkit.createInventory(null, size, title);
        setupInventory();
    }

    protected abstract void setupInventory();

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public abstract void onInventoryClick(InventoryClickEvent event);

    public void onInventoryClose(InventoryCloseEvent event) {
    }

    public String getTitle() {
        return title;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
