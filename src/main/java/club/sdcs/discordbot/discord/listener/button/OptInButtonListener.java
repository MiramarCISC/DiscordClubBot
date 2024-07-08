package club.sdcs.discordbot.discord.listener.button;

import club.sdcs.discordbot.discord.commands.slash.MembershipManagement.EmbedUtils;
import club.sdcs.discordbot.discord.listener.EventListener;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.TextInput;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionPresentModalSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static club.sdcs.discordbot.discord.listener.button.RegistrationButtonListener.optInEmail;
import static club.sdcs.discordbot.discord.listener.button.RegistrationButtonListener.optInPhone;

@Component
public class OptInButtonListener implements EventListener<ButtonInteractionEvent> {

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {

        if (event.getInteraction().getUser().getId().asLong() != RegistrationButtonListener.userID) {
            return event.getInteraction().getChannel()
                    .flatMap(channel -> channel.createMessage("You are not the person who started the registration process." +
                            "\nIf you wish to register, please use the command `/membership` and start the process yourself."))
                    .then();
        }

        String customId = event.getCustomId();

        if (customId.startsWith("optinemail_")) {
            optInEmail = customId.equals("optinemail_yes");
            return event.edit()
                    .withEmbeds(EmbedCreateSpec.builder()
                            .title("SMS Opt-In")
                            .color(Color.SUMMER_SKY)
                            .description("Would you like to opt-in to receive email notifications?\nThis means registering your phone number to the club.")
                            .build())
                    .withComponents(ActionRow.of(
                            Button.success("optinphone_yes", "Yes"),
                            Button.danger("optinphone_no", "No")
                    ));
        } else if (customId.startsWith("optinphone_")) {
            optInPhone = customId.equals("optinphone_yes");
            return event.edit()
                    .withEmbeds(EmbedCreateSpec.builder()
                            .title("Registration Form")
                            .color(Color.SUMMER_SKY)
                            .description("Click on the button to finish registering for the Miramar SDCS Club.")
                            .build())
                    .withComponents(ActionRow.of(
                            Button.primary("registration_form", "Begin Form")
                    ));
        }

        return Mono.empty();
    }

}
