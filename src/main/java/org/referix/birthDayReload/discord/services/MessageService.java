package org.referix.birthDayReload.discord.services;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.referix.birthDayReload.discord.DiscordService;

public class MessageService {
    private final DiscordService service;

    public MessageService(DiscordService service) {
        this.service = service;
    }

    public void sendMessage(String message) {
        String channelId = service.getSettings().getChannelId();
        TextChannel channel = service.getJda().getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Канал с ID " + channelId + " не найден!");
        }

        channel.sendMessage(message).queue(
                success -> System.out.println("Сообщение успешно отправлено: " + message),
                error -> System.err.println("Ошибка отправки сообщения: " + error.getMessage())
        );
    }
}
