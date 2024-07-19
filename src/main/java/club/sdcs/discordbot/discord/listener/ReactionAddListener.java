package club.sdcs.discordbot.discord.listener;

import club.sdcs.discordbot.discord.command.slash.rollcall.RollCallCommand;
import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.repository.MeetingRepository;
import club.sdcs.discordbot.repository.UserRepository;
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

        return event.getUser()
                .flatMap(user -> {
                    if (user.isBot()) {
                        return Mono.empty();
                    }
                    return handleReaction(event);
                });
    }


    private Mono<Void> handleReaction(ReactionAddEvent event) {

        //TODO: error handling, make sure user is registered, if not bad reaction
        //TODO: handle user reaction removal

        User user = userService.getUserByDiscordId(event.getUserId().asLong());
        meetingService.findMeetingById(RollCallCommand.meetingId).addUserToMeeting(user);

        return Mono.empty();
    } //end handleReaction()

}
