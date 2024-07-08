package club.sdcs.discordbot.discord.commands.prefix;

import club.sdcs.discordbot.discord.commands.slash.MembershipManagement.ValidityChecker;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class UserEditCommand implements PrefixCommand {

    private final UserService userService;
    private final ValidityChecker validityChecker = new ValidityChecker();

    public UserEditCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "!user edit";
    }

    @Override
    public Mono<Void> handle(Message message) {
        return message.getChannel().flatMap(channel -> {
            if (channel.getType() == Channel.Type.DM) {
                return processUserEditCommand(message);
            } else {
                return channel.createMessage("This command can only be used in private messages.").then();
            }
        });
    } //end handle()

    /**
     * processes user edit command
     * @param message takes in message by user
     * @return updated information
     */
    private Mono<Void> processUserEditCommand(Message message) {
        try {
            User user = userService.getUserByDiscordId(message.getAuthor().get().getId().asLong());

            String[] content = message.getContent().split(" ");
            String fieldToEdit = content[2].toLowerCase();
            String newInformation = String.join(" ", Arrays.copyOfRange(content, 3, content.length));

            switch (fieldToEdit) {

                case "name" -> {
                    user.setFullName(newInformation);
                    return message.getChannel().flatMap(channel -> channel.createMessage("Successfully edited the user!\nName: " + user.getFullName()).then());
                }

                case "email" -> {
                    if (validityChecker.checkEmailValidity(newInformation)) {
                        user.setEmail(newInformation);
                        return message.getChannel().flatMap(channel -> channel.createMessage("Successfully edited the user!\nEmail: " + user.getEmail()).then());
                    } else {
                        return createErrorMessage(message);
                    }
                }

                case "districtid" -> {
                    if (validityChecker.checkDistrictIDValidity(newInformation)) {
                        user.setDistrictId(Long.parseLong(newInformation));
                        return message.getChannel().flatMap(channel -> channel.createMessage("Successfully edited the user!\nDistrict ID: " + user.getDistrictId()).then());
                    } else {
                        return createErrorMessage(message);
                    }
                }

                case "phonenumber" -> {
                    if (validityChecker.checkPhoneNumberValidity(newInformation)) {
                        user.setMobileNumber(Long.parseLong(newInformation));
                        return message.getChannel().flatMap(channel -> channel.createMessage("Successfully edited the user!\nPhone Number: " + user.getMobileNumber()).then());
                    } else {
                        return createErrorMessage(message);
                    }
                }

                default -> {
                    return createErrorMessage(message);
                }
            }
        } catch (Exception exception) {
            return createErrorMessage(message);
        }

    } //end processUserEditCommand()

    /**
     * creates an error message
     * @param message takes in message
     * @return bot message
     */
    private Mono<Void> createErrorMessage(Message message) {

        return message.getChannel().flatMap(channel -> channel.createMessage("""
                            There was an error. This was caused by one of more of the following reasons: \


                            1. You have not completed the registration process.\

                            2. You typed the command incorrectly.\

                            3. The information you provided was not valid.\


                            Please try again once resolving one of these reasons."""))
                .then();

    } //end createErrorMessage()

} //end UserRegistrationCommand class
