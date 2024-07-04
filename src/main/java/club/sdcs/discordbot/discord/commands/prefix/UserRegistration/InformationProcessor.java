package club.sdcs.discordbot.discord.commands.prefix.UserRegistration;

import club.sdcs.discordbot.model.User;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

import club.sdcs.discordbot.service.UserService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.GuildMemberEditSpec;
import reactor.core.publisher.Mono;

public class InformationProcessor {

    private final UserService userService;
    private final ValidityChecker validityChecker = new ValidityChecker();
    private final long guildID = 1252368620047044648L;

    public InformationProcessor(UserService userService) {
        this.userService = userService;
    }

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

            String description = "Your name is set to **" + firstName + " " + lastName + "**.\n\n" +
                    "**Next Step:**\n" +
                    "Please enter your **campus email** using the command:\n" +
                    "`!user setEmail [email]`" +
                    "\n\n\n**Example:**\n";

            String exampleImage = "https://i.imgur.com/mPjpVTV.png";

            return EmbedUtils.createEmbedMessage(message, "Name Received", description, exampleImage);
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
        String email = eventMessage[2];
        if (validityChecker.checkEmailValidity(email)) {
            user.setEmail(email);

            String description = "Your email is set to **" + email + "**.\n\n" +
                    "**Next Step:**\n" +
                    "Please enter your **district ID** using the command:\n" +
                    "`!user setDistrictID [districtID]`" +
                    "\n\n\n**Example:**\n";

            String exampleImage = "https://i.imgur.com/kTOb1GH.png";

            return EmbedUtils.createEmbedMessage(message, "Email Received", description, exampleImage);
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
        String districtID = eventMessage[2];
        if (validityChecker.checkDistrictIDValidity(districtID)) {
            user.setDistrictId(Long.parseLong(districtID));

            String description = "Your district ID is set to **" + districtID + "**.\n\n" +
                    "**Next Step:**\n" +
                    "Please enter your **phone number** (no dashes) using the command:\n" +
                    "`!user setPhoneNumber [phone number]`" +
                    "\n\n\n**Example:**\n";

            String exampleImage = "https://i.imgur.com/FIAHvNc.png";

            return EmbedUtils.createEmbedMessage(message, "District ID Received", description, exampleImage);

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
        String phoneNumber = eventMessage[2];
        if (validityChecker.checkPhoneNumberValidity(phoneNumber)) {
            user.setMobileNumber(Long.parseLong(phoneNumber));

            String description = "Your phone number is set to **" + phoneNumber + "**.\n\n" +
                    "**As the last step of the registration process, please indicate your membership status:** " +
                    "Become an **active** member of the club?\n\n" +
                    "**Active Membership Requirements & Benefits:**\n" +
                    "- Participate in a meeting at least once a month\n" +
                    "- Ability to vote for officers of the club\n\n" +
                    "If this interests you, use the command:\n" +
                    "`!user request active` to become an **active** member of the club.\n\n" +
                    "Otherwise, use the command:\n" +
                    "`!user request inactive` to become an **inactive** member of the club.";

            return EmbedUtils.createEmbedMessage(message, "Phone Number Received", description);

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

            return message.getClient().getGuildById(Snowflake.of(guildID))
                    .flatMap(guild -> guild.getMemberById(Snowflake.of(message.getAuthor().get().getId().asString())))
                    .flatMap(member -> member.edit(GuildMemberEditSpec.builder().addRole(Snowflake.of(1254855153857859656L)).build())) // CHANGE ROLE ID
                    .then(askConfirmation(message, user));

        } else if (requestedRole.equals("inactive")) {
            user.setRole(User.Role.INACTIVE);

            return message.getClient().getGuildById(Snowflake.of(guildID))
                    .flatMap(guild -> guild.getMemberById(Snowflake.of(message.getAuthor().get().getId().asString())))
                    .flatMap(member -> member.edit(GuildMemberEditSpec.builder().addRole(Snowflake.of(1255218703922888705L)).build())) //CHANGE ROLE ID
                    .then(askConfirmation(message, user));


        } else {
            return EmbedUtils.createEmbedMessage(message, "Invalid Role", "I did not recognize that role. Refer to the instructions given previously.");

        }

    } //end assignRoleToUser()

    /**
     * asks user to confirm their details
     * @param message takes in message to find message channel
     * @param user takes in user
     * @return confirmation message
     */
    private Mono<Void> askConfirmation(Message message, User user) {
        String confirmationMessage = "\nName: **" + user.getFullName() +
                "**\nEmail: **" + user.getEmail() +
                "**\nDistrict ID: **" + user.getDistrictId() +
                "**\nPhone number: **" + user.getMobileNumber() +
                "**\nRole Assigned: **" + user.getRole() +
                "**\n\nType `!user confirm` to **confirm** this user information.\nType `!user edit [field] [information]` to **edit** a specific field before finalizing.\n\n\n**Example:**\n";

        String exampleImage = "https://i.imgur.com/4vmaCzq.png";

        return EmbedUtils.createEmbedMessage(message, "Confirm Your Details", confirmationMessage, exampleImage);

    } //end askConfirmation()

    /**
     * edits user details
     * @param message takes in user message to know what channel to send to
     * @param content takes in split up message content to change user information
     * @param user takes in user to edit their information
     * @return bot message telling user it has updated information
     */
    public Mono<Void> editUserDetails(Message message, String[] content, User user) {
        if (content.length < 4) {
            return EmbedUtils.createEmbedMessage(message, "Invalid Command", "Please provide the correct information.");
        }

        String fieldToEdit = content[2].toLowerCase();
        String newInformation = String.join(" ", Arrays.copyOfRange(content, 3, content.length));

        //switch case to know what user information to edit
        switch (fieldToEdit) {
            case "name" -> {
                String[] nameParts = newInformation.split(" ");
                if (validityChecker.checkNameValidity(content)) {
                    user.setFullName(nameParts[0] + " " + nameParts[1]);
                } else {
                    return createErrorMessage(message);
                }
            }
            case "email" -> {
                if (validityChecker.checkEmailValidity(newInformation)) {
                    user.setEmail(newInformation);
                } else {
                    return createErrorMessage(message);
                }
            }
            case "districtid" -> {
                if (validityChecker.checkDistrictIDValidity(newInformation)) {
                    user.setDistrictId(Long.parseLong(newInformation));
                } else {
                    return createErrorMessage(message);
                }
            }
            case "phonenumber" -> {
                if (validityChecker.checkPhoneNumberValidity(newInformation)) {
                    user.setMobileNumber(Long.parseLong(newInformation));
                } else {
                    return createErrorMessage(message);
                }
            }
            case "role" -> {
                if (newInformation.equalsIgnoreCase("active")) {
                    user.setRole(User.Role.ACTIVE);
                } else if (newInformation.equalsIgnoreCase("inactive")) {
                    user.setRole(User.Role.INACTIVE);
                } else {
                    return createErrorMessage(message);
                }
            }
            default -> {
                return EmbedUtils.createEmbedMessage(message, "Invalid Command", "I did not recognize that command. Ensure that you typed the command as **stated** and the information is **valid**.");
            }
        } // end switch case

        return EmbedUtils.createEmbedMessage(message, "Field Information Updated", "Field information updated. Please **confirm** your information again.")
                .then(askConfirmation(message, user));
    } // end editUserDetails()


    /**
     * confirm user details by saving to user repository
     * @param message takes in user message
     * @param user takes in user
     * @return bot message
     */
    public Mono<Void> confirmUserDetails(Message message, User user) {
        //to finalize, set user's discord ID/name, join date, and finished registered status
        user.setDiscordId(message.getAuthor().get().getId().asLong());
        user.setDiscordName(message.getAuthor().get().getUsername());
        user.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        user.setStatus(User.Status.REGISTERED);

        userService.addUser(user);
        UserRegistrationCommand.registration_mode = false;

        return EmbedUtils.createEmbedMessage(message, "Registration Complete", "\nYour details have been **confirmed** and **saved!**" +
                "\n\nThat concludes the membership registration process.\nThank you for joining the **Miramar SDCS Club!**");

    } //end confirmUserDetails()

    /**
     * creates an error message to user when information inputted is in the wrong format
     * @param message takes in message sent by user
     * @return an error message requesting the user to try again
     */
    private Mono<Void> createErrorMessage(Message message) {

        return EmbedUtils.createEmbedMessage(message, "Error Received", "The information you just provided was not in the correct format or was invalid." +
                "\nPlease refer to the command above for the proper format and try again.");

    } //end createErrorMessage()

}
