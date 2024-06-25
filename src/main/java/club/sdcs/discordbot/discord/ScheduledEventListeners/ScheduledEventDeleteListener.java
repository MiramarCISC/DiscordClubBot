package club.sdcs.discordbot.discord.ScheduledEventListeners;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.guild.ScheduledEventDeleteEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ScheduledEventDeleteListener implements EventListener<ScheduledEventDeleteEvent> {
    private final MeetingService meetingService;

    public ScheduledEventDeleteListener(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public Class<ScheduledEventDeleteEvent> getEventType() {
        return ScheduledEventDeleteEvent.class;
    }

    @Override
    public Mono<Void> execute(ScheduledEventDeleteEvent event) {
        return processEvent(event);
    }

    public Mono<Void> processEvent(ScheduledEventDeleteEvent event) {
        Meeting meeting = meetingService.findMeetingById(event.getScheduledEvent().getId().asLong());
        meeting.setStatus(4); // CANCELLED status
        meetingService.updateMeeting(meeting);

        return Mono.empty();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
