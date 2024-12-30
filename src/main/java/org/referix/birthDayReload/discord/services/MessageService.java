package org.referix.birthDayReload.discord.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.referix.birthDayReload.discord.DiscordService;

import java.awt.Color;

public class MessageService {
    private final DiscordService service;

    public MessageService(DiscordService service) {
        this.service = service;
    }

    public void sendEmbed(DiscordMessage discordMessage) {
        String channelId = service.getSettings().getChannelId();
        TextChannel channel = service.getJda().getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Канал с ID " + channelId + " не найден!");
        }

        // Создаем Embed сообщение
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(discordMessage.getTitle()) // Заголовок
                .setColor(parseColor(discordMessage.getColor())); // Цвет

        // Добавляем текст сообщения как описание
        String description = String.join("\n", discordMessage.getMessage());
        embedBuilder.setDescription(description.trim()); // Убираем лишние переносы строки

        // Отправка Embed сообщения
        channel.sendMessageEmbeds(embedBuilder.build()).queue(
                success -> System.out.println("Embed сообщение успешно отправлено: " + discordMessage.getTitle()),
                error -> System.err.println("Ошибка отправки Embed сообщения: " + error.getMessage())
        );
    }


    private Color parseColor(String color) {
        try {
            return (Color) Color.class.getField(color.toUpperCase()).get(null);
        } catch (Exception e) {
            return Color.WHITE; // Default color
        }
    }
}
