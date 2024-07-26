package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MeetingListCommand extends AbstractMeetingListCommand {

    public MeetingListCommand(MeetingService meetingService) {
        super(meetingService);
    }

    @Override
    protected String getCommandName() {
        return "!meeting list";
    }

    @Override
    protected String getCommandDescription() {
        return "Lists detail of all active and scheduled meetings.";
    }

    @Override
    protected Mono<Void> handleMeeting(Message message, List<Meeting> meetings) {
        return message.getChannel().flatMap(channel ->
                Flux.fromIterable(meetings)
                        .flatMap(meeting -> channel.createMessage(meeting.toDiscordFormatMessage()))
                        .then()
        );
    }
}
