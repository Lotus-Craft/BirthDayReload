package org.referix.birthDayReload.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordService {
    private final DiscordSettings settings;
    private JDA jda;

    public DiscordService(DiscordSettings settings) {
        this.settings = settings;
    }

    public void start() {
        try {
            jda = JDABuilder.createDefault(settings.getToken()).build();
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException("Discord bot startup error: " + e.getMessage(), e);
        }
    }

    public void stop() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public JDA getJda() {
        if (jda == null) {
            throw new IllegalStateException("The bot is not running!");
        }
        return jda;
    }

    public DiscordSettings getSettings() {
        return settings;
    }
}
