package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractMeetingListCommand implements PrefixCommand {
    protected final MeetingService meetingService;

    public AbstractMeetingListCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    protected abstract String getCommandName();

    protected abstract String getCommandDescription();

    protected abstract Mono<Void> handleMeeting(Message message, List<Meeting> meetings);

    @Override
    public String getName() {
        return getCommandName();
    }

    @Override
    public String getDescription() {
        return getCommandDescription();
    }

    @Override
    public Mono<Void> handle(Message message) {
        List<Meeting.Status> statuses = Arrays.asList(Meeting.Status.ACTIVE, Meeting.Status.SCHEDULED, Meeting.Status.COMPLETED);
        List<Meeting> meetings = meetingService.getMeetingsByStatuses(statuses);
        return message.getChannel()
                .flatMap(channel -> {
                    if (meetings.isEmpty()) {
                        return channel.createMessage("No meetings found.");
                    } else {
                        return handleMeeting(message, meetings);
                    }
                }).then();
    }
}
