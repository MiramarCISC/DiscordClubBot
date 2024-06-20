package club.sdcs.discordbot.discord;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.guild.ScheduledEventCreateEvent;
import discord4j.core.object.entity.ScheduledEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ScheduledEventCreateListener implements EventListener<ScheduledEventCreateEvent> {

    private final MeetingService meetingService;

    public ScheduledEventCreateListener(MeetingService meetingService, UserService userService) {
        this.meetingService = meetingService;
    }

    @Override
    public Class<ScheduledEventCreateEvent> getEventType() {
        return ScheduledEventCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(ScheduledEventCreateEvent event) {
        return processEvent(event);
    }

    public Mono<Void> processEvent(ScheduledEventCreateEvent event) {
        ScheduledEvent scheduledEvent = event.getScheduledEvent();
        Meeting meeting = new Meeting();
        meeting.setMeetingId(scheduledEvent.getId().asLong());
        meeting.setName(scheduledEvent.getName());
        meeting.setDescription(scheduledEvent.getDescription().orElse("No description"));
        meeting.setLocation(scheduledEvent.getLocation().orElse("No location"));
        // TODO: Parse time correctly
        //meeting.setTimeStart(LocalTime.from(scheduledEvent.getStartTime()));
        //meeting.setTimeEnd(scheduledEvent.getEndTime().map(LocalTime::from).orElse(null));
        meeting.setQuorumMet(false);
        meeting.setStatus(scheduledEvent.getStatus().getValue());

        return Mono.fromCallable(() -> meetingService.addMeeting(meeting)).then();
    }

    // TODO: Send channel and direct message notification upon creation of meeting
    public Mono<Void> sendEventCreatedMessage(Meeting meeting) {
        return null;
    }
}
