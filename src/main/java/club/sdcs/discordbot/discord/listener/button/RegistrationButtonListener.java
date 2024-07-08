package club.sdcs.discordbot.discord.listener.button;

import club.sdcs.discordbot.discord.listener.EventListener;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.*;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionPresentModalSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RegistrationButtonListener implements EventListener<ButtonInteractionEvent> {

    private final UserService userService;
    public static boolean optInEmail, optInPhone;
    public static long userID;

    public RegistrationButtonListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    } //end getEventType()

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
        long discordUser = event.getInteraction().getUser().getId().asLong();
        User user = userService.getUserByDiscordId(discordUser);

        if (user != null) {
            if (user.getStatus() == User.Status.REGISTERED) {
                return event.reply(user.getFullName() + ", you have already completed the registration process.");
            }
        }

        String customId = event.getCustomId();
        userID = event.getInteraction().getUser().getId().asLong();

        if (customId.equalsIgnoreCase("start_registration")) {
            return event.edit()
                    .withEmbeds(EmbedCreateSpec.builder()
                            .title("Email Opt-In")
                            .color(Color.SUMMER_SKY)
                            .description("Would you like to opt-in to receive email notifications?\nThis means registering your email to the club.")
                            .build())
                    .withComponents(ActionRow.of(
                            Button.success("optinemail_yes", "Yes"),
                            Button.danger("optinemail_no", "No")
                    ));
        } else if (customId.equalsIgnoreCase("registration_form")) {
            return showModal(event, optInEmail, optInPhone);
        }

        return Mono.empty();

    } //end execute()

    private Mono<Void> showModal(ButtonInteractionEvent event, boolean optInEmail, boolean optInSMS) {
        String registrationId = event.getCustomId();

        ActionRow nameRow = ActionRow.of(TextInput.small(registrationId + "name", "Enter full name:").required(true));
        ActionRow districtIDRow = ActionRow.of(TextInput.small(registrationId + "districtID", "Enter district ID:").required(true));

        InteractionPresentModalSpec.Builder modalSpecBuilder = InteractionPresentModalSpec.builder()
                .title("Registration Process")
                .customId(registrationId)
                .addComponent(nameRow)
                .addComponent(districtIDRow);

        if (optInEmail) {
            ActionRow emailRow = ActionRow.of(TextInput.small(registrationId + "email", "Enter campus email:").required(true));
            modalSpecBuilder.addComponent(emailRow);
        }
        if (optInSMS) {
            ActionRow phoneNumberRow = ActionRow.of(TextInput.small(registrationId + "phonenumber", "Enter phone number:").required(true));
            modalSpecBuilder.addComponent(phoneNumberRow);
        }

        InteractionPresentModalSpec modalSpec = modalSpecBuilder.build();
        return event.presentModal(modalSpec);

    } //end showModal()

}
