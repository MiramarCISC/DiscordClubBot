package club.sdcs.discordbot.discord.listener.poll;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.service.NominationService;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.poll.PollVoteEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class NominationPollListener implements EventListener<PollVoteEvent> {
    private final UserService userService;
    private final NominationService nominationService;

    public NominationPollListener(UserService userService, NominationService nominationService) {
        this.userService = userService;
        this.nominationService = nominationService;
    }

    @Override
    public Class<PollVoteEvent> getEventType() {
        return PollVoteEvent.class;
    }

    @Override
    public Mono<Void> execute(PollVoteEvent event) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
