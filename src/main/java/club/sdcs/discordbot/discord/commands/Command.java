package club.sdcs.discordbot.discord.commands;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface Command {
    String getName();
    Mono<Void> handle(Message message);
}
