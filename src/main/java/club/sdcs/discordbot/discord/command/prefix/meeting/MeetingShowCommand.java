package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MeetingShowCommand implements PrefixCommand {
    private final MeetingService meetingService;

    public MeetingShowCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "!meeting show";
    }

    @Override
    public String getDescription() {
        return "Shows details of a specific meeting. `!meeting show [meeting_id]`";
    }

    @Override
    public Mono<Void> handle(Message message) {
        String content = message.getContent();
        String[] parts = content.split(" ");

        if (parts.length < 3) {
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("Please provide a meeting ID. Usage: !meeting show [meeting_id]"))
                    .then();
        }

        String meetingIdStr = parts[2];
        long meetingId;

        try {
            meetingId = Long.parseLong(meetingIdStr);
        } catch (NumberFormatException e) {
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("Invalid meeting ID. Please provide a valid number."))
                    .then();

        }

        return message.getChannel()
                .flatMap(channel -> channel.createMessage(showMeeting(meetingId)))
                .then();
    }

    private MessageCreateSpec showMeeting(long meetingId) {
        Meeting meeting = meetingService.findMeetingById(meetingId);

        if (meeting == null) {
            return MessageCreateSpec.create().withContent("The meeting with specified value does not exist.");
        }
        return meeting.toDiscordFormatMessage();
    }
}
