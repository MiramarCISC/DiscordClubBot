package club.sdcs.discordbot.discord.commands.prefix;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class MeetingListCommand implements PrefixCommand {
    private MeetingService meetingService;

    public MeetingListCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "!meeting list";
    }

    @Override
    public Mono<Void> handle(Message message) {
        List<Meeting.Status> statuses = Arrays.asList(Meeting.Status.ACTIVE, Meeting.Status.SCHEDULED);
        List<Meeting> meetings = meetingService.getMeetingsByStatuses(statuses);
        return message.getChannel()
                .flatMapMany(channel -> Flux.fromIterable(meetings)
                        .flatMap(meeting -> channel.createMessage(MessageCreateSpec.builder()
                                .addEmbed(meeting.toDiscordFormatEmbed())
                                .build())))
                .then();
    }
}
