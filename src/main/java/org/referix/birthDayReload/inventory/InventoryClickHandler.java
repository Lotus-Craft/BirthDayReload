package org.referix.birthDayReload.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClickHandler implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (InventoryManager.isCustomInventory(title)) {
            BaseInventory inventory = InventoryManager.getInventory(title);
            if (inventory != null) {
                inventory.onInventoryClick(event);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (InventoryManager.isCustomInventory(title)) {
            BaseInventory inventory = InventoryManager.getInventory(title);
            if (inventory != null) {
                inventory.onInventoryClose(event);
            }
        }
    }
}
