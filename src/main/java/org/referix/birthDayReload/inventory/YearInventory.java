package org.referix.birthDayReload.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.referix.birthDayReload.BirthDayReload;
import org.referix.birthDayReload.utils.headutils.CustomHeadUtil;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class YearInventory extends BaseInventory {


    private final List<Integer> lastClicks = new LinkedList<>(); // Останні 4 кліки

    public YearInventory(String title) {
        super(title, 45); // 45 слотів
    }

    @Override
    protected void setupInventory() {
        Map<Integer, String> numberTextures = CustomHeadUtil.getAllNumberTextures();
        for (Integer number : numberTextures.keySet()) {
            ItemStack head = CustomHeadUtil.getNumberHead(number, BirthDayReload.getInstance().getTextureKey());
            inventory.setItem(number, head); // Вставляємо голову у відповідний слот
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot >= 8 && slot <= 17) { // Клік по цифрах (слоти 8–17)
            int number = slot - 8; // Віднімаємо 8, щоб отримати число
            if (lastClicks.size() >= 4) {
                lastClicks.remove(0); // Видаляємо найстаріший клік
            }
            lastClicks.add(number); // Додаємо число
            player.sendMessage("§aCurrent input: " + getYearInput());
        } else if (slot == 40) { // Вихід
            player.closeInventory();
            player.sendMessage("§cSelection cancelled.");
        } else if (slot == 44) { // Підтвердження
            int year = getYearInput();
            if (isValidYear(year)) {
                player.sendMessage("§aYear confirmed: " + year);
                player.closeInventory();
            } else {
                player.sendMessage("§cInvalid year! Please select again.");
                lastClicks.clear(); // Очищаємо кліки
            }
        }
    }

    private int getYearInput() {
        if (lastClicks.size() < 4) {
            return -1; // Недостатньо чисел для року
        }
        StringBuilder year = new StringBuilder();
        for (Integer number : lastClicks) {
            year.append(number);
        }
        return Integer.parseInt(year.toString());
    }

    private boolean isValidYear(int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return year >= 1900 && year <= currentYear; // Рік у діапазоні
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        lastClicks.clear(); // Очищаємо історію кліків при закритті
    }
}
