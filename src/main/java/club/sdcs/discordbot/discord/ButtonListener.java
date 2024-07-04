package club.sdcs.discordbot.discord;

import club.sdcs.discordbot.discord.commands.prefix.UserRegistration.EmbedUtils;
import club.sdcs.discordbot.discord.commands.prefix.UserRegistration.UserRegistrationCommand;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
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
                UserRegistrationCommand.registration_mode = true;

                    return event.reply("You have started the registration process. See your direct messages for further detail.")
                            .then(event.getInteraction().getUser().getPrivateChannel())
                            .flatMap(privateChannel -> EmbedUtils.createEmbedMessage(privateChannel,
                                    "Registration Process Started",
                                    "Please enter your name to **begin** the registration process.\n\n" +
                                            "**Step**:\n" +
                                            "`!user setName [first name] [last name]`" +
                                            "\n\n\n**Example:**\n",
                                    "https://i.imgur.com/xx1rjLz.png"
                            ))
                            .then();

            default:
                return Mono.empty();
        }
    } //end handleButton()
}
