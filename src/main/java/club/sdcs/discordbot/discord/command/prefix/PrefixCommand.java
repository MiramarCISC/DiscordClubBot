package club.sdcs.discordbot.discord.command.prefix;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface PrefixCommand {
    String getName();
    String getDescription();
    Mono<Void> handle(Message message);
}
