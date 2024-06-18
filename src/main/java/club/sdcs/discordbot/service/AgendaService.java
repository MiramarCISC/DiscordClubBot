package club.sdcs.discordbot.service;

import club.sdcs.discordbot.config.DiscordClient;
import club.sdcs.discordbot.model.Agenda;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.repository.AgendaRepository;
import club.sdcs.discordbot.repository.UserRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final UserRepository userRepository;
    private final GatewayDiscordClient discordClient;


    public AgendaService(AgendaRepository agendaRepository, UserRepository userRepository, GatewayDiscordClient discordClient) {
        this.agendaRepository = agendaRepository;
        this.userRepository = userRepository;
        this.discordClient = discordClient;
    }

    public Agenda addAgenda(Agenda agenda) {
        return agendaRepository.save(agenda);
    }

    public List<Agenda> getAllAgendas() {
        return agendaRepository.findAll();
    }

    // Method to send reminders about agenda due dates
    @Scheduled() // TODO: schedule everyday
    public void sendReminders() {
        List<Agenda> agendas = agendaRepository.findAll(); // potential priority queue <LocalDateTime>
        List<User> users = userRepository.findAll(); // TODO: Change later to permitted roles (officers)
        LocalDateTime currentTime = LocalDateTime.now();


        for (Agenda agenda : agendas) {
            String message = "Reminder: The agenda for " + agenda.getTitle() + " is due on " + agenda.getDueDate();
            if (agenda.getDueDate().isEqual(currentTime.plusDays(1))) {
                for (User user : users) {
                    sendDM(user, message);
                }
            }
        }
    }

    public void sendDM(User user, String message) {
        discordClient.getUserById(Snowflake.of(user.getDiscordId()))
                .flatMap(discord4j.core.object.entity.User::getPrivateChannel)
                .flatMap(channel -> channel.createMessage(message))
                .subscribe();
    }
}
