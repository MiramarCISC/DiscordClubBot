package club.sdcs.discordbot;

import club.sdcs.discordbot.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MembershipBot {

    public static void main(String[] args) {
        SpringApplication.run(MembershipBot.class, args);
    }

    @Bean
    public CommandLineRunner startBot(UserService userService) {
        return(args) -> {

        };
    }
}
