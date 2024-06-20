package club.sdcs.discordbot.discord;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.PrivateChannel;
import reactor.core.publisher.Mono;

public abstract class MessageListener {
    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().startsWith("!user"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (channel instanceof PrivateChannel) {
                        return processRegistration(eventMessage);
                    } else {
                        return Mono.empty();
                    }
                })

                .then();
    }

    public Mono<Void> processRegistration(Message eventMessage) {
        return Mono.just(eventMessage)
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("hello"))
                .then();
    } //end processRegistration()
}
