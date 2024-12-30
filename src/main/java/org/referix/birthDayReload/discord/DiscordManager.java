package org.referix.birthDayReload.discord;

import org.referix.birthDayReload.discord.services.MessageService;

public class DiscordManager {
    private final DiscordService service;
    private final MessageService messageService;

    public DiscordManager(DiscordService service) {
        this.service = service;
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
