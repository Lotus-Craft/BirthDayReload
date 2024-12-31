package org.referix.birthDayReload.discord;

import okhttp3.*;

import java.io.IOException;

public class DiscordHttp {

    private final String botToken;
    private final String channelId;
    private final OkHttpClient client;

    // Конструктор для ініціалізації токена та ID каналу
    public DiscordHttp(String botToken, String channelId) {
        this.botToken = botToken;
        this.channelId = channelId;
        this.client = new OkHttpClient();
    }

    public void sendEmbedMessage(String title, String description, int color) {
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
}
