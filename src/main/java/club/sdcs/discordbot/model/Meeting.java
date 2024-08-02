package club.sdcs.discordbot.model;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="meeting")
public class Meeting extends Auditable {

    public enum Status {
        SCHEDULED,
        ACTIVE,
        COMPLETED,
        CANCELED
    }
    @Id
    private long meetingId;
    private String name;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String agendaLink;
    private String minutesLink;
    private boolean isQuorumMet;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Meeting() {}

    public Meeting(long meetingId, String name, String description, String location, LocalDateTime startTime, LocalDateTime endTime, String agendaLink, String minutesLink, boolean isQuorumMet, Status status) {
        this.meetingId = meetingId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.agendaLink = agendaLink;
        this.minutesLink = minutesLink;
        this.isQuorumMet = isQuorumMet;
        this.status = status;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(long meetingId) {
        this.meetingId = meetingId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getFormatStartTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, MM-dd-yyyy");
        return startTime.format(formatter);
    }

    public void setStartTime(LocalDateTime timeStart) {
        this.startTime = timeStart;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getFormatEndTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, MM-dd-yyyy");
        return endTime.format(formatter);
    }

    public void setEndTime(LocalDateTime timeEnd) {
        this.endTime = timeEnd;
    }

    public String getAgendaLink() {
        return agendaLink;
    }

    public void setAgendaLink(String agendaLink) {
        this.agendaLink = agendaLink;
    }

    public String getMinutesLink() {
        return minutesLink;
    }

    public void setMinutesLink(String minutesLink) {
        this.minutesLink = minutesLink;
    }

    public boolean isQuorumMet() {
        return isQuorumMet;
    }

    public void setQuorumMet(boolean quorumMet) {
        isQuorumMet = quorumMet;
    }

    public Status getStatus() {
        return status;
    }

    // Discord API status is represented by integers 1-4
    public void setStatus(int status) {
        switch (status) {
            case 1:
                this.status = Status.SCHEDULED;
                break;
            case 2:
                this.status = Status.ACTIVE;
                break;
            case 3:
                this.status = Status.COMPLETED;
                break;
            case 4:
                this.status = Status.CANCELED;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingId=" + meetingId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", timeStart=" + startTime +
                ", timeEnd=" + endTime +
                ", agendaLink='" + agendaLink + '\'' +
                ", minutesLink='" + minutesLink + '\'' +
                ", isQuorumMet=" + isQuorumMet +
                ", status=" + status +
                '}';
    }

    private EmbedCreateSpec.Builder createEmbedBuilder() {
        Color embedColor = (status == Status.ACTIVE) ? Color.GREEN : Color.GRAY;
        String startTime = this.startTime != null ? getFormatStartTime() : "N/A" ;
        String endTime = this.endTime != null ? getFormatEndTime() : "N/A";

        return EmbedCreateSpec.builder()
                .title(name)
                .color(embedColor)
                .addField("Description", description, true)
                .addField("Location", location, true)
                .addField("Time", startTime + " - " + endTime, false)
                .addField("Agenda Link", agendaLink != null ? agendaLink : "N/A", false)
                .addField("Minutes Link", minutesLink != null ? minutesLink : "N/A", false)
                .addField("Status", String.valueOf(status), true)
                .addField("Quorum Met", isQuorumMet ? "Yes" : "No", true);
    }

    // Non-interactive message
    public EmbedCreateSpec toDiscordFormatEmbed() {
        return createEmbedBuilder().build();
    }

    // Allows Discord buttons/interactions
    public MessageCreateSpec toDiscordFormatMessage() {
        EmbedCreateSpec embed = createEmbedBuilder().build();
        return MessageCreateSpec.builder()
                .addEmbed(embed)
                .addComponent(ActionRow.of(Button.primary("meeting-" + meetingId, // unique button per meeting obj
                        "input agenda/minutes link")))
                .build();
    }
}
