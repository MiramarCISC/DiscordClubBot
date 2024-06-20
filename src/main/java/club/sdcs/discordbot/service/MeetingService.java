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
}
