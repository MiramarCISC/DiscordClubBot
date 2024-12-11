package club.sdcs.discordbot.discord.command.prefix.general;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RolesCommand implements PrefixCommand {
    private final UserService userService;

    public RolesCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "!roles";
    }

    @Override
    public String getDescription() {
        return "Displays a list of assigned & unassigned roles";
    }

    @Override
    public Mono<Void> handle(Message message) {
        return message.getChannel()
                .flatMap(messageChannel -> messageChannel.createMessage(rolesList())
                        .then());
    }

    private String rolesList() {
        List<User> officers = userService.getOfficers();
        Set<User.Role> assignedRoles = officers.stream().map(User::getRole).collect(Collectors.toSet());
        Set<User.Role> allRoles = EnumSet.of(
                User.Role.PRESIDENT,
                User.Role.VP_EXTERNAL,
                User.Role.VP_INTERNAL,
                User.Role.VP_OPERATIONS,
                User.Role.SECRETARY,
                User.Role.TREASURER,
                User.Role.MARKETING_OFFICER,
                User.Role.SOCIAL_MEDIA_OFFICER,
                User.Role.ASG_REPRESENTATIVE
        );

        Set<User.Role> unassignedRoles = EnumSet.copyOf(allRoles);
        unassignedRoles.removeAll(assignedRoles);

        StringBuilder result = new StringBuilder();

        result.append("**Assigned Roles:**\n");
        for (User.Role role : assignedRoles) {
            String users = officers.stream()
                    .filter(user -> user.getRole() == role)
                    .map(User::getFullName)
                    .collect(Collectors.joining(", "));
            result.append(role).append(": ").append(users).append("\n");
        }
        result.append("\n**Unassigned Roles:**\n");
        for (User.Role role : unassignedRoles) {
            result.append(role).append("\n");
        }
        
        return result.toString();
    }
}
