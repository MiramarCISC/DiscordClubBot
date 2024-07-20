package club.sdcs.discordbot.discord.command.slash.rollcall;

import club.sdcs.discordbot.discord.command.slash.SlashCommand;
import club.sdcs.discordbot.discord.command.slash.membership.EmbedUtils;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class RollCallCommand implements SlashCommand {

    private final MeetingService meetingService;
    public static long meetingId;

    public RollCallCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "rollcall";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {

        //TODO: check for if user is officer
        List<Meeting> meetingList = meetingService.getMeetingsByStatus(Meeting.Status.ACTIVE);

        if (meetingList.isEmpty()) {
            return event.reply("Cannot initiate roll call. There is no current meeting active.");
        }

        meetingList.forEach(meeting -> {
            meetingId = meeting.getMeetingId();
        });


        Mono<EmbedCreateSpec> embedMessage = EmbedUtils.createEmbedMessage(
                "Roll Call for Meeting: " + meetingService.findMeetingById(meetingId).getName(),
                "React to this message to confirm that you are attending this meeting."
        );

        return embedMessage.flatMap(embed -> event.reply().withEmbeds(embed))
                .then(event.getReply().flatMap(message -> message.addReaction(ReactionEmoji.unicode("✅"))));
    }

}
