package org.referix.birthDayReload.discord;

import org.referix.birthDayReload.discord.services.MessageService;

public class DiscordManager {
    private final DiscordService service;
    private final MessageService messageService;

    public DiscordManager(DiscordSettings settings) {
        this.service = new DiscordService(settings);
        this.messageService = new MessageService(service);
    }

    public void start() {
        service.start();
    }

    public void stop() {
        service.stop();
    }

    public MessageService getMessageService() {
        return messageService;
    }

}
