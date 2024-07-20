package club.sdcs.discordbot.discord.listener.reaction;

import club.sdcs.discordbot.discord.command.slash.rollcall.RollCallCommand;
import club.sdcs.discordbot.discord.listener.EventListener;
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
        return handleReactionRemoval(event);
    }

    /**
     * handles reaction removal
     * @param event takes in reaction
     * @return user removal from meeting log
     */
    private Mono<Void> handleReactionRemoval(ReactionRemoveEvent event) {
        return event.getUser().flatMap(user -> {
            User dbUser = userService.getUserByDiscordId(event.getUserId().asLong());

            if (dbUser != null) {
                if (event.getEmoji().asUnicodeEmoji().get().getRaw().equals("✅")) {
                    meetingService.findMeetingById(RollCallCommand.meetingId).removeUserFromMeeting(dbUser);
                }
            }

            return Mono.empty();
        });
    }

}
