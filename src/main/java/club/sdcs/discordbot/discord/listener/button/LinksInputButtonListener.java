package club.sdcs.discordbot.discord.listener.button;

import club.sdcs.discordbot.discord.EventListener;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.spec.InteractionPresentModalSpec;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LinksInputButtonListener implements EventListener<ButtonInteractionEvent> {

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        return showModal(event);
    }

    private Mono<Void> showModal(ButtonInteractionEvent event) {
        String meetingId = event.getCustomId();

        InteractionPresentModalSpec modalSpec = InteractionPresentModalSpec.builder()
                .title("Enter links")
                .customId(meetingId)
                .addComponent(ActionRow.of(
                        TextInput.small(meetingId + "agenda", "Agenda link:")
                ))
                .addComponent(ActionRow.of(
                        TextInput.small(meetingId + "minutes", "Minutes link:")
                ))
                .build();
        return event.presentModal(modalSpec);
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error("Unable to process {}", getEventType().getSimpleName(), error);
        return Mono.empty();
    }
}
