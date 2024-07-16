package club.sdcs.discordbot.discord.command.prefix.nomination;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class NominateCommand implements PrefixCommand {
    private final UserService userService;

    public NominateCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "!nominate";
    }

    @Override
    public String getDescription() {
        return "Nominate an active user (required second) for officer position `!nominate @user [role]`.";
    }

    // TODO: Nominator, nominee, & second must be active member
    // TODO: implement discord poll for vote
    @Override
    public Mono<Void> handle(Message message) {
        String content = message.getContent();
        String[] parts = content.split(" ");

        Pattern pattern = Pattern.compile("<@(\\d+)>");
        Matcher matcher = pattern.matcher(parts[1]);

        if (parts.length < 3) {
            return message.getChannel()
                    .flatMap(messageChannel -> messageChannel.createMessage("Usage: `!nominate @user role`")
                            .then());
        }

        if (!matcher.find()) {
            return message.getChannel()
                    .flatMap(messageChannel -> messageChannel.createMessage("Invalid user mention.")
                            .then());
        }

        String userId = matcher.group(1);
        String nominatorUserName = message.getAuthor().map(User::getUsername).orElse("Unknown");
        String role = parts[2];

        if (!isValidRole(role)) {
            return message.getChannel()
                    .flatMap(messageChannel -> messageChannel.createMessage("Not a valid role.")
                            .then());
        }

        return message.getChannel()
                .flatMap(messageChannel -> messageChannel.createMessage(nominatorUserName + " nominated <@" + userId + "> for " + role)
                        .then());
    }

    // TODO: check if role is vacant
    // TODO: exclude non-officer roles
    public boolean isValidRole(String role) {
        String normalizedRole = role.toUpperCase();

        List<club.sdcs.discordbot.model.User.Role> officerRoles = Arrays.asList(
                club.sdcs.discordbot.model.User.Role.values()
        );

        try {
            club.sdcs.discordbot.model.User.Role userRole = club.sdcs.discordbot.model.User.Role.valueOf(normalizedRole);
            return officerRoles.contains(userRole);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
