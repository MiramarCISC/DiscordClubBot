package club.sdcs.discordbot.discord.command.prefix.user;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.discord.command.slash.EmbedUtils;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

@Component
public class UserInfoCommand implements PrefixCommand {

    private final UserService userService;

    public UserInfoCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "!user info";
    }

    @Override
    public String getDescription() {
        return "Gets user information";
    }

    @Override
    public Mono<Void> handle(Message message) {

        return message.getAuthorAsMember()
                .flatMap(member -> message.getGuild()
                        .flatMap(guild -> isOfficer(member, guild))
                            .flatMap(isOfficer -> {
                                if (!isOfficer) {
                                    return message.getChannel()
                                        .flatMap(channel -> channel.createMessage("You must be an officer to use this command."))
                                        .then();
                                }

                                String[] content = message.getContent().split(" ");
                                String userTag = String.join(" ", Arrays.copyOfRange(content, 2, content.length));;

                                return message.getGuild()
                                        .flatMap(guild -> findMemberByTag(guild, message, userTag))
                                        .flatMap(member1 -> {
                                            long userId = member1.getId().asLong();
                                            User user;

                                            if (userService.getUserByDiscordId(userId) != null) {
                                                user = userService.getUserByDiscordId(userId);
                                            } else {
                                                user = null;
                                                return message.getChannel()
                                                    .flatMap(channel -> channel.createMessage("This user has not completed the registration process."))
                                                    .then();
                                            }

                                            return message.getChannel()
                                                .flatMap(channel -> EmbedUtils.createEmbedMessage(channel, "User Info: " + member1.getTag(),
                                                                "Name: **" + user.getFullName() + "**\nDistrict ID: **" + user.getDistrictId() +
                                                                        "**\nCampus Email: **" + user.getEmail() + "**\nPhone Number: **" + user.getMobileNumber() + "**"))
                                                .then();

                                        });

                            }));

    }

    /**
     * checks if user who types command is officer
     * @param member takes in member
     * @return if member is officer or not
     */
    private Mono<Boolean> isOfficer(Member member, Guild guild) {
        return getOfficerRole(guild)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(optionalRole -> {
                    if (optionalRole.isEmpty()) {
                        return Mono.just(false);
                    }
                    Role officerRole = optionalRole.get();
                    return Mono.just(member.getRoleIds().contains(officerRole.getId()));
                });
    } //end isOfficer()

    /**
     * retrieves the officer role from the guild
     * @param guild the guild to retrieve the officer role from
     * @return officer role if found
     */
    private Mono<Role> getOfficerRole(Guild guild) {
        return guild.getRoles()
                .filter(role -> role.getName().equalsIgnoreCase("Officer"))
                .next();
    } //end getOfficerRole()

    /**
     * finds member by the user tag
     * @param guild takes in guild of user
     * @param message takes in message
     * @param userTag takes in user tag
     * @return if it finds a match
     */
    private Mono<Member> findMemberByTag(Guild guild, Message message, String userTag) {
        return guild.requestMembers()
                .filter(member -> member.getTag().equalsIgnoreCase(userTag))
                .next()
                .switchIfEmpty(message.getChannel()
                        .flatMap(channel -> channel.createMessage("User does not exist. Make sure you use the user's tag and not display name."))
                        .then(Mono.empty()));
    }//end findMemberByTag()

}
