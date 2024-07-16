package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.Nomination;
import org.springframework.data.repository.CrudRepository;

public interface NominationRepository extends CrudRepository<Nomination, Long> {
}
