package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.Motion;
import org.springframework.data.repository.CrudRepository;

public interface MotionRepository extends CrudRepository<Motion, Long> {
}
