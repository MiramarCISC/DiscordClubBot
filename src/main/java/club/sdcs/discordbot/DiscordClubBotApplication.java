package club.sdcs.discordbot;

import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.sql.Timestamp;

@SpringBootApplication
@EnableJpaAuditing
public class DiscordClubBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordClubBotApplication.class, args);
    }

    @Bean
    public CommandLineRunner startBot(UserService userService) {
        return(args) -> {
            long discordId = 341721074813108225L; // change to your Discord ID or agendaService.sendReminders() will DM me
            User user = userService.addUser(new User(discordId, 5394384L, "Andrew Huang", "andrew_huang", "ahuang@sdccd.edu", 3104398858L, Timestamp.valueOf("2022-08-08 00:00:00"), true, true, User.Role.ADVISOR));

        };
    }
}
