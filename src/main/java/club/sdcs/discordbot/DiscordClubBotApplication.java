package club.sdcs.discordbot;

import club.sdcs.discordbot.model.Agenda;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.AgendaService;
import club.sdcs.discordbot.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableJpaAuditing
public class DiscordClubBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordClubBotApplication.class, args);
    }

    @Bean
    public CommandLineRunner startBot(UserService userService, AgendaService agendaService) {
        return(args) -> {
            long discordId = 341721074813108225L; // change to your Discord ID or agendaService.sendReminders() will DM me
            User user = userService.addUser(new User(discordId, 5394384L, "Andrew Huang", "andrew_huang", "ahuang@sdccd.edu", 3104398858L, Timestamp.valueOf("2022-08-08 00:00:00"), true, true, User.Role.ADVISOR));

            LocalDateTime currentTime = LocalDateTime.now();
            Agenda agenda1 = new Agenda(4657468457L, "Test Event 1", "https://example.com/link1", currentTime.plusDays(1));
            agendaService.addAgenda(agenda1);
            Agenda agenda2 = new Agenda(4657468458L, "Test Event 2", "https://example.com/link2", currentTime.plusDays(2));
            agendaService.addAgenda(agenda2);
            Agenda agenda3 = new Agenda(4657468459L, "Test Event 3", "https://example.com/link3", currentTime.plusDays(3));
            agendaService.addAgenda(agenda3);

            //agendaService.sendReminders();

        };
    }
}
