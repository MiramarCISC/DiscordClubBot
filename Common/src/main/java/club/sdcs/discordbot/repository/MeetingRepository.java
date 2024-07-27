package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.Meeting;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    Meeting findById(long id);
    List<Meeting> findByStatus(Meeting.Status status);
    List<Meeting> findByStatusIn(List<Meeting.Status> statuses);
}
