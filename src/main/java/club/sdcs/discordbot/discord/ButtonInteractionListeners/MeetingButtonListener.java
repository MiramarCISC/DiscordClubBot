package club.sdcs.discordbot.discord.ButtonInteractionListeners;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.InteractionReplyEditSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MeetingButtonListener implements EventListener<ButtonInteractionEvent> {
    private final MeetingService meetingService;

    public MeetingButtonListener(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        String customId = event.getCustomId();

        if (customId.startsWith("agendaLink-button-")) {
            return handleAgendaLinkButton(event);
        }
        if (customId.startsWith("minutesLink-button-")) {
            return handleMinutesLinkButton(event);
        }
        return Mono.empty();
    }

    private Mono<Void> handleAgendaLinkButton(ButtonInteractionEvent event) {
        return event.reply("Please provide the agenda link:")
                .withEphemeral(true)
                .then(handleUserInput(event, "agenda"));
    }

    private Mono<Void> handleMinutesLinkButton(ButtonInteractionEvent event) {
        return event.reply("Please provide the minutes link:")
                .withEphemeral(true)
                .then(handleUserInput(event, "minutes"));
    }

    private Mono<Void> handleUserInput(ButtonInteractionEvent event, String linkType) {
        return event.getInteraction().getChannel().flatMap(channel ->
                channel.getClient().on(MessageCreateEvent.class)
                        .filter(msgEvent -> msgEvent.getMessage().getChannelId().equals(channel.getId()))
                        .next()
                        .flatMap(msgEvent -> {
                            String userInput = msgEvent.getMessage().getContent();

                            long meetingId = Long.parseLong(event.getCustomId().substring(linkType.equals("agenda") ? "agendaLink-button-".length() : "minutesLink-button-".length()));
                            Meeting meeting = meetingService.findMeetingById(meetingId);

                            if (linkType.equals("agenda")) {
                                meeting.setAgendaLink(userInput);
                            } else if (linkType.equals("minutes")) {
                                meeting.setMinutesLink(userInput);
                            }

                            meetingService.updateMeeting(meeting);

                            return event.editReply(InteractionReplyEditSpec.builder()
                                    .contentOrNull("Saved " + linkType + " link: " + userInput)
                                    .build());
                        })).then();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error("Unable to process {}", getEventType().getSimpleName(), error);
        return Mono.empty();
    }
}
