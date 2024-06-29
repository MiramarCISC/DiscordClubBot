package club.sdcs.discordbot.discord.commands.prefix.UserRegistration;

import club.sdcs.discordbot.discord.commands.prefix.PrefixCommand;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.PrivateChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Component
public class UserRegistrationCommand implements PrefixCommand {

    // TODO: ability to unsubscribe from emails and sms

    public static UserService userService;
    public static boolean registration_mode = false;
    private final HashMap<String, User> users = new HashMap<>();
    private static User currentUser = new User();

    public UserRegistrationCommand(UserService userService) {
        UserRegistrationCommand.userService = userService;
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
                    String discordID = message.getAuthor().get().getId().asString();
                    currentUser = users.computeIfAbsent(discordID, id -> new User());

                    InformationProcessor informationProcessor = new InformationProcessor();
                    String[] content = message.getContent().split(" "); //take in message command for membership registration and split up its content
                    String userInfo = content[1]; //takes in what the information the user is setting

                    //check which user info is being set and save that user information
                    if (registration_mode) {
                        return switch (userInfo.toLowerCase()) {
                            case "setname" -> informationProcessor.processName(message, content, currentUser);
                            case "setemail" -> informationProcessor.processEmail(message, content, currentUser);
                            case "setdistrictid" -> informationProcessor.processDistrictID(message, content, currentUser);
                            case "setphonenumber" -> informationProcessor.processPhone(message, content, currentUser);
                            case "confirm" -> informationProcessor.confirmUserDetails(message, currentUser);
                            case "edit" -> informationProcessor.editUserDetails(message, content, currentUser);
                            case "request" -> informationProcessor.assignRoleToUser(message, content, currentUser);
                            default ->
                                    channel.createMessage("I did not recognize that command. Ensure that you typed the command as stated.");
                        }; //end switch case
                    } else {
                        return Mono.empty();
                    }

                }) //end .flatMap()
                .then(); //end return statement

    } //end handle()
} //end UserRegistrationCommand class
