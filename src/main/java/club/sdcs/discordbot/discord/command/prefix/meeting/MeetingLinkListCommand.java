package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MeetingLinkListCommand extends AbstractMeetingListCommand {

    public MeetingLinkListCommand(MeetingService meetingService) {
        super(meetingService);
    }

    @Override
    protected String getCommandName() {
        return "!meeting link";
    }

    @Override
    protected String getCommandDescription() {
        return "Lists all active and scheduled meetings with their agenda and minutes links.";
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
            String agendaLink = "\tAgenda: <" + meeting.getAgendaLink() + ">";
            String minutesLink = "\tMinutes: <" + meeting.getMinutesLink() + ">";
            String name = meeting.getName() + ":";

            list.append(name).append("\n").append(agendaLink).append("\n").append(minutesLink).append("\n");
        }
        return list.toString();
    }
}
