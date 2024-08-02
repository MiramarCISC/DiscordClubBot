package club.sdcs.discordbot.discord.listener.menu;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Nomination;
import club.sdcs.discordbot.service.NominationService;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.entity.poll.Poll;
import discord4j.core.object.entity.poll.PollAnswer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NominationSelectMenuListener implements EventListener<SelectMenuInteractionEvent> {
    private final NominationService nominationService;

    public NominationSelectMenuListener(NominationService nominationService) {
        this.nominationService = nominationService;
    }

    @Override
    public Class<SelectMenuInteractionEvent> getEventType() {
        return SelectMenuInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(SelectMenuInteractionEvent event) {
        String customId = event.getCustomId();

        // Check if the customId matches "select-nomination"
        if (customId.equals("select-nomination")) {
            return handleSelectMenu(event);
        }

        return Mono.empty();
    }

    // Handle the select menu interaction event
    private Mono<Void> handleSelectMenu(SelectMenuInteractionEvent event) {
        String selectedValue = event.getValues().getFirst();

        // Filter nominations based on selected role and check if they are seconded
        List<Nomination> nominations = nominationService.getAllNominations().stream()
                .filter(nomination -> nomination.getRole().toString().equalsIgnoreCase(selectedValue))
                .filter(nomination -> nomination.getSecond() != null)
                .collect(Collectors.toList());

        // Handle case when there are no nominations for the selected role
        if (nominations.isEmpty()) {
            return event.reply("No nominations for this role.").withEphemeral(true);
        }

        // Send a poll to Discord for the selected role
        return sendDiscordPoll(event, selectedValue, nominations)
                .then(event.getMessage().get().delete());
    }

    // Create and send a poll to Discord
    private Mono<Void> sendDiscordPoll(SelectMenuInteractionEvent event, String role, List<Nomination> nominations) {
        List<PollAnswer> pollAnswers = nominations.stream()
                .map(nomination -> PollAnswer.of(nomination.getNominee().getDiscordName()))
                .collect(Collectors.toList());

        return event.getInteraction().getChannel()
                .flatMap(channel -> channel.createPoll()
                        .withQuestion("Election for: " + role)
                        .withAnswers(pollAnswers)
                        .withAllowMultiselect(false)
                        .withDuration(1)
                        .withLayoutType(Poll.PollLayoutType.DEFAULT)
                )
                .flatMap(poll -> {
                    // Update nominations with the poll message ID to track/use poll message later
                    nominations.forEach(nomination -> {
                        nomination.setMessageId(poll.getId().asLong());
                        nominationService.updateNomination(nomination);
                    });
                    return Mono.empty();
                })
                .then();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
