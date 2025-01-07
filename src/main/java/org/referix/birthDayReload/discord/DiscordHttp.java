package org.referix.birthDayReload.discord;

import okhttp3.*;
import org.referix.birthDayReload.utils.configmannagers.DiscordConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DiscordHttp {

    private final String botToken;
    private final String channelId;
    private final OkHttpClient client;
    private boolean enabled; // Стан бота
    private final DiscordConfig config; // Конфігурація Discord

    private final Map<String, Boolean> playerMessageStatus = new HashMap<>();


    // Конструктор для ініціалізації через DiscordConfig
    public DiscordHttp(DiscordConfig config) {
        this.botToken = config.getToken();
        this.channelId = config.getChannelId();
        this.client = new OkHttpClient();
        this.enabled = config.isEnabled();
        this.config = config;
    }

    public void sendSetBirthdayMessage(String player, String date) {
        if (!enabled) {
            return;
        }

        // Форматування дати у більш простий формат
        String formattedDate = formatDate(date);

        String title = config.getSetBirthdayTitle().replace("%player%", player);
        String description = String.join("\n", config.getSetBirthdayMessage())
                .replace("%player%", player)
                .replace("%date%", formattedDate)
                .replace("\n", "\\n"); // Заміняємо символи нового рядка


        int color = parseColor(config.getSetBirthdayColor());



        sendEmbedMessage(title, description, color);
    }

    // Відправлення повідомлення "Happy Birthday"
    public void sendHappyBirthdayMessage(String player) {
        // Перевірка, чи повідомлення вже було відправлено
        if (playerMessageStatus.getOrDefault(player, false)) {

            return; // Якщо повідомлення вже відправлено, не відправляємо знову
        }

        if (!enabled) {
            System.err.println("Discord-бот вимкнений. Повідомлення не буде відправлено.");
            return;
        }

        // Формування заголовку та опису для повідомлення
        String title = config.getHappyBirthdayTitle().replace("%player%", player);
        String description = String.join("\n", config.getHappyBirthdayMessage())
                .replace("%player%", player);

        int color = parseColor(config.getHappyBirthdayColor());



        // Відправка повідомлення через Discord
        sendEmbedMessage(title, description, color);

        // Оновлення статусу відправки повідомлення для цього гравця
        playerMessageStatus.put(player, true);
    }

    public void resetPlayerMessageStatus(String playerName) {
        playerMessageStatus.put(playerName, false);  // Скидаємо статус відправлення
    }

    // Відправлення повідомлення "Admin Delete Birthday"
    public void sendAdminDeleteBirthdayMessage(String adminPlayer, String targetPlayer) {
        if (!enabled) {
            return;
        }

        String title = config.getAdminDeleteBirthdayTitle().replace("%target_player%", targetPlayer);
        String description = String.join("\n", config.getAdminDeleteBirthdayMessage())
                .replace("%player%", adminPlayer)
                .replace("%target_player%", targetPlayer);

        int color = parseColor(config.getAdminDeleteBirthdayColor());
        sendEmbedMessage(title, description, color);
    }

    // Приватний метод для відправлення вбудованого повідомлення
    private void sendEmbedMessage(String title, String description, int color) {
        String embedJson = String.format("""
                {
                  "embeds": [
                    {
                      "title": "%s",
                      "description": "%s",
                      "color": %d
                    }
                  ]
                }
                """, title, description, color);
        sendRequest(embedJson);
    }

    // Приватний метод для виконання HTTP-запиту
    private void sendRequest(String json) {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                json
        );

        Request request = new Request.Builder()
                .url("https://discord.com/api/v10/channels/" + channelId + "/messages")
                .post(body)
                .addHeader("Authorization", "Bot " + botToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Message sent successfully!");
            } else {
                System.err.println("Failed to send message. Error: " + response.code());
                System.err.println(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Перетворення кольору з HEX у ціле число
    private int parseColor(String hexColor) {
        try {
            return Integer.parseInt(hexColor.replace("#", ""), 16);
        } catch (NumberFormatException e) {
            System.err.println("Invalid color format: " + hexColor + ". Defaulting to white.");
            return 0xFFFFFF;
        }
    }
    private String formatDate(String date) {
        try {
            // Якщо дата у вигляді "2000-01-07", спробуємо перетворити в "07.01.2000"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = inputFormat.parse(date);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date; // Повертаємо оригінальну дату, якщо не вдалося відформатувати
        }
    }


    public void close() {
        if (client != null) {
            client.dispatcher().executorService().shutdown(); // Зупинка потоків
            client.connectionPool().evictAll();               // Очищення пулу з'єднань
        }
    }

}
