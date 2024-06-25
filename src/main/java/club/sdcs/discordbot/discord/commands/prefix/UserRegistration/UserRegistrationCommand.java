package club.sdcs.discordbot.discord.commands.prefix.UserRegistration;

import club.sdcs.discordbot.discord.commands.prefix.PrefixCommand;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.PrivateChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRegistrationCommand implements PrefixCommand {

    // TODO: make user registration command only reachable during the registration process
    // TODO: ability to unsubscribe from emails and sms
    // TODO: save user to user repository

    private final UserService userService;
    private final User user = new User();

    public UserRegistrationCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "!user";
    }

    @Override
    public Mono<Void> handle(Message message) {

        return Mono.just(message)
                .filter(msg -> msg.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .filter(channel -> channel instanceof PrivateChannel)
                .flatMap(channel -> {

                    InformationProcessor informationProcessor = new InformationProcessor();
                    String[] content = message.getContent().split(" "); //take in message command for membership registration and split up its content
                    String userInfo = content[1]; //takes in what the information the user is setting

                    //check which user info is being set and save that user information
                    return switch (userInfo.toLowerCase()) {
                        case "setname" -> informationProcessor.processName(message, content, user);
                        case "setemail" -> informationProcessor.processEmail(message, content, user);
                        case "setdistrictid" -> informationProcessor.processDistrictID(message, content, user);
                        case "setphonenumber" -> informationProcessor.processPhone(message, content, user);
                        case "confirm" -> informationProcessor.confirmUserDetails(message, user);
                        case "edit" -> informationProcessor.editUserDetails(message, content, user);
                        case "request" -> informationProcessor.assignRoleToUser(message, content, user);
                        default -> channel.createMessage("I did not recognize that command. Ensure that you typed the command as stated.");
                    }; //end switch case

                }) //end .flatMap()
                .then(); //end return statement

    } //end handle()
} //end UserRegistrationCommand class
