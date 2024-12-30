package org.referix.birthDayReload.discord.services;

public class DiscordMessage {
    private final String title;
    private final String color;
    private final String[] message;

    public DiscordMessage(String title, String color, String[] message) {
        this.title = title;
        this.color = color;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    public String[] getMessage() {
        return message;
    }
}
