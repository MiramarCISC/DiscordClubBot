package club.sdcs.discordbot.discord.command.slash.rollcall;

import club.sdcs.discordbot.discord.command.slash.SlashCommand;
import club.sdcs.discordbot.discord.command.slash.membership.EmbedUtils;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

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
        return event.getInteraction().getGuild()
                .flatMap(guild -> event.getInteraction().getMember()
                        .map(member -> handleRollCall(event, guild, member))
                        .orElseGet(() -> event.reply("An error has occurred. Please try again.")));
    }

    private Mono<Void> handleRollCall(ChatInputInteractionEvent event, Guild guild, Member member) {
        return isOfficer(member, guild).flatMap(isOfficer -> {
            if (!isOfficer) {
                return event.reply("You must be an officer to initiate roll call.");
            }

            List<Meeting> meetingList = meetingService.getMeetingsByStatus(Meeting.Status.ACTIVE);
            if (meetingList.isEmpty()) {
                return event.reply("Cannot initiate roll call. There is no current meeting active.");
            }

            meetingList.forEach(meeting -> meetingId = meeting.getMeetingId());

            Mono<EmbedCreateSpec> embedMessage = EmbedUtils.createEmbedMessage(
                    "Roll Call for Meeting: " + meetingService.findMeetingById(meetingId).getName(),
                    "React to this message to confirm that you are attending this meeting."
            );

            return embedMessage.flatMap(embed -> event.reply().withEmbeds(embed))
                    .then(event.getReply().flatMap(message -> message.addReaction(ReactionEmoji.unicode("✅"))));
        });
    }


    /**
     * checks if user who types command is officer
     * @param member takes in member
     * @return if member is officer or not
     */
    private Mono<Boolean> isOfficer(Member member, Guild guild) {
        return getOfficerRole(guild)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(optionalRole -> {
                    if (optionalRole.isEmpty()) {
                        return Mono.just(false);
                    }
                    Role officerRole = optionalRole.get();
                    return Mono.just(member.getRoleIds().contains(officerRole.getId()));
                });
    } //end isOfficer()

    /**
     * retrieves the officer role from the guild
     * @param guild the guild to retrieve the officer role from
     * @return officer role if found
     */
    private Mono<Role> getOfficerRole(Guild guild) {
        return guild.getRoles()
                .filter(role -> role.getName().equalsIgnoreCase("Officer"))
                .next();
    } //end getOfficerRole()

}
