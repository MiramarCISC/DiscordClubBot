package club.sdcs.discordbot.service;

import club.sdcs.discordbot.repository.MeetingRepository;
import org.springframework.stereotype.Service;

@Service
public class MeetingService {
    private MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }
}
