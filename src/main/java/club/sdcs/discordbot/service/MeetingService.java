package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting addMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getAllMeetings() {
        return (List<Meeting>) meetingRepository.findAll();
    }

    public void updateMeeting(Meeting meeting) {
        if (meetingRepository.existsById(meeting.getMeetingId())) {
            meetingRepository.save(meeting);
        } else {
            throw new IllegalArgumentException("Meeting with ID " + meeting.getMeetingId() + " does not exist. Womp Womp");
        }
    }
}
