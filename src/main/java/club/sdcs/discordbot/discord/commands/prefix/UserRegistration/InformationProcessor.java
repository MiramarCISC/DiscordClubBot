package club.sdcs.discordbot.discord.commands.prefix.UserRegistration;

import club.sdcs.discordbot.model.User;
import discord4j.core.object.entity.Message;
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
                    .flatMap(messageChannel -> messageChannel.createMessage("Name received. Your name is set to " +
                            firstName + " " + lastName + ".\n\nFor the next step, please enter in your campus email. (!user setEmail [email])"))
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
                    .flatMap(messageChannel -> messageChannel.createMessage("Campus email received. Your email is set to " +
                            email + ".\n\nFor the next step, please enter in your district ID. (!user setDistrictID [districtID])"))
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
                    .flatMap(messageChannel -> messageChannel.createMessage("District ID received. Your district ID is set to " +
                            districtID + ".\n\nFor the next step, please enter in your phone number (no dashes [-]). (!user setPhoneNumber [phonenumber])"))
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
                    .flatMap(messageChannel -> messageChannel.createMessage("Phone number received. Your phone number is set to " +
                            phone + ".\n\nThe registration process is now complete. Please confirm if the following information is correct."))
                    .then(askConfirmation(message, user));
        }

        return createErrorMessage(message);
    } //end processPhone()

    /**
     * asks user to confirm their details
     * @param message takes in message to find message channel
     * @param user takes in user
     * @return confirmation message
     */
    private Mono<Void> askConfirmation(Message message, User user) {
        String confirmationMessage = "\nName: " + user.getFullName() +
                "\nEmail: " + user.getEmail() +
                "\nDistrict ID: " + user.getDistrictId() +
                "\nPhone number: " + user.getMobileNumber() +
                "\n\nType '!user confirm' to confirm this user information or '!user edit [field] [information]' to edit a specific field.";

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
                if (nameParts.length == 2) {
                    user.setFullName(nameParts[0] + " " + nameParts[1]);
                }
            }
            case "email" -> user.setEmail(newInformation);
            case "districtid" -> user.setDistrictId(Long.parseLong(newInformation));
            case "phonenumber" -> user.setMobileNumber(Long.parseLong(newInformation));
            default -> {
                return Mono.just(message)
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("I did not recognize that command. Ensure that you typed the command as stated."))
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
        //to finalize, set user's discord ID
        user.setDiscordId(message.getAuthor().get().getId().asLong());

        //TODO: save user to repository

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
                .flatMap(channel -> channel.createMessage("The information you just provided was not in the correct format." +
                        "\nPlease refer to the command above for the proper format and try again."))
                .then();

    } //end createErrorMessage()
}
