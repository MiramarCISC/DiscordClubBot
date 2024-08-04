package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MeetingIdListCommand extends AbstractMeetingListCommand {

    public MeetingIdListCommand(MeetingService meetingService) {
        super(meetingService);
    }

    @Override
    protected String getCommandName() {
        return "!meeting id";
    }

    @Override
    protected String getCommandDescription() {
        return "Lists IDs of all active and scheduled meetings.";
    }

    @Override
    protected Mono<Void> handleMeeting(Message message, List<Meeting> meetings) {
        return message.getChannel().flatMap(channel ->
                channel.createMessage(printList(meetings)).then()
        );
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