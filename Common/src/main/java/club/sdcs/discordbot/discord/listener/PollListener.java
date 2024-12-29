package club.sdcs.discordbot.discord.listener;

import club.sdcs.discordbot.model.DiscordPoll;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.DiscordPollService;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.poll.PollVoteEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.poll.Poll;
import discord4j.core.object.entity.poll.PollAnswerCount;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Component
public class PollListener implements EventListener<PollVoteEvent> {
    private final DiscordPollService discordPollService;
    private final UserService userService;

    public PollListener(DiscordPollService discordPollService, UserService userService) {
        this.discordPollService = discordPollService;
        this.userService = userService;
    }

    @Override
    public Class<PollVoteEvent> getEventType() {
        return PollVoteEvent.class;
    }

    @Override
    public Mono<Void> execute(PollVoteEvent event) {
        DiscordPoll poll = discordPollService.findDiscordPollById(event.getMessageId().asLong());
        String condition = poll.getPassCondition().toString();

        // Checks only for polls with set conditions
        if (!poll.getPassCondition().equals(DiscordPoll.PassCondition.NO_CONDITION)) {
            Poll optionalPoll = event.getPoll().block();

            assert optionalPoll != null;
            return optionalPoll.getLatestResults()
                    .flatMapMany(pollResult -> Flux.fromIterable(pollResult.getAnswerCount())) // Get all answer counts
                    .collectMap(PollAnswerCount::getAnswerId, PollAnswerCount::getCount) // Collect into a map of ID -> count
                    .flatMap(answerCounts -> {
                        if (votePassed(poll, answerCounts)) {

                            // Send a reply message to the poll
                            return event.getMessage()
                                    .flatMap(Message::getChannel)
                                    .flatMap(channel -> channel.createMessage(spec -> {
                                        spec.setMessageReference(event.getMessageId()); // Reference the poll message
                                        spec.setContent("The poll has passed! 🎉");
                                    }));
                        }
                        return Mono.empty();
                    })
                    .then();
        }
        return Mono.empty();
    }

    private boolean votePassed(DiscordPoll poll, Map<Integer, Integer> answerCounts) {
        // Get all registered users to calculate total possible votes
        List<User> users = userService.getAllUsers();
        List<User> registeredUsers = users.stream()
                .filter(user -> user.getStatus() == User.Status.REGISTERED) // Filter for REGISTERED status
                .toList();

        double totalVotes = registeredUsers.size(); // Use total registered users as the total possible votes
        double threshold;

        if (poll.getPassCondition() == DiscordPoll.PassCondition.SIMPLE_MAJORITY) {
            // Simple majority: more than half of the total votes
            threshold = totalVotes / 2;
            return answerCounts.values().stream().anyMatch(count -> count > threshold);
        }

        if (poll.getPassCondition() == DiscordPoll.PassCondition.TWO_THIRDS_MAJORITY) {
            // Two-thirds majority: at least 2/3 of total votes
            threshold = (2.0 / 3.0) * totalVotes;
            return answerCounts.values().stream().anyMatch(count -> count >= threshold);
        }

        if (poll.getPassCondition() == DiscordPoll.PassCondition.UNANIMOUS) {
            // Unanimous: must equal the total number of votes
            return answerCounts.values().stream().anyMatch(count -> count == totalVotes);
        }

        return false;
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
