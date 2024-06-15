package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.Agenda;
import club.sdcs.discordbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {
    public Agenda findById(long id);
}
