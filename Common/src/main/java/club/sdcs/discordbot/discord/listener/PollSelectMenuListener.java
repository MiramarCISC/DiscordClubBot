package club.sdcs.discordbot.discord.listener;

import club.sdcs.discordbot.model.DiscordPoll;
import club.sdcs.discordbot.service.DiscordPollService;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PollSelectMenuListener implements EventListener<SelectMenuInteractionEvent> {
    private final DiscordPollService discordPollService;

    public PollSelectMenuListener(DiscordPollService discordPollService) {
        this.discordPollService = discordPollService;
    }

    @Override
    public Class<SelectMenuInteractionEvent> getEventType() {
        return SelectMenuInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(SelectMenuInteractionEvent event) {
        String customId = event.getCustomId();

        if (customId.startsWith("poll-select-menu-")) {
            handleSelectMenu(event);
            return event.getMessage().get().delete();
        }

        return Mono.empty();
    }

    // Sets DiscordPoll's PassCondition
    private void handleSelectMenu(SelectMenuInteractionEvent event) {
        String customId = event.getCustomId();
        String pollIdString = customId.substring("poll-select-menu-".length());
        long pollId = Long.parseLong(pollIdString);

        String selectedValue = event.getValues().getFirst();

        DiscordPoll discordPoll = discordPollService.findDiscordPollById(pollId);

        if (discordPoll == null) return;

        discordPoll.setPassCondition(DiscordPoll.PassCondition.valueOf(selectedValue));
        discordPollService.updateDiscordPoll(discordPoll);

    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
