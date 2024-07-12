package club.sdcs.discordbot.discord.listener.ScheduledEvent;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.guild.ScheduledEventCreateEvent;
import discord4j.core.object.entity.ScheduledEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.common.util.Snowflake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Listener for handling creation of server events
 */
@Component
public class ScheduledEventCreateListener implements EventListener<ScheduledEventCreateEvent> {
    private final MeetingService meetingService;

    @Value("${spring.discord.officer-channel-id}")
    private String CHANNEL_ID;
    @Value("${spring.discord.zone-id}")
    private String ZONE_ID;

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
        meeting.setQuorumMet(false); // default value
        meeting.setStatus(scheduledEvent.getStatus().getValue());
        return Mono.fromCallable(() -> meetingService.addMeeting(meeting)).thenReturn(meeting);
    }

    private Mono<Void> sendScheduledEventCreateMessage(ScheduledEventCreateEvent event, Meeting meeting) {
        return event.getGuild()
                .flatMap(guild -> guild.getChannelById(Snowflake.of(CHANNEL_ID))
                        .ofType(MessageChannel.class))
                .flatMap(channel -> channel.createMessage(meeting.toDiscordFormatEmbed()))
                .then();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
