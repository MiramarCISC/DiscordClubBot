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

        Optional<String> meetingIdOptional = event.getOption("meeting_id")
                .flatMap(option -> option.getValue().map(value -> value.asString()));

        return event.getInteraction().getGuild()
                .flatMap(guild -> event.getInteraction().getMember()
                        .map(member -> handleRollCall(event, guild, member, meetingIdOptional))
                        .orElseGet(() -> event.reply("An error has occurred. Please try again.")));
    }

    private Mono<Void> handleRollCall(ChatInputInteractionEvent event, Guild guild, Member member, Optional<String> meetingIdOptional) {
        return isOfficer(member, guild).flatMap(isOfficer -> {
            if (!isOfficer) {
                return event.reply("You must be an officer to initiate roll call.");
            }

            if (meetingIdOptional.isPresent()) {
                meetingId = Long.parseLong(meetingIdOptional.get());

            } else {
                List<Meeting> meetingList = meetingService.getMeetingsByStatus(Meeting.Status.ACTIVE);
                if (meetingList.isEmpty()) {
                    return event.reply("Cannot initiate roll call. No meeting active.").withEphemeral(true);
                }
                meetingId = meetingList.getFirst().getMeetingId();

            }
            return handleRollCallForMeeting(event, meetingId);
        });
    }

    private Mono<Void> handleRollCallForMeeting(ChatInputInteractionEvent event, long meetingId) {

        if (!meetingService.findMeetingById(meetingId).getStatus().equals(Meeting.Status.ACTIVE)) {
            return event.reply("Cannot initiate roll call. The meeting is not active.").withEphemeral(true);
        }

        String meetingName;

        try {
            meetingName = meetingService.findMeetingById(meetingId).getName();
        } catch (Exception e) {
            return event.reply("That meeting does not exist or has not been started.").withEphemeral(true);
        }

        Mono<EmbedCreateSpec> embedMessage = EmbedUtils.createEmbedMessage(
                "Roll Call for Meeting: " + meetingName,
                "React to this message to confirm that you are attending this meeting."
        );

        return embedMessage.flatMap(embed -> event.reply().withEmbeds(embed))
                .then(event.getReply().flatMap(message -> message.addReaction(ReactionEmoji.unicode("✅"))));
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
