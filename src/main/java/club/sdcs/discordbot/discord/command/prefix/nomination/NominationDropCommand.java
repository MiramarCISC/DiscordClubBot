package club.sdcs.discordbot.discord.command.prefix.nomination;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Nomination;
import club.sdcs.discordbot.service.NominationService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO: Only select users to roles command on other users
@Component
public class NominationDropCommand implements PrefixCommand {
    private final NominationService nominationService;

    public NominationDropCommand(NominationService nominationService) {
        this.nominationService = nominationService;
    }

    @Override
    public String getName() {
        return "!nominate drop";
    }

    @Override
    public String getDescription() {
        return "Drops nomination of user. Can only be done by nominee themselves (& select roles)";
    }

    @Override
    public Mono<Void> handle(Message message) {
        Long nomineeId = message.getAuthor().map(User::getId).map(Snowflake::asLong).orElse(null);

        if (!hasNomination(nomineeId)) {
            return message.getChannel()
                    .flatMap(messageChannel -> messageChannel.createMessage("User does not have nomination."))
                    .then();
        }

        deleteNomination(nomineeId);

        String userMention = "<@" + nomineeId + ">";

        return message.getChannel()
                .flatMap(messageChannel -> messageChannel.createMessage("Nomination of " + userMention + " dropped."))
                .then();
    }

    private boolean hasNomination(Long nomineeId) {
        return nominationService.getAllNominations().stream()
                .anyMatch(nomination -> nomination.getNominee() != null
                        && nomination.getNominee().getDiscordId() == nomineeId);
    }

    private void deleteNomination(Long nomineeId) {
        List<Nomination> nominations = nominationService.getAllNominations();
        for (Nomination nomination : nominations) {
            if (nomination.getNominee().getDiscordId() == nomineeId) {
                nominationService.deleteNomination(nomination.getNominationId());
                break;
            }
        }
    }

}
