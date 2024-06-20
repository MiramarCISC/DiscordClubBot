package club.sdcs.discordbot.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name="meeting")
public class Meeting {

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
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private String agendaLink;
    private String minutesLink;
    private boolean isQuorumMet;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Meeting() {}

    public Meeting(long meetingId, String name, String description, String location, LocalTime timeStart, LocalTime timeEnd, String agendaLink, String minutesLink, boolean isQuorumMet, Status status) {
        this.meetingId = meetingId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
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

    public LocalTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalTime timeEnd) {
        this.timeEnd = timeEnd;
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
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", agendaLink='" + agendaLink + '\'' +
                ", minutesLink='" + minutesLink + '\'' +
                ", isQuorumMet=" + isQuorumMet +
                ", status=" + status +
                '}';
    }
}
