package club.sdcs.discordbot.discord.commands;



import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class CommandListener {

    private final Collection<Command> commands;

    public CommandListener(Collection<Command> messageCommands, GatewayDiscordClient client) {
        this.commands = messageCommands;
        client.on(MessageCreateEvent.class, this::handle).subscribe();
    }

    public Mono<Void> handle(MessageCreateEvent event) {
        Message message = event.getMessage();
        return Flux.fromIterable(commands)
                .filter(command -> message.getContent().startsWith(command.getName()))
                .next()
                .flatMap(command -> command.handle(message));
    }
}
