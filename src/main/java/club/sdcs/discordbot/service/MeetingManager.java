package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.model.User;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Manages the scheduling and reminders for meetings
 */
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

    /**
     * Scheduled method that checks meetings every [cron] time.
     * Sends reminders to channels and direct messages if necessary.
     */
    @Scheduled(cron = "${spring.discord.scheduling}") // preferably everyday
    public void scheduledCheckMeetings() {
        checkMeetings().subscribe();
    }

    /**
     * Checks meetings and sends reminders.
     */
    public Mono<Boolean> checkMeetings() {
        return Mono.defer(() -> {
            List<Meeting.Status> statuses = List.of(Meeting.Status.SCHEDULED);
            List<Meeting> scheduledMeetings = meetingService.getMeetingsByStatuses(statuses);
            List<User> officers = userService.getOfficers();
            LocalDateTime now = LocalDateTime.now();
            boolean hasIncompleteLinks = false;

            for (Meeting meeting : scheduledMeetings) {
                LocalDateTime meetingStartTime = meeting.getStartTime();
                String reminderMsg = reminderMessage(meeting);

                if ((meeting.getAgendaLink() == null || meeting.getAgendaLink().isEmpty() ||
                        meeting.getMinutesLink() == null || meeting.getMinutesLink().isEmpty())) {
                    hasIncompleteLinks = true;

                    // Channel reminder 1 day before due date
                    if (isMeetingDueInADay(meetingStartTime, now)) {
                        sendChannelReminder(reminderMsg);
                        // DM reminder if links are not filled
                        sendDMReminder(officers, reminderMsg);
                    }
                    // Check if the meeting is due in a week
                    else if (isMeetingDueInAWeek(meetingStartTime, now) || isMeetingCreatedWithinAWeekOfStart(meetingStartTime, now)) {
                        sendChannelReminder(reminderMsg);
                    }
                }
            }

            return Mono.just(hasIncompleteLinks);
        });
    }

    // Checks if the meeting was created within a week of its start time
    private boolean isMeetingCreatedWithinAWeekOfStart(LocalDateTime meetingStartTime, LocalDateTime now) {
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        return meetingStartTime.isAfter(oneWeekAgo) && meetingStartTime.isBefore(now);
    }

    // Checks if the meeting is due in a week
    private boolean isMeetingDueInAWeek(LocalDateTime meetingStartTime, LocalDateTime now) {
        LocalDateTime oneWeekFromNow = now.plusWeeks(1);
        return meetingStartTime.isAfter(now) && meetingStartTime.isBefore(oneWeekFromNow);
    }

    // Checks if the meeting is due in a day
    private boolean isMeetingDueInADay(LocalDateTime meetingStartTime, LocalDateTime now) {
        LocalDateTime oneDayFromNow = now.plusDays(1);
        return meetingStartTime.isAfter(now) && meetingStartTime.isBefore(oneDayFromNow);
    }

    private String reminderMessage(Meeting meeting) {
        StringBuilder message = new StringBuilder();
        String agendaLinkString = "<" + meeting.getAgendaLink() + ">";
        String minutesLinkString = "<" + meeting.getMinutesLink() + ">";

        if (meeting.getAgendaLink() == null || meeting.getAgendaLink().isEmpty()) {
            message.append("- Agenda link has not been entered ❌ \n");
        } else {
            message.append("- Ensure agenda doc is filled: ").append(agendaLinkString).append(" ✅\n");
        }

        if (meeting.getMinutesLink() == null || meeting.getMinutesLink().isEmpty()) {
            message.append("- Minutes link has not been entered ❌\n");
        } else {
            message.append("- Ensure minutes doc is filled: ").append(minutesLinkString).append(" ✅");
        }

        return "⏳ Reminder: " + meeting.getName() + "'s agenda/minutes are due before " + meeting.getFormatStartTime()
                + "\n- `!meeting list` to display list/edit links of scheduled/active meetings\n" + message;
    }

    // Sends a direct message reminder to the specified users
    private void sendDMReminder(List<User> users, String message) {
        users.forEach(user -> client.getUserById(Snowflake.of(user.getDiscordId()))
                .flatMap(discord4j.core.object.entity.User::getPrivateChannel)
                .flatMap(channel -> channel.createMessage(message))
                .subscribe());
    }

    // Sends a reminder to the specified channel
    private void sendChannelReminder(String message) {
        client.getChannelById(Snowflake.of(CHANNEL_ID))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.createMessage(message))
                .subscribe();
    }
}
