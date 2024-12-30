package org.referix.birthDayReload.utils.configmannagers;

import java.util.List;

public class EmbeddedMessage {
    private final String title;
    private final List<String> messages;

    public EmbeddedMessage(String title, List<String> messages) {
        this.title = title;
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String formatMessage(String player, String data, String wish) {
        String formattedTitle = title.replace("%player%", player);
        StringBuilder formattedMessages = new StringBuilder();

        for (String message : messages) {
            formattedMessages.append(
                    message.replace("%player%", player)
                            .replace("%data%", data)
                            .replace("%wish%", wish)
            ).append("\n");
        }

        return "**" + formattedTitle + "**\n" + formattedMessages.toString();
    }
}
