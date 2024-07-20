package club.sdcs.discordbot.discord.listener.reaction;

import club.sdcs.discordbot.discord.command.slash.rollcall.RollCallCommand;
import club.sdcs.discordbot.discord.listener.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.MeetingService;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.message.ReactionAddEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactionAddListener implements EventListener<ReactionAddEvent> {

    private final MeetingService meetingService;
    private final UserService userService;

    public ReactionAddListener(MeetingService meetingService, UserService userService) {
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @Override
    public Class<ReactionAddEvent> getEventType() {
        return ReactionAddEvent.class;
    }

    @Override
    public Mono<Void> execute(ReactionAddEvent event) {

        String eventId = event.getEmoji().asUnicodeEmoji().get().getRaw();

        if (eventId.equals("✅")) {
            return event.getUser()
                    .flatMap(user -> {
                        if (user.isBot()) {
                            return Mono.empty();
                        }
                        return handleReactionAdded(event);
                    });
        } else {
            return event.getMessage()
                    .flatMap(message -> message.removeReaction(event.getEmoji(), event.getUserId()));
        }

    }

    /**
     * handles reaction add
     * @param event the reaction
     * @return either bot message or nothing
     */
    private Mono<Void> handleReactionAdded(ReactionAddEvent event) {

        return event.getUser().flatMap(user -> {
            User dbUser = userService.getUserByDiscordId(event.getUserId().asLong());
            if (dbUser == null) {
                return event.getMessage()
                        .flatMap(message -> message.removeReaction(event.getEmoji(), event.getUserId()))
                        .then(user.getPrivateChannel()
                                .flatMap(privateChannel -> privateChannel.createMessage("You have not registered to the club, therefore cannot contribute to roll call.")))
                        .then();
            }

            Meeting meeting = meetingService.findMeetingById(RollCallCommand.meetingId);
            meeting.addUserToMeeting(dbUser);
            meetingService.updateMeeting(meeting);

            return Mono.empty();
        }); //end handleReaction()
    }
}
