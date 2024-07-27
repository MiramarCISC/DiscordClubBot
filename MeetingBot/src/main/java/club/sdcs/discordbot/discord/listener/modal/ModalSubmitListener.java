package club.sdcs.discordbot.discord.listener.modal;

import club.sdcs.discordbot.discord.listener.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Listener for handling InteractionPresentModalSpec modal
 * @see club.sdcs.discordbot.discord.listener.button.LinksInputButtonListener
 */
@Component
public class ModalSubmitListener implements EventListener<ModalSubmitInteractionEvent> {
    private final MeetingService meetingService;

    public ModalSubmitListener(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public Class<ModalSubmitInteractionEvent> getEventType() {
        return ModalSubmitInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ModalSubmitInteractionEvent event) {
        String customId = event.getCustomId();

        // Retrieve previous links in case user input is left empty
        Meeting meeting = meetingService.findMeetingById(Long.parseLong(customId));

        // Retrieve previous links in case user input is left empty
        String agendaLink = meeting.getAgendaLink();
        String minutesLink = meeting.getMinutesLink();

        // Unliked way of retrieving modal values
        for (TextInput component : event.getComponents(TextInput.class)) {
            String inputId = component.getCustomId();
            String value = component.getValue().orElse("").trim();

            // Ensures no unwanted overwriting of link string
            if (!value.isEmpty()) {
                if ((customId + "agenda").equals(inputId)) {
                    agendaLink = value;
                } else if ((customId + "minutes").equals(inputId)) {
                    minutesLink = value;
                }
            }
        }

        return updateMeetingDetails(customId, agendaLink, minutesLink)
                .then(event.reply("The links have been updated successfully!"));
    }

    private Mono<Void> updateMeetingDetails(String meetingId, String agendaLink, String minutesLink) {
        Meeting meeting = meetingService.findMeetingById(Long.parseLong(meetingId));

        meeting.setAgendaLink(agendaLink);
        meeting.setMinutesLink(minutesLink);

        meetingService.updateMeeting(meeting);

        return Mono.empty();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error("Error processing modal submission: {}", error.getMessage());
        return Mono.empty();
    }
}
