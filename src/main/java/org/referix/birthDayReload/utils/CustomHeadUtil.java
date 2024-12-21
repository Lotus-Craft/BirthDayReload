package org.referix.birthDayReload.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomHeadUtil {

    private static final Map<Integer, String> numberTextures = new HashMap<>();

    public static ItemStack getNumberHead(int number, NamespacedKey key) {
        String texture = getNumberTexture(number);
        if (texture == null) {
            throw new IllegalArgumentException("Invalid number: " + number);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            // Установка текстуры через GameProfile
            GameProfile profile = new GameProfile(UUID.randomUUID(), "name"); // Имя профиля не null
            profile.getProperties().put("textures", new Property("textures", texture));

            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            // Adventure API: Устанавливаем отображаемое имя
            meta.displayName(Component.text("Present"));

            // Добавляем NamespacedKey в PersistentDataContainer
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, texture); // Сохраняем текстуру в метаданные

            head.setItemMeta(meta);
        }

        return head;
    }



    static {
        numberTextures.put(1, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg5MWYzNDQxMmZjYjQ2MGJjNmUzNDhhOWIxZjJkYWNmYzIxNjVjYjU5NTdmMDNkYmRiM2RiNmUxYThmOWNjIn19fQ==");
//        numberTextures.put(9, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTg3NDQ0ZWQ2ZDg0NTY5MDExNWIzODU4OGNmMWEyNjQ0ZGE5YzcxZTZmMzFhZTI0NjUxMDM4Y2IxZDQ3NmVhIn19fQ==");
//        numberTextures.put(10, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU0NGI3MDdiMjA1YjdkMjNlNDBhM2MwZGYxOWIwOWI2MmQwMjc4MDcyNzhlZjMwMWIzYzJhYjk4YjRmMGQ2In19fQ==");
//        numberTextures.put(11, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTNmZDZjMjI1OTk4YzJkYjRhNGFmZGRkMjRhOTNjNmYxY2YzNDk5MTRhYTNjNWExNTI1Y2M1NTg3M2IzMGQ3MiJ9fX0=");
//        numberTextures.put(12, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YzMTg3YTVhMGFhN2FiNDg5MzdjN2NmOTE5ODgxZDYxNzg5OTVhNTYxNWQyZWEyNDRlMmRhY2VjNjZlOGQ1NiJ9fX0=");
//        numberTextures.put(13, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTg2YzEyOGJhZmY5YWRkMzVkY2M3MTgzNWRlY2M1NTNiNThjOWIyMTE2ZjU0ZWY0MjM5NjBjZDI2Yzk0NjljOSJ9fX0=");
//        numberTextures.put(14, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjMxODFmYThjZjZiZjA4OTg3N2FkOTIxZTNmODQwMTQzMDQ0NWEzZTZiMTg1MGVhNGQ2NzUwMWRhYjZiMjAyNCJ9fX0=");
//        numberTextures.put(15, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FjODRiYWZjNDNlNGQzZGU2NjFkN2Y4NWRlNDhkNWU5YTYyZjRlNDg4Mjc4ODM0Yzc5ZTMwOTUxNjMxNTQ5NiJ9fX0=");
//        numberTextures.put(16, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FjODRiYWZjNDNlNGQzZGU2NjFkN2Y4NWRlNDhkNWU5YTYyZjRlNDg4Mjc4ODM0Yzc5ZTMwOTUxNjMxNTQ5NiJ9fX0=");
//        numberTextures.put(17, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE1MGU5YTExMmZlYjc5NWE2NDViZTI0NDBlYWQ1YTcxMmM4ODllYjE5MDI4MDVmMWMwN2YyZjU2ZmY0OTA2NyJ9fX0=");
//        //numberTextures.put(21, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDliMzk5NzYwMTc3YjI3NzVlMTc0ZDQ5NTEyZjdiNmZhOWFhMDgwNzEzZWY0MzRkNDQwN2IxMWUzZDk5N2E4NCJ9fX0=");
        //numberTextures.put(22, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZhZGViZjRmMDllMDdmNTMzMTBhZGQxMmI3ZjQ3ZDQ4ZGFlNmM3N2FhM2U2MjcwMDFiNTMwOTJlNjlmODcxOSJ9fX0=");
        //numberTextures.put(23, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmRhOWNiYmYzM2Y4Yjc5ZTcwNjEzOGQ0ZmFlODQzZGNkNDlkNDQxOTAwMTMzZDE1ODhlZDQ4NmM0NWU4ODIxMSJ9fX0=");
















        numberTextures.put(40, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZkMjQwMDAwMmFkOWZiYmJkMDA2Njk0MWViNWIxYTM4NGFiOWIwZTQ4YTE3OGVlOTZlNGQxMjlhNTIwODY1NCJ9fX0=");
        numberTextures.put(44, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk5YWI0NDFmYzk3ZjYwOTA4MWFkM2NlMzNkNTk4MjkxZDUxYmVmOGNiN2FkMjQ4NGI1YzEzODdjN2E4NCJ9fX0=");
    }

    public static String getNumberTexture(int number) {
        return numberTextures.getOrDefault(number, null);
    }


    public static Map<Integer, String> getAllNumberTextures() {
        return new HashMap<>(numberTextures); // Повертає копію мапи
    }

}
