package club.sdcs.discordbot.discord.command.prefix;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collection;

@Component
public class PrefixCommandListener {
    private final Collection<PrefixCommand> prefixCommands;

    public PrefixCommandListener(Collection<PrefixCommand> messagePrefixCommands, GatewayDiscordClient client) {
        this.prefixCommands = messagePrefixCommands;
        client.on(MessageCreateEvent.class, this::handle).subscribe();
    }

    public Mono<Void> handle(MessageCreateEvent event) {
        Message message = event.getMessage();
        return Flux.fromIterable(prefixCommands)
                .filter(prefixCommand -> message.getContent().startsWith(prefixCommand.getName()))
                .next()
                .flatMap(prefixCommand -> prefixCommand.handle(message));
    }
}
