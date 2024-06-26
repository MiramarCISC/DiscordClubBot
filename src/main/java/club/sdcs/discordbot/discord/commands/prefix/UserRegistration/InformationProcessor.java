package club.sdcs.discordbot.discord.commands.prefix.UserRegistration;

import club.sdcs.discordbot.model.User;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.GuildMemberEditSpec;
import reactor.core.publisher.Mono;

public class InformationProcessor {

    private final ValidityChecker validityChecker = new ValidityChecker();

    /**
     * sets the user's name
     * @param eventMessage takes in user message command (!user setName [name])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processName(Message message, String[] eventMessage, User user) {
        if (validityChecker.checkNameValidity(eventMessage)) {
            String firstName = eventMessage[2];
            String lastName = eventMessage[3];
            user.setFullName(firstName + " " + lastName);

            return Mono.just(message)
                    .flatMap(Message::getChannel)
                    .flatMap(messageChannel -> messageChannel.createMessage("Name received. Your name is set to **" +
                            firstName + " " + lastName + "**.\n\nFor the next step, please enter in your **campus** email. (**!user setEmail [email]**)"))
                    .then();
        }

        return createErrorMessage(message);
    } //end processName()

    /**
     * sets the user's email
     * @param eventMessage takes in user message command (!user setEmail [email])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processEmail(Message message, String[] eventMessage, User user) {
        if (validityChecker.checkEmailValidity(eventMessage)) {
            String email = eventMessage[2];
            user.setEmail(email);

            return Mono.just(message)
                    .flatMap(Message::getChannel)
                    .flatMap(messageChannel -> messageChannel.createMessage("Campus email received. Your email is set to **" +
                            email + "**.\n\nFor the next step, please enter in your **district ID**. (**!user setDistrictID [districtID]**)"))
                    .then();
        }

        return createErrorMessage(message);
    } //end processEmail()

    /**
     * sets the user's district ID
     * @param eventMessage takes in user message command (!user setDistrictID [districtID])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processDistrictID(Message message, String[] eventMessage, User user) {
        if (validityChecker.checkDistrictIDValidity(eventMessage)) {
            long districtID = Long.parseLong(eventMessage[2]);
            user.setDistrictId(districtID);

            return Mono.just(message)
                    .flatMap(Message::getChannel)
                    .flatMap(messageChannel -> messageChannel.createMessage("District ID received. Your district ID is set to **" +
                            districtID + "**.\n\nFor the next step, please enter in your **phone number** (no dashes [-]). (**!user setPhoneNumber [phonenumber]**)"))
                    .then();
        }

        return createErrorMessage(message);
    } //end processDistrictID()

    /**
     * sets the user's phone information
     * @param eventMessage takes in user message command (!user setPhoneNumber [phonenumber])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processPhone(Message message, String[] eventMessage, User user) {
        if (validityChecker.checkPhoneNumberValidity(eventMessage)) {
            long phone = Long.parseLong(eventMessage[2]);
            user.setMobileNumber(phone);

            return Mono.just(message)
                    .flatMap(Message::getChannel)
                    .flatMap(messageChannel -> messageChannel.createMessage("Phone number received. Your phone number is set to **" +
                            phone + "**.\n\nAs the last step of the registration process, would you like to become an **active** member of the club?" +
                            "\nThis means that you will participate in a meeting at least once a month and have the ability to vote for officers of the club." +
                            "\nIf this interests you, use the command '**!user request active**' to become an **active** member of the club." +
                            "\nOtherwise, use the command '**!user request inactive**' to still become a member of the club but **inactive**."))
                    .then();
        }

        return createErrorMessage(message);
    } //end processPhone()

    /**
     * assigns a role to the user upon joining the club (active/inactive)
     * @param message takes in message sent by user
     * @param eventMessage split up message into array
     * @param user takes in user
     * @return bot message
     */
    public Mono<Void> assignRoleToUser(Message message, String[] eventMessage, User user) {

        String requestedRole = eventMessage[2].toLowerCase();

        if (requestedRole.equals("active")) {
            user.setRole(User.Role.ACTIVE);

            return message.getClient().getGuildById(Snowflake.of(1252368620047044648L)) // CHANGE GUILD ID
                    .flatMap(guild -> guild.getMemberById(Snowflake.of(message.getAuthor().get().getId().asString())))
                    .flatMap(member -> member.edit(GuildMemberEditSpec.builder().addRole(Snowflake.of(1254855153857859656L)).build())) // CHANGE ROLE ID
                    .then(askConfirmation(message, user));

        } else if (requestedRole.equals("inactive")) {
            user.setRole(User.Role.INACTIVE);

            return message.getClient().getGuildById(Snowflake.of(1252368620047044648L)) // CHANGE GUILD ID
                    .flatMap(guild -> guild.getMemberById(Snowflake.of(message.getAuthor().get().getId().asString())))
                    .flatMap(member -> member.edit(GuildMemberEditSpec.builder().addRole(Snowflake.of(1255218703922888705L)).build())) //CHANGE ROLE ID
                    .then(askConfirmation(message, user));


        } else {
            return Mono.just(message)
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("I did not recognize that role. Refer to the instructions given previously."))
                    .then();
        }

    } //end assignRoleToUser()

    /**
     * asks user to confirm their details
     * @param message takes in message to find message channel
     * @param user takes in user
     * @return confirmation message
     */
    private Mono<Void> askConfirmation(Message message, User user) {
        String confirmationMessage = "\nName: " + user.getFullName() +
                "\nEmail: **" + user.getEmail() +
                "**\nDistrict ID: **" + user.getDistrictId() +
                "**\nPhone number: **" + user.getMobileNumber() +
                "**\nRole Assigned: **" + user.getRole() +
                "**\n\nType '**!user confirm**' to confirm this user information or '**!user edit [field] [information]**' to edit a specific field.";

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(confirmationMessage))
                .then();
    } //end askConfirmation()

    /**
     * edits user details
     * @param message takes in user message to know what channel to send to
     * @param content takes in split up message content to change user information
     * @param user takes in user to edit their information
     * @return bot message telling user it has updated information
     */
    public Mono<Void> editUserDetails(Message message, String[] content, User user) {
        String fieldToEdit = content[2].toLowerCase();
        String newInformation = content[3];

        //switch case to know what user information to edit
        switch (fieldToEdit) {
            case "name" -> {
                String[] nameParts = newInformation.split(" ");
                if (nameParts.length == 2 && validityChecker.checkNameValidity(content)) {
                    user.setFullName(nameParts[0] + " " + nameParts[1]);
                } else {
                    return createErrorMessage(message);
                }
            }
            case "email" -> {
                if (validityChecker.checkEmailValidity(content)) {
                    user.setEmail(newInformation);
                } else {
                    return createErrorMessage(message);
                }
            }
            case "districtid" -> {
                if (validityChecker.checkDistrictIDValidity(content)) {
                    user.setDistrictId(Long.parseLong(newInformation));
                } else {
                    return createErrorMessage(message);
                }
            }
            case "phonenumber" -> {
                if (validityChecker.checkPhoneNumberValidity(content)) {
                    user.setMobileNumber(Long.parseLong(newInformation));
                } else {
                    return createErrorMessage(message);
                }
            }
            default -> {
                return Mono.just(message)
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("I did not recognize that command. Ensure that you typed the command as stated and the information is valid."))
                        .then();
            }
        } //end switch case

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Field information updated. Please confirm your information again."))
                .then(askConfirmation(message, user));
    } //end editUserDetails()

    /**
     * confirm user details by saving to user repository
     * @param message takes in user message
     * @param user takes in user
     * @return bot message
     */
    public Mono<Void> confirmUserDetails(Message message, User user) {
        //to finalize, set user's discord ID and finished registered status
        user.setDiscordId(message.getAuthor().get().getId().asLong());
        user.setStatus(User.Status.REGISTERED);
        UserRegistrationCommand.userService.addUser(user);

        UserRegistrationCommand.registration_mode = false;

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("\nYour details have been confirmed and saved!" +
                        "\nThat concludes the membership registration process. Thank you for joining the Miramar SDCS Club!"))
                .then();
    } //end confirmUserDetails()

    /**
     * creates an error message to user when information inputted is in the wrong format
     * @param message takes in message sent by user
     * @return an error message requesting the user to try again
     */
    private Mono<Void> createErrorMessage(Message message) {

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("The information you just provided was not in the correct format or was invalid." +
                        "\nPlease refer to the command above for the proper format and try again."))
                .then();

    } //end createErrorMessage()
}
