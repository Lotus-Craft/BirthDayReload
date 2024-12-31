package org.referix.birthDayReload.utils.headutils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CustomHeadUtil {

    private static final Map<Integer, String> numberTextures = new HashMap<>();

    public static ItemStack getNumberHead(int number, NamespacedKey key) {
        String textureUrl = getNumberTexture(number);
        if (textureUrl == null) {
            throw new IllegalArgumentException("Invalid number: " + number);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        if (skullMeta != null) {
            PlayerProfile playerProfile = org.bukkit.Bukkit.createPlayerProfile("UUID-" + number);
            PlayerTextures textures = playerProfile.getTextures();

            try {
                // Преобразование строки в URL
                URL url = new URL(textureUrl);
                textures.setSkin(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid texture URL: " + textureUrl, e);
            }

            playerProfile.setTextures(textures);
            skullMeta.setOwnerProfile(playerProfile);

            // Adventure API: Устанавливаем отображаемое имя
            skullMeta.displayName(Component.text("Present"));

            // Добавляем NamespacedKey в PersistentDataContainer
            PersistentDataContainer data = skullMeta.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, textureUrl);

            head.setItemMeta(skullMeta);
        }

        return head;
    }

    static {
        numberTextures.put(1, "http://textures.minecraft.net/texture/a891f34412fcb460bc6e348a9b1f2dacfc2165cb5957f03dbdb3db6e1a8f9cc");
        numberTextures.put(40, "http://textures.minecraft.net/texture/afd2400002ad9fbbbd0066941eb5b1a384ab9b0e48a178ee96e4d129a5208654");
        numberTextures.put(44, "http://textures.minecraft.net/texture/e99ab441fc97f609081ad3ce33d598291d51bef8cb7ad2484b5c1387c7a84");
    }

    public static String getNumberTexture(int number) {
        return numberTextures.getOrDefault(number, null);
    }

    public static Map<Integer, String> getAllNumberTextures() {
        return new HashMap<>(numberTextures); // Возвращает копию мапы
    }

}
