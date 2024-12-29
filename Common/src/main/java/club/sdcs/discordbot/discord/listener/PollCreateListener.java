package club.sdcs.discordbot.discord.listener;

import club.sdcs.discordbot.model.DiscordPoll;
import club.sdcs.discordbot.service.DiscordPollService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.component.SelectMenu.Option;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.poll.Poll;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class PollCreateListener implements EventListener<MessageCreateEvent> {

    private final DiscordPollService discordPollService;

    public PollCreateListener(DiscordPollService discordPollService) {
        this.discordPollService = discordPollService;
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        Message message = event.getMessage();
        Optional<Poll> optionalPoll = message.getPoll();

        // Checks if Discord message is of type Poll
        if (optionalPoll.isPresent()) {
            Poll poll = optionalPoll.get();
            savePoll(poll, event);

            // DM the user who created the poll message
            return message.getAuthorAsMember()
                    .flatMap(User::getPrivateChannel)
                    .flatMap(channel -> channel.createMessage(createPollResponseMessage(poll)))
                    .then();
        }
        return Mono.empty();
    }

    // Sets Pass Condition for Poll's model
    private MessageCreateSpec createPollResponseMessage(Poll poll) {
        SelectMenu selectMenu = SelectMenu.of("poll-select-menu-" + poll.getId().asLong(),
                Option.of("Simple Majority", String.valueOf(DiscordPoll.PassCondition.SIMPLE_MAJORITY)),
                Option.of("2/3rds Majority", String.valueOf(DiscordPoll.PassCondition.TWO_THIRDS_MAJORITY)),
                Option.of("Unanimous", String.valueOf(DiscordPoll.PassCondition.UNANIMOUS)),
                Option.of("No Condition", String.valueOf(DiscordPoll.PassCondition.NO_CONDITION))
        ).withPlaceholder("Vote Condition");

        return MessageCreateSpec.builder()
                .content("Please choose pass condition for the poll: ")
                .addComponent(ActionRow.of(selectMenu))
                .build();
    }

    // Saves poll obj to DB
    private void savePoll(Poll poll, MessageCreateEvent event) {
        DiscordPoll discordPoll = new DiscordPoll();
        long pollCreatorId = event.getMessage().getAuthor().get().getId().asLong();

        discordPoll.setPollId(poll.getId().asLong());
        discordPoll.setMotionedUser(pollCreatorId);
        discordPoll.setPassCondition(DiscordPoll.PassCondition.NO_CONDITION);

        discordPollService.addDiscordPoll(discordPoll);
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
