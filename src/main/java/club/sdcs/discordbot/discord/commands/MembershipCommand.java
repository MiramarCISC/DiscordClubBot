package club.sdcs.discordbot.discord.commands;

import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.swing.*;

@Component
public class MembershipCommand implements SlashCommand {

    private final UserService userService;

    public MembershipCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "membership";
    } //end getName()

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Button registrationButton = Button.primary("start_registration", "Start Registration");
        ActionRow actionRow = ActionRow.of(registrationButton);

        return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(createMembershipEmbed())
                .addComponent(actionRow)
                .build());
    } //end handle()

    private EmbedCreateSpec createMembershipEmbed() {
        return EmbedCreateSpec.builder()
                .color(Color.SUMMER_SKY)
                .title("SDCS Club Membership Registration")
                .description("Click the button below to start the registration process.")
                .build();
    } //end createMembershipEmbed()
}
