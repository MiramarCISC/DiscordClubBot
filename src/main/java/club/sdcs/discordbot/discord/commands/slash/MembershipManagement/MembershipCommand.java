package club.sdcs.discordbot.discord.commands.slash.MembershipManagement;

import club.sdcs.discordbot.discord.commands.slash.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MembershipCommand implements SlashCommand {

    public static Button registrationButton = Button.primary("start_registration", "Start Registration");

    @Override
    public String getName() {
        return "membership";
    } //end getName()

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
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
