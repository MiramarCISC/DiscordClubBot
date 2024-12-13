package club.sdcs.discordbot.discord.command.prefix.user;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.discord.command.slash.EmbedUtils;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
public class UserListCommand implements PrefixCommand {

    @Override
    public String getName() {
        return "!user list";
    }

    @Override
    public String getDescription() {
        return "Lists all users";
    }

    @Override
    public Mono<Void> handle(Message message) {
        String[] content = message.getContent().split(" ");
        if (content.length != 3) {
            return message.getChannel()
                    .flatMap(channel ->
                            channel.createMessage("""
                                    It appears you have typed the command incorrectly. \

                                    Ensure that you type the command as follows: **`!user list [role]`**. \

                                    Insert the role you are searching for into the brackets and it will provide all members with that role."""))
                    .then();
        }

        String roleName = content[2].toUpperCase();

        return message.getGuild()
                .flatMap(guild -> findMembersByRole(guild, roleName))
                .flatMap(memberNames -> message.getChannel()
                        .flatMap(channel -> EmbedUtils.createEmbedMessage(message, "Members with Role: " + roleName,
                                        "○ " + memberNames))
                .then());
    }

    /**
     * finds member by their role
     * @param guild takes in guild of user
     * @param roleName takes in role wanted
     * @return all members that have the role
     */
    private Mono<String> findMembersByRole(Guild guild, String roleName) {
        return guild.getRoles()
                .filter(role -> role.getName().equalsIgnoreCase(roleName))
                .next()
                .flatMap(role -> guild.getMembers()
                        .filter(member -> member.getRoleIds().contains(role.getId()))
                        .map(Member::getDisplayName)
                        .collect(Collectors.joining("\n○ ")));
    } //end findMemberByRole()

}
