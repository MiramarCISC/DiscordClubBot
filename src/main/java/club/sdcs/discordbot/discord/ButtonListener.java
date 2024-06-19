package club.sdcs.discordbot.discord;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ButtonListener {

    public ButtonListener(GatewayDiscordClient client) {
        client.on(ButtonInteractionEvent.class, this::handleButton).subscribe();
    } //end constructor

    public Mono<Void> handleButton(ButtonInteractionEvent event) {
        String customId = event.getCustomId();

        // add cases here if adding more buttons with different functions
        switch (customId) {

            case "start_registration":
                // TODO: receive user information (name, ID, campus email, phone number) and save to repo
                // TODO: request user to be active, voting member
                // TODO: ability to unsubcribe from emails and sms
                // TODO: record discord ID to map people to discord users
                return event.reply("You have started the registration process. See your direct messages for further detail.");

            default:
                return Mono.empty();
        }
    } //end handleButton()
}
