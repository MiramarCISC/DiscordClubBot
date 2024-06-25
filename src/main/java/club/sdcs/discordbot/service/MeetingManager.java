package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.model.User;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MeetingManager {
    private final GatewayDiscordClient client;
    private final MeetingService meetingService;
    private final UserService userService;

    public MeetingManager(MeetingService meetingService, GatewayDiscordClient client, UserService userService) {
        this.meetingService = meetingService;
        this.client = client;
        this.userService = userService;
    }

    @Scheduled(fixedRateString = "${spring.scheduling.fixedRate}") // every 2 hours
    public void checkMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        LocalDateTime now = LocalDateTime.now();
        List<User> officers = userService.getOfficers();

        for (Meeting meeting : meetings) {
            if (meeting.getStartTime().isAfter(now) && !meeting.isQuorumMet()) {
                sendDMReminder(meeting, officers);
            }
        }
    }

    @Scheduled(cron = "${spring.scheduling.cron}")
    public void remindAgendaDue() {
    }

    @Scheduled(cron = "${spring.scheduling.cron}")
    public void remindMinutesDue() {
    }

    private void sendDMReminder(Meeting meeting, List<User> users) {
        for (User user : users) {
            client.getUserById(Snowflake.of(user.getDiscordId()))
                    .flatMap(discord4j.core.object.entity.User::getPrivateChannel)
                    .flatMap(channel -> channel.createMessage("Reminder: " + "Meeting " + meeting.getName()
                            + " is due before " + meeting.getStartTime()))
                    .subscribe();
        }
    }

    private void sendChannelReminder(String channelId) {
    }
}
