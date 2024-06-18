package club.sdcs.discordbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="meeting")
public class Meeting extends ClubEvent {
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Meeting(long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String url) {
        this.setId(id);
        this.setTitle(title);
        this.setUrl(url);
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Meeting() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
