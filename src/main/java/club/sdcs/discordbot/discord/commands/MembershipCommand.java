package club.sdcs.discordbot.discord.commands;

import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MembershipCommand implements Command {

    private final UserService userService;

    public MembershipCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getName() {
        return "!membership";
    } //end getName()

    @Override
    public Mono<Void> handle(Message message) {
        User user = message.getAuthor().orElse(null);
        if (user != null) {
            return user.getPrivateChannel()
                    .flatMap(channel -> channel.createMessage("Apply for Membership to SDCS Club? [Y] / [N]"))
                    .then(handleUserResponse(user));
        }

        return Mono.empty();
    } //end handle()

    private Mono<Void> handleUserResponse(User user) {
        // TODO: register members including name, student ID, campus email, phone number for sms,
        // requesting to be active, requesting to be voting member, ability to unsubscribe from emails and sms
        // also record discordId to map people to their discord users

        return Mono.empty();
    } //end createMembershipApplication()
}
