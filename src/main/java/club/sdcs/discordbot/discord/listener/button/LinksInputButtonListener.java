package club.sdcs.discordbot.discord.listener.button;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Meeting;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.spec.InteractionPresentModalSpec;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Listener for handling Meeting's MessageCreateSpec button
 * @see Meeting#toDiscordFormatMessage()
 */
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
                // customId prevents conflict of reference
                .addComponent(ActionRow.of(TextInput.small(meetingId + "agenda", "Agenda link:").required(false)))
                .addComponent(ActionRow.of(TextInput.small(meetingId + "minutes", "Minutes link:").required(false)))
                .build();
        return event.presentModal(modalSpec);
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
