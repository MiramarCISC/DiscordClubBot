package club.sdcs.discordbot.discord.commands;


import club.sdcs.discordbot.model.Agenda;
import club.sdcs.discordbot.service.AgendaService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AgendaCommand implements Command {
    private final AgendaService agendaService;

    public AgendaCommand(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @Override
    public String getName() {
        return "!agenda";
    }

    @Override
    public Mono<Void> handle(Message message) {
        return message.getChannel()
                .flatMap(channel -> channel.createMessage(agendaList()))
                .then();
    }

    private String agendaList() {
        List<Agenda> agendas = agendaService.getAllAgendas();
        StringBuilder agendaList = new StringBuilder();
        for (Agenda agenda : agendas) {
            agendaList.append(agenda.getLink()).append("\n");
        }
        return agendaList.toString();
    }
}
