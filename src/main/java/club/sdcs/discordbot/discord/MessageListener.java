package club.sdcs.discordbot.discord;

import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.repository.UserRepository;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.PrivateChannel;
import reactor.core.publisher.Mono;

public abstract class MessageListener {

    private User user = new User();

    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().startsWith("!user set"))
                .flatMap(message -> {
                    String[] content = message.getContent().split(" ");
                    String userInfo = content[2];
                    switch (userInfo.toLowerCase()) {
                        case "name":
                            return processName(content, user);
                        case "email":
                            return processEmail(content, user);
                        case "districtid":
                            return processDistrictID(content, user);
                        case "phonenumber":
                            return processPhone(content, user);
                        default:
                            return Mono.empty();
                    }
                })
                .then();
        }







    public Mono<Void> processName(String[] eventMessage, User user) {
        String firstName = eventMessage[3];
        String lastName = eventMessage[4];
        user.setFullName(firstName + " " + lastName);
        System.out.println(firstName + " " + lastName);
        return Mono.empty();
    }
    public Mono<Void> processEmail(String[] eventMessage, User user) {
        String email = eventMessage[3];
        user.setEmail(email);
        System.out.println(email);
        return Mono.empty();
    }
    public Mono<Void> processDistrictID(String[] eventMessage, User user) {
        long districtID = Long.parseLong(eventMessage[3]);
        user.setDistrictId(districtID);
        System.out.println(districtID);
        return Mono.empty();
    }
    public Mono<Void> processPhone(String[] eventMessage, User user) {
        long phone = Long.parseLong(eventMessage[3]);
        user.setMobileNumber(phone);
        System.out.println(phone);
        return Mono.empty();
    };
}
