package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
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

    public Meeting findMeetingById(long id) {
        return meetingRepository.findById(id);
    }

    public List<Meeting> getMeetingsByStatus(Meeting.Status status) {
        return meetingRepository.findByStatus(status);
    }

    public List<Meeting> getMeetingsByStatuses(List<Meeting.Status> statuses) {
        return meetingRepository.findByStatusIn(statuses);
    }

    public void updateMeeting(Meeting meeting) {
        if (meetingRepository.existsById(meeting.getMeetingId())) {
            meetingRepository.save(meeting);
        } else {
            throw new IllegalArgumentException("Meeting with ID " + meeting.getMeetingId() + " does not exist.");
        }
    }

    public List<User> findUserAttendanceByMeetingId(long id) {
        return meetingRepository.findById(id).getUserAttendance();
    }
}
