package club.sdcs.discordbot.discord.commands.slash;

import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Timer;
import java.util.TimerTask;

@Component
public class MembershipCommand implements SlashCommand {

    public static Button registrationButton = Button.primary("start_registration", "Start Registration");
    private static final Button disabledButton = registrationButton.disabled();

    @Override
    public String getName() {
        return "membership";
    } //end getName()

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
            ActionRow actionRow = ActionRow.of(registrationButton);

            Mono<Void> reply = event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(createMembershipEmbed())
                    .addComponent(actionRow)
                    .build());

            // Schedule to disable the button after 5 seconds
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    event.editReply(InteractionReplyEditSpec.builder()
                            .addEmbed(createMembershipEmbed())
                            .addComponent(ActionRow.of(disabledButton))
                            .build()).subscribe();
                }
            }, 5000);

            return reply;
    } //end handle()

    private EmbedCreateSpec createMembershipEmbed() {
        return EmbedCreateSpec.builder()
                .color(Color.SUMMER_SKY)
                .title("SDCS Club Membership Registration")
                .description("Click the button below to start the registration process.")
                .build();
    } //end createMembershipEmbed()
}
