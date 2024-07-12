package club.sdcs.discordbot.discord.command.prefix.meeting;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.service.MeetingManager;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MeetingRemindCommand implements PrefixCommand {
    private final MeetingManager meetingManager;

    public MeetingRemindCommand(MeetingManager meetingManager) {
        this.meetingManager = meetingManager;
    }

    @Override
    public String getName() {
        return "!meeting remind";
    }

    @Override
    public String getDescription() {
        return "Manually sends meeting reminders";
    }

    @Override
    public Mono<Void> handle(Message message) {
        return meetingManager.checkMeetings().flatMap(hasIncompleteLinks -> {
            if (hasIncompleteLinks) {
                return Mono.empty();
            } else {
                return message.getChannel().flatMap(channel ->
                        channel.createMessage("There are no meetings with incomplete agenda or minutes links.")
                ).then();
            }
        });
    }
}
