package club.sdcs.discordbot.discord.commands.prefix;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface PrefixCommand {
    String getName();
    Mono<Void> handle(Message message);
}
