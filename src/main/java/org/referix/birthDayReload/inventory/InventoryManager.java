package org.referix.birthDayReload.inventory;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    private static final Map<String, BaseInventory> inventories = new HashMap<>();

    public static void registerInventory(BaseInventory inventory) {
        inventories.put(inventory.getTitle(), inventory);
    }

    public static BaseInventory getInventory(String title) {
        return inventories.get(title);
    }

    public static boolean isCustomInventory(String title) {
        return inventories.containsKey(title);
    }
}
