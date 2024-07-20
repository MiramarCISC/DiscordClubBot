package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.discord.command.slash.membership.EmbedUtils;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MeetingLogCommand implements PrefixCommand {

    private final MeetingService meetingService;

    public MeetingLogCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "!meeting log";
    }

    @Override
    public String getDescription() {
        return "Gets log of all members who attended a specific meeting. `!meeting log [meeting_id]`";
    }

    @Override
    public Mono<Void> handle(Message message) {
        String[] content = message.getContent().split(" ");

        if (content.length < 3) {
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("Please provide a meeting ID. Usage: `!meeting log [meeting_id]`"))
                    .then();
        }

        long meetingId;

        try {
            meetingId = Long.parseLong(content[2]);
        } catch (NumberFormatException e) {
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("Invalid meeting ID. Please provide a valid number."))
                    .then();

        }

        List<User> users = meetingService.findMeetingById(meetingId).getUserAttendance();
        String userList = formatUserList(users);

        return EmbedUtils.createEmbedMessage(message, "Meeting Attendance Log For: " + meetingService.findMeetingById(meetingId).getName(),
                userList).then();

    }

    /**
     * formats user list
     * @param users takes in list of users attended
     * @return formatted user list
     */
    private String formatUserList(List<User> users) {
        if (users.isEmpty()) {
            return "No attendees.";
        }

        StringBuilder userList = new StringBuilder();
        for (User user : users) {
            userList.append("○ ").append(user.getFullName()).append(" (").append(user.getDiscordName()).append(")").append("\n");
        }
        return userList.toString();
    }
}
