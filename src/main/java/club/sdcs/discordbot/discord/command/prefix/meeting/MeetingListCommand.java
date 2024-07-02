package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;

@Component
public class MeetingListCommand implements PrefixCommand {
    private final MeetingService meetingService;

    public MeetingListCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "!meeting list";
    }

    @Override
    public String getDescription() {
        return "Lists detail of all active and scheduled meetings.";
    }

    @Override
    public Mono<Void> handle(Message message) {
        List<Meeting.Status> statuses = Arrays.asList(Meeting.Status.ACTIVE, Meeting.Status.SCHEDULED);
        List<Meeting> meetings = meetingService.getMeetingsByStatuses(statuses);
        return message.getChannel()
                .flatMap(channel -> {
                    if (meetings.isEmpty()) {
                        return channel.createMessage("No active or scheduled meetings.");
                    } else {
                        return Flux.fromIterable(meetings)
                                .flatMap(meeting -> channel.createMessage(meeting.toDiscordFormatMessage()))
                                .then();
                    }
                }).then();
    }
}
