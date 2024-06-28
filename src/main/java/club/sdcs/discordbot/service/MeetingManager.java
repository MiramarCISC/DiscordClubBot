package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.model.User;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MeetingManager {
    private final GatewayDiscordClient client;
    private final MeetingService meetingService;
    private final UserService userService;

    @Value("${spring.discord.officer-channel-id}")
    private String CHANNEL_ID;

    public MeetingManager(MeetingService meetingService, GatewayDiscordClient client, UserService userService) {
        this.meetingService = meetingService;
        this.client = client;
        this.userService = userService;
    }

    @Scheduled(cron = "${spring.scheduling.cron}") // every day
    public void checkMeetings() {
        List<Meeting.Status> statuses = List.of(Meeting.Status.SCHEDULED);
        List<Meeting> scheduledMeetings = meetingService.getMeetingsByStatuses(statuses);
        List<User> officers = userService.getOfficers();
        LocalDateTime now = LocalDateTime.now();

        scheduledMeetings.forEach(meeting -> {
            LocalDateTime meetingStartTime = meeting.getStartTime();
            String reminderMsg = reminderMessage(meeting);

            // Channel reminder 1 week before due date
            if (isMeetingDueInAWeek(meetingStartTime, now)) {
                sendChannelReminder(reminderMsg);
            }
            // Channel reminder 1 day before due date
            if (isMeetingDueInADay(meetingStartTime, now)) {
                sendChannelReminder(reminderMsg);
                // DM reminder if links are not filled
                if (meeting.getAgendaLink() == null || meeting.getAgendaLink().isEmpty() ||
                        meeting.getMinutesLink() == null || meeting.getMinutesLink().isEmpty()) {
                    sendDMReminder(officers, reminderMsg);
                }
            }
        });
    }

    private String reminderMessage(Meeting meeting) {
        StringBuilder message = new StringBuilder();

        if (meeting.getAgendaLink() == null || meeting.getAgendaLink().isEmpty()) {
            message.append("- Agenda link has not been entered ❌ \n");
        } else {
            message.append("- Ensure agenda doc is filled: ").append(meeting.getAgendaLink()).append(" ✅\n");
        }

        if (meeting.getMinutesLink() == null || meeting.getMinutesLink().isEmpty()) {
            message.append("- Minutes link has not been entered ❌\n");
        } else {
            message.append("- Ensure minutes doc is filled: ").append(meeting.getMinutesLink()).append(" ✅");
        }

        return "⏳ Reminder: " + meeting.getName() + "'s agenda/minutes are due before " + meeting.getFormatStartTime()
                + "\n- `!meeting list` to display list/edit links of scheduled/active meetings\n" + message;
    }

    private boolean isMeetingDueInAWeek(LocalDateTime meetingStartTime, LocalDateTime now) {
        LocalDateTime oneWeekFromNow = now.plusWeeks(1);
        return meetingStartTime.isAfter(now) && meetingStartTime.isBefore(oneWeekFromNow);
    }

    private boolean isMeetingDueInADay(LocalDateTime meetingStartTime, LocalDateTime now) {
        LocalDateTime oneDayFromNow = now.plusDays(1);
        return meetingStartTime.isAfter(now) && meetingStartTime.isBefore(oneDayFromNow);
    }

    private void sendDMReminder(List<User> users, String message) {
        users.forEach(user -> client.getUserById(Snowflake.of(user.getDiscordId()))
                .flatMap(discord4j.core.object.entity.User::getPrivateChannel)
                .flatMap(channel -> channel.createMessage(message))
                .subscribe());
    }

    private void sendChannelReminder(String message) {
        client.getChannelById(Snowflake.of(CHANNEL_ID))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.createMessage(message))
                .subscribe();
    }
}
