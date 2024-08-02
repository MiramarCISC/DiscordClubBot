package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NominationRepository extends JpaRepository<Nomination, Long> {
    public Nomination findByNominationId(Long id);
    public Nomination findByMessageId(long messageId);
}
