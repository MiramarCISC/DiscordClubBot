package club.sdcs.discordbot.discord.command.prefix.nomination;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Nomination;
import club.sdcs.discordbot.service.NominationService;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @see club.sdcs.discordbot.discord.listener.menu.NominationSelectMenuListener select menu listening/handling
 */
@Component
public class NominateListCommand implements PrefixCommand {
    private final NominationService nominationService;

    public NominateListCommand(NominationService nominationService) {
        this.nominationService = nominationService;
    }

    @Override
    public String getName() {
        return "!nominate list";
    }

    @Override
    public String getDescription() {
        return "Lists the list of nominated users & respected roles";
    }

    @Override
    public Mono<Void> handle(Message message) {
        List<Nomination> nominations = nominationService.getAllNominations();

        // Handle case when there are no nominations
        if (nominations.isEmpty()) {
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("No nominated users."))
                    .then();
        }

        // Group nominations by role and create a formatted string
        Map<String, List<String>> rolesWithNominations = nominations.stream()
                .collect(Collectors.groupingBy(
                        nomination -> nomination.getRole().toString(),
                        Collectors.mapping(this::getNominationStatus, Collectors.toList())
                ));

        StringBuilder contentBuilder = new StringBuilder();
        rolesWithNominations.forEach((role, statuses) -> {
            contentBuilder.append(role).append(":\n");
            contentBuilder.append(String.join("\n", statuses)).append("\n\n");
        });

        // Create an embed message with the formatted nominations
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Nominated Roles")
                .description(contentBuilder.toString())
                .build();

        // Create a dropdown menu with roles that have nominations
        List<SelectMenu.Option> options = getNominationOptionsWithSecondUser();

        MessageCreateSpec messageCreateSpec = MessageCreateSpec.builder()
                .addEmbed(embed)
                .addComponent(ActionRow.of(SelectMenu.of("select-nomination", options)
                        .withPlaceholder("Available roles for election")))
                .build();

        return message.getChannel()
                .flatMap(channel -> channel.createMessage(messageCreateSpec))
                .then();
    }

    // Get the nomination status (seconded or not seconded) for a nomination
    private String getNominationStatus(Nomination nomination) {
        String nomineeName = nomination.getNominee().getDiscordName();
        String status = (nomination.getSecond() == null) ? "not seconded" : "seconded";
        return nomineeName + " (" + status + ")";
    }

    // Get the options for the dropdown menu with roles that have at least one seconded nomination
    private List<SelectMenu.Option> getNominationOptionsWithSecondUser() {
        return nominationService.getAllNominations().stream()
                .filter(nomination -> nomination.getSecond() != null)
                .map(Nomination::getRole)
                .distinct()
                .map(role -> SelectMenu.Option.of(role.toString(), role.toString()))
                .collect(Collectors.toList());
    }
}
