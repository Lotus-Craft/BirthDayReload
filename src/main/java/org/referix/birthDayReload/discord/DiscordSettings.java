package org.referix.birthDayReload.discord;

import org.referix.birthDayReload.utils.configmannagers.MessageManager;

public class DiscordSettings {
    private final String token;
    private final String channelId;
    private final boolean isEnabled;

    public DiscordSettings(MessageManager messageManager) {
        this.token = messageManager.DISCORD_TOKEN;
        this.channelId = messageManager.DISCORD_CHANNEL_ID;
        this.isEnabled = messageManager.DISCORD_ENABLED;

        if (token.isEmpty()) {
            throw new IllegalArgumentException("Discord token is not specified in the configuration!");
        }
        if (channelId.isEmpty()) {
            throw new IllegalArgumentException("Discord channel ID is not specified in the configuration!");
        }
    }

    public String getToken() {
        return token;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }
}
