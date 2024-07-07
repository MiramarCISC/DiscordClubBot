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

    public final UserService userService;

    public UserRegistrationCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "!user";
    }

    @Override
    public Mono<Void> handle(Message message) {
        return Mono.empty();
    } //end handle()

} //end UserRegistrationCommand class
