package club.sdcs.discordbot.discord.command.prefix;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class HelpCommand implements PrefixCommand {
    private final Collection<PrefixCommand> prefixCommands;

    public HelpCommand(Collection<PrefixCommand> prefixCommands) {
        this.prefixCommands = prefixCommands;
    }

    @Override
    public String getName() {
        return "!help";
    }

    @Override
    public String getDescription() {
        return "Displays a list of available commands and their descriptions.";
    }

    @Override
    public Mono<Void> handle(Message message) {
        String commandList = prefixCommands.stream()
                .map(cmd -> "`" + cmd.getName() + "` - " + cmd.getDescription())
                .collect(Collectors.joining("\n"));

        return message.getChannel()
                .flatMap(channel -> channel.createMessage("Commands:\n" + commandList))
                .then();
    }
}
