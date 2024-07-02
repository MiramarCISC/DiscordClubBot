package club.sdcs.discordbot.discord.command.prefix;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DirectMessageCommand implements PrefixCommand {
    @Override
    public String getName() {
        return "!dm";
    }

    @Override
    public String getDescription() {
        return "Direct messages command's author.";
    }

    @Override
    public Mono<Void> handle(Message message) {
        return message.getAuthor()
                .map(user -> user.getPrivateChannel()
                        .flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
                                .content("Hello!  `!help`  to list commands.")
                                .build())))
                .orElse(Mono.empty()).then();
    }
}
