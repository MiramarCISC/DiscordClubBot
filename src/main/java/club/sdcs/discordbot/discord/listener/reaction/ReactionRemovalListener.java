package club.sdcs.discordbot.discord.listener.reaction;

import club.sdcs.discordbot.discord.command.slash.rollcall.RollCallCommand;
import club.sdcs.discordbot.discord.listener.EventListener;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.MeetingService;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactionRemovalListener implements EventListener<ReactionRemoveEvent> {

    private final UserService userService;
    private final MeetingService meetingService;

    public ReactionRemovalListener(UserService userService, MeetingService meetingService) {
        this.userService = userService;
        this.meetingService = meetingService;
    }

    @Override
    public Class<ReactionRemoveEvent> getEventType() {
        return ReactionRemoveEvent.class;
    }

    @Override
    public Mono<Void> execute(ReactionRemoveEvent event) {

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
                        return handleReactionRemoval(event, meeting);
                    });
        }

        return Mono.empty();
    }

    /**
     * handles reaction removal
     * @param event takes in reaction
     * @return user removal from meeting log
     */
    private Mono<Void> handleReactionRemoval(ReactionRemoveEvent event, Meeting meeting) {
        return event.getUser().flatMap(user -> {
            meeting.removeUserFromMeeting(event.getUserId().asLong());
            meetingService.updateMeeting(meeting);

            return Mono.empty();
        });
    }

}
