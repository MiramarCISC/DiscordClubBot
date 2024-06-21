package club.sdcs.discordbot.discord;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.guild.ScheduledEventCreateEvent;
import discord4j.core.object.entity.ScheduledEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.common.util.Snowflake;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class ScheduledEventCreateListener implements EventListener<ScheduledEventCreateEvent> {
    private final MeetingService meetingService;
    private final String CHANNEL_ID = "1209001365234393091"; // Set announcement channel ID here
    private final String ZONE_ID = "America/Los_Angeles";

    public ScheduledEventCreateListener(MeetingService meetingService) {
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
        return saveMeeting(event.getScheduledEvent())
                .flatMap(meeting -> sendScheduledEventCreateMessage(event, meeting));
    }

    private Mono<Meeting> saveMeeting(ScheduledEvent scheduledEvent) {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(scheduledEvent.getId().asLong());
        meeting.setName(scheduledEvent.getName());
        meeting.setDescription(scheduledEvent.getDescription().orElse("No description"));
        meeting.setLocation(scheduledEvent.getLocation().orElse("No location"));
        meeting.setStartTime(LocalDateTime.ofInstant(scheduledEvent.getStartTime(), ZoneId.of(ZONE_ID)));
        if (scheduledEvent.getEndTime().isPresent()) meeting.setEndTime(LocalDateTime.ofInstant(scheduledEvent.getEndTime().get(), ZoneId.of(ZONE_ID)));
        meeting.setQuorumMet(false);
        meeting.setStatus(scheduledEvent.getStatus().getValue());
        return Mono.fromCallable(() -> meetingService.addMeeting(meeting)).thenReturn(meeting);
    }

    private Mono<Void> sendScheduledEventCreateMessage(ScheduledEventCreateEvent event, Meeting meeting) {
        return event.getGuild()
                .flatMap(guild -> guild.getChannelById(Snowflake.of(CHANNEL_ID))
                        .ofType(MessageChannel.class))
                .flatMap(channel -> channel.createMessage(meeting.toString())) // TODO: return more refined string/message
                .then();
    }
}
