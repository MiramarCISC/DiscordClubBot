package club.sdcs.discordbot.discord.commands.slash;



import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MeetingCommand implements SlashCommand {
    @Override
    public String getName() {
        return "meeting";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return event.reply()
                .withContent("meeting!");
    }
}
