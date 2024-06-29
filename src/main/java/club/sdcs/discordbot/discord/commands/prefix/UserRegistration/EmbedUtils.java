package club.sdcs.discordbot.discord.commands.prefix.UserRegistration;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class EmbedUtils {

    /**
     * creates an embed for the bot
     * @param message takes in message
     * @param title takes in title for embed
     * @param description takes in description for embed
     * @return created embed
     */
    public static Mono<Void> createEmbedMessage(Message message, String title, String description) {
        return message.getChannel()
                .flatMap(channel -> createEmbedMessage(channel, title, description));
    }

    /**
     * creates an embed message for the bot
     * @param messageChannel takes in message channel
     * @param title takes in title for embed
     * @param description takes in description for embed
     * @return created embed
     */
    public static Mono<Void> createEmbedMessage(MessageChannel messageChannel, String title, String description) {
        return messageChannel.createMessage(
                        EmbedCreateSpec.builder()
                                .title(title)
                                .description(description)
                                .color(Color.SUMMER_SKY)
                                .build()
                )
                .then();
    } //end createEmbedMessage()
}
