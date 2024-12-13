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
                        Meeting meeting = meetingService.findMeetingById(RollCallCommand.meetingId);

                        if (user.isBot()) {
                            return Mono.empty();
                        } else if (meeting.getStatus().equals(Meeting.Status.COMPLETED)) {
                            return event.getUser()
                                    .flatMap(discord4j.core.object.entity.User::getPrivateChannel)
                                    .flatMap(channel -> channel.createMessage("This meeting has ended already."))
                                    .then();
                        }
                        return handleReactionAdded(event, meeting);
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
    private Mono<Void> handleReactionAdded(ReactionAddEvent event, Meeting meeting) {
        return event.getUser().flatMap(user -> {
            User dbUser = userService.getUserByDiscordId(event.getUserId().asLong());

            if (dbUser != null) {
                meeting.addUserToMeeting(event.getUserId().asLong());
                meetingService.updateMeeting(meeting);
                dbUser.addAttendance(meeting.getStartTime());
            } else {
                return event.getMessage()
                        .flatMap(message -> message.removeReaction(event.getEmoji(), event.getUserId()))
                        .then(user.getPrivateChannel()
                                .flatMap(privateChannel -> privateChannel.createMessage("You have not registered to the club, therefore cannot contribute to roll call.")))
                        .then();
            }


            return Mono.empty();
        }); //end handleReaction()
    }
}
