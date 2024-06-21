package club.sdcs.discordbot.discord;

import club.sdcs.discordbot.service.UserService;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ButtonListener {

    public ButtonListener(GatewayDiscordClient client, UserService userService) {
        client.on(ButtonInteractionEvent.class, this::handleButton).subscribe();
    } //end constructor

    public Mono<Void> handleButton(ButtonInteractionEvent event) {
        String customId = event.getCustomId();

        // add cases here if adding more buttons with different functions
        switch (customId) {

            case "start_registration":
                return event.reply("You have started the registration process. See your direct messages for further detail.")
                        .then(event.getInteraction().getUser().getPrivateChannel())
                        .flatMap(privateChannel -> privateChannel.createMessage("Please enter in your name to begin the registration process. (!user set name [first name] [last name])"))
                        .then();

            default:
                return Mono.empty();
        }
    } //end handleButton()
}
