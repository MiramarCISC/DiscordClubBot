package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.Meeting;
import org.springframework.data.repository.CrudRepository;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    public Meeting findById(long id);
}
