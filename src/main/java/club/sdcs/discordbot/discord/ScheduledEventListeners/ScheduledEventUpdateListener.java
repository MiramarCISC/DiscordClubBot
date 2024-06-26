package club.sdcs.discordbot.discord.ScheduledEventListeners;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.ScheduledEventUpdateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.ZoneId;
import java.time.LocalDateTime;

@Component
public class ScheduledEventUpdateListener implements EventListener<ScheduledEventUpdateEvent> {
    private final MeetingService meetingService;

    @Value("${spring.discord.officer-channel-id}")
    private String CHANNEL_ID;
    @Value("${spring.discord.zone-id}")
    private String ZONE_ID;
    public ScheduledEventUpdateListener(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public Class<ScheduledEventUpdateEvent> getEventType() {
        return ScheduledEventUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(ScheduledEventUpdateEvent event) {
        return processEvent(event);
    }

    public Mono<Void> processEvent(ScheduledEventUpdateEvent event) {
        return updatedMeeting(event)
                .flatMap(meeting -> sendScheduledEventUpdateMessage(event, meeting));
    }

    private Mono<Meeting> updatedMeeting(ScheduledEventUpdateEvent updatedEvent) {
        return Mono.justOrEmpty(updatedEvent.getOld())
                .flatMap(oldScheduledEvent -> {
                    Meeting meeting = meetingService.findMeetingById(oldScheduledEvent.getId().asLong());
                    return Mono.justOrEmpty(updatedEvent.getCurrent())
                            .flatMap(currentScheduledEvent -> {
                                meeting.setMeetingId(currentScheduledEvent.getId().asLong());
                                meeting.setName(currentScheduledEvent.getName());
                                meeting.setDescription(currentScheduledEvent.getDescription().orElse("No description"));
                                meeting.setLocation(currentScheduledEvent.getLocation().orElse("No location"));
                                meeting.setStartTime(LocalDateTime.ofInstant(currentScheduledEvent.getStartTime(), ZoneId.of(ZONE_ID)));
                                currentScheduledEvent.getEndTime().ifPresent(endTime ->
                                        meeting.setEndTime(LocalDateTime.ofInstant(endTime, ZoneId.of(ZONE_ID))));
                                meeting.setStatus(currentScheduledEvent.getStatus().getValue());

                                return Mono.fromRunnable(() -> meetingService.updateMeeting(meeting)).thenReturn(meeting);
                            });
                });
    }

    private Mono<Void> sendScheduledEventUpdateMessage(ScheduledEventUpdateEvent event, Meeting meeting) {
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