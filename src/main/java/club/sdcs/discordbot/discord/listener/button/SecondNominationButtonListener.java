package club.sdcs.discordbot.discord.listener.button;

import club.sdcs.discordbot.discord.EventListener;
import club.sdcs.discordbot.model.Nomination;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.NominationService;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecondNominationButtonListener implements EventListener<ButtonInteractionEvent> {
    private final NominationService nominationService;
    private final UserService userService;

    public SecondNominationButtonListener(NominationService nominationService, UserService userService) {
        this.nominationService = nominationService;
        this.userService = userService;
    }

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        String customId = event.getCustomId();

        // Check if the customId starts with "second-nominate-"
        if (customId.startsWith("second-nominate-")) {
            return handle(event);
        }
        return Mono.empty();
    }

    // Handle the button interaction event
    private Mono<Void> handle(ButtonInteractionEvent event) {
        return Mono.fromCallable(() -> {
            String nominationId = event.getCustomId().replaceFirst("second-nominate-", "");
            return nominationService.getNominationById(Long.parseLong(nominationId));
        }).flatMap(nomination -> {
            if (nomination == null) {
                return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .content("Nomination not found.")
                        .ephemeral(true)
                        .build()).then();
            }

            // Validate the seconding user
            if (!isValidSecond(event, nomination)) {
                return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .content("Invalid second.")
                        .ephemeral(true)
                        .build()).then();
            }

            // Update the nomination with the seconding user
            long secondUserId = event.getUser().getId().asLong();
            User secondUser = userService.getUserByDiscordId(secondUserId);
            nomination.setSecond(secondUser);
            nominationService.updateNomination(nomination);

            // Delete the original message and send a new message indicating the seconding
            return event.getInteraction().getMessage().get().delete()
                    .then(sendNominationMessage(event, nomination));
        });
    }

    // Send a message indicating the seconding of the nomination
    private Mono<Void> sendNominationMessage(ButtonInteractionEvent event, Nomination nomination) {
        String secondUserId = event.getUser().getId().asString();
        String secondUserMention = "<@" + secondUserId + ">";

        String nomineeId = String.valueOf(nomination.getNominee().getDiscordId());
        String nomineeMention = "<@" + nomineeId + ">";

        String role = nomination.getRole().toString();

        return event.getInteraction().getChannel()
                .flatMap(messageChannel -> messageChannel
                        .createMessage(secondUserMention + " seconds nomination for " + nomineeMention + " as " + role)
                        .then());
    }

    // Validate if the user is eligible to second the nomination
    private boolean isValidSecond(ButtonInteractionEvent event, Nomination nomination) {
        long secondUserId = event.getUser().getId().asLong();
        User secondUser = userService.getUserByDiscordId(secondUserId);

        if (secondUser == null) {
            System.out.println("null secondUser");
            return false;
        }

        long nominatorId = nomination.getNominator().getDiscordId();

        // A user cannot second if they are not the nominator
        return secondUserId != nominatorId;
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
