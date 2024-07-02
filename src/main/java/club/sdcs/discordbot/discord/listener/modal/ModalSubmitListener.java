package club.sdcs.discordbot.discord.listener.modal;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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

        String agendaLink = "";
        String minutesLink = "";

        for (TextInput component : event.getComponents(TextInput.class)) {
            String value = component.getValue().orElse("");
            if ((customId + "agenda").equals(component.getCustomId())) {
                agendaLink = value;
            } else if ((customId + "minutes").equals(component.getCustomId())) {
                minutesLink = value;
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
