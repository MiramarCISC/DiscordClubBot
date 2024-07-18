package club.sdcs.discordbot.discord.command.slash.rollcall;

import club.sdcs.discordbot.discord.command.slash.SlashCommand;
import club.sdcs.discordbot.discord.command.slash.membership.EmbedUtils;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RollCallCommand implements SlashCommand {

    private final MeetingService meetingService;
    private long meetingId;

    public RollCallCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "rollcall";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {

        List<Meeting> meetingList = meetingService.getMeetingsByStatus(Meeting.Status.ACTIVE);
        meetingList.forEach(meeting -> {
            meetingId = meeting.getMeetingId();
        });

        Mono<EmbedCreateSpec> embedMessage = EmbedUtils.createEmbedMessage(
                "Roll Call for Meeting: " + meetingService.findMeetingById(meetingId).getName(),
                "React to this message to confirm that you are attending this meeting."
        );

        //TODO: check for if user is officer
        //TODO: create a meeting attendance log
        //TODO: add reaction to embed that upon user interaction saves/removes user from meeting attendance log

        return embedMessage.flatMap(embed -> event.reply().withEmbeds(embed));
    }

}
