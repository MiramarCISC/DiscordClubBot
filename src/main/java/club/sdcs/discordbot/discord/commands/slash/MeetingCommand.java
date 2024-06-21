package club.sdcs.discordbot.discord.commands.slash;



import club.sdcs.discordbot.model.Meeting;
import club.sdcs.discordbot.service.MeetingService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MeetingCommand implements SlashCommand {
    private final MeetingService meetingService;

    public MeetingCommand(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public String getName() {
        return "meeting";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        List<Meeting> meetings = meetingService.getAllMeetings();
        StringBuilder meetingsMessage = new StringBuilder("**List of Meetings:**\n\n");
        System.out.println("meeting");
        for (Meeting meeting : meetings) {
            meetingsMessage.append(String.format("**%s**\n- **Description**: %s\n- **Location**: %s\n- **Start Time**: %s\n- **End Time**: %s\n- **Agenda**: [Link](%s)\n\n",
                    meeting.getName(),
                    meeting.getDescription(),
                    meeting.getLocation(),
                    meeting.getStartTime(),
                    meeting.getEndTime() != null ? meeting.getEndTime() : "No end time",
                    meeting.getAgendaLink()));
        }

        return event.reply()
                .withContent(meetingsMessage.toString());
    }
}
