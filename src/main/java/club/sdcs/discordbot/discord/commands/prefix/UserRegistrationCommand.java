package club.sdcs.discordbot.discord.commands.prefix;

import club.sdcs.discordbot.model.User;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRegistrationCommand implements PrefixCommand {

    // TODO: make command only reachable during the registration process
    // TODO: add exception handlers for receiving information
    // TODO: add confirmation for user to check if information is correct
    // TODO: request user to be active/voting member
    // TODO: ability to unsubscribe from emails and sms
    // TODO: record discordID to map people to their discord
    // TODO: save user to user repository

    private final User user = new User();

    @Override
    public String getName() {
        return "!user set";
    }

    @Override
    public Mono<Void> handle(Message message) {
        return Mono.just(message)
                .filter(msg -> msg.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .filter(channel -> channel instanceof PrivateChannel)
                .flatMap(channel -> {

                    String[] content = message.getContent().split(" "); //take in message command for membership registration and split up its content
                    String userInfo = content[2]; //takes in what the information the user is setting

                    //check which user info is being set and save that user information
                    return switch (userInfo.toLowerCase()) {
                        case "name" -> processName(message, content, user);
                        case "email" -> processEmail(message, content, user);
                        case "districtid" -> processDistrictID(message, content, user);
                        case "phonenumber" -> processPhone(message, content, user);
                        default -> channel.createMessage("I did not recognize that command. Ensure that you typed the command as stated.");
                    }; //end switch case

                }) //end .flatMap()
                .then(); //end return statement

    } //end handle()

    /**
     * sets the user's name
     * @param eventMessage takes in user message command (!user set name [name])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processName(Message message, String[] eventMessage, User user) {
        String firstName = eventMessage[3];
        String lastName = eventMessage[4];
        user.setFullName(firstName + " " + lastName);

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(messageChannel -> messageChannel.createMessage("Name received. Your name is set to " +
                        firstName + " " + lastName + ".\n\nFor the next step, please enter in your campus email. (!user set email [email])"))
                .then();
    }

    /**
     * sets the user's email
     * @param eventMessage takes in user message command (!user set email [email])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processEmail(Message message, String[] eventMessage, User user) {
        String email = eventMessage[3];
        user.setEmail(email);

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(messageChannel -> messageChannel.createMessage("Campus email received. Your email is set to " +
                        email + ".\n\nFor the next step, please enter in your district ID. (!user set districtID [districtID])"))
                .then();
    } //end processEmail()

    /**
     * sets the user's district ID
     * @param eventMessage takes in user message command (!user set districtID [districtID])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processDistrictID(Message message, String[] eventMessage, User user) {
        long districtID = Long.parseLong(eventMessage[3]);
        user.setDistrictId(districtID);

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(messageChannel -> messageChannel.createMessage("District ID received. Your district ID is set to " +
                        districtID + ".\n\nFor the next step, please enter in your phone number (no dashes [-]). (!user set phonenumber [phonenumber])"))
                .then();
    } //end processDistrictID()

    /**
     * sets the user's phone information
     * @param eventMessage takes in user message command (!user set phonenumber [phonenumber])
     * @param user takes in user
     * @return nothing
     */
    public Mono<Void> processPhone(Message message, String[] eventMessage, User user) {
        long phone = Long.parseLong(eventMessage[3]);
        user.setMobileNumber(phone);

        return Mono.just(message)
                .flatMap(Message::getChannel)
                .flatMap(messageChannel -> messageChannel.createMessage("Phone number received. Your phone number is set to " +
                        phone + ".\n\nThat concludes the membership registration process. Thank you for joining the Miramar SDCS Club!"))
                .then();
    } //end processPhone()

}
