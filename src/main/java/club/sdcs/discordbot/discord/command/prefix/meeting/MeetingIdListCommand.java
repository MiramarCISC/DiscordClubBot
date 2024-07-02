package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;

@Component
public class MeetingIdListCommand implements PrefixCommand {

    private final MeetingService meetingService;

    public MeetingIdListCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "!meeting id";
    }

    @Override
    public String getDescription() {
        return "Lists IDs of all active and scheduled meetings.";
    }

    @Override
    public Mono<Void> handle(Message message) {
        List<Meeting.Status> statuses = Arrays.asList(Meeting.Status.ACTIVE, Meeting.Status.SCHEDULED);
        List<Meeting> meetings = meetingService.getMeetingsByStatuses(statuses);

        return message.getChannel()
                .flatMap(channel -> channel.createMessage(printList(meetings))
                        .then());
    }

    private String printList(List<Meeting> meetings) {
        StringBuilder list = new StringBuilder();
        for (Meeting meeting : meetings) {
            list.append(meeting.getName()).append(":\t`").append(meeting.getMeetingId()).
                    append("` \n");
        }
        return list.toString();
    }
}
