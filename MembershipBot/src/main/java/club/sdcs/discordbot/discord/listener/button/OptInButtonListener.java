package club.sdcs.discordbot.discord.listener.button;

import club.sdcs.discordbot.discord.listener.EventListener;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static club.sdcs.discordbot.discord.listener.button.RegistrationButtonListener.*;

@Component
public class OptInButtonListener implements EventListener<ButtonInteractionEvent> {

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }

    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {

        String customId = event.getCustomId();

        if (customId.startsWith("optinemail_")) {

            optInEmail = customId.equals("optinemail_yes");
            return event.edit()
                    .withEmbeds(EmbedCreateSpec.builder()
                            .title("SMS Opt-In")
                            .color(Color.SUMMER_SKY)
                            .description("Would you like to opt-in to receive phone notifications?\nThis means registering your phone number to the club.")
                            .build())
                    .withComponents(ActionRow.of(
                            Button.success("optinphone_yes", "Yes"),
                            Button.danger("optinphone_no", "No")
                    ));
        } else if (customId.startsWith("optinphone_")) {

            optInPhone = customId.equals("optinphone_yes");
            return event.edit()
                    .withEmbeds(EmbedCreateSpec.builder()
                            .title("Active Role Opt-In")
                            .color(Color.SUMMER_SKY)
                            .description("Would you like to become an active member of the club?\nThis means that you are required to attend at least one meeting a month." +
                                    "\nIf not, you will become an inactive member of the club.")
                            .build())
                    .withComponents(ActionRow.of(
                            Button.success("optinrole_yes", "Yes"),
                            Button.danger("optinrole_no", "No")
                    ));

        } else if (customId.startsWith("optinrole_")) {

            optInActive = customId.equals("optinrole_yes");

            if (optInActive) {
                return event.edit()
                        .withEmbeds(EmbedCreateSpec.builder()
                                .title("Voter Role Opt-In")
                                .color(Color.SUMMER_SKY)
                                .description("Being an active member of the club, you have the ability to vote if you wish to." +
                                        "\nVoting entails selecting officers of the club. Would you like to become a voting member?")
                                .build())
                        .withComponents(ActionRow.of(
                                Button.success("optinvoter_yes", "Yes"),
                                Button.danger("optinvoter_no", "No")
                        ));
            }
            return event.edit()
                    .withEmbeds(EmbedCreateSpec.builder()
                            .title("Registration Form")
                            .color(Color.SUMMER_SKY)
                            .description("Click on the button to finish registering for the Miramar SDCS Club.")
                            .build())
                    .withComponents(ActionRow.of(
                            Button.primary("registration_form", "Begin Form")
                    ));

        } else if (customId.startsWith("optinvoter_")) {

            optInVoter = customId.equals("optinvoter_yes");

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
