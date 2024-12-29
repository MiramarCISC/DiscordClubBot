package club.sdcs.discordbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class DiscordPoll extends Auditable {
    @Id
    private long pollId;
    private String title;
    private LocalDateTime startDate = LocalDateTime.now();
    private LocalDateTime endDate;
    private long motionedUser;

    public enum PassCondition {
        SIMPLE_MAJORITY,
        TWO_THIRDS_MAJORITY,
        UNANIMOUS,
        NO_CONDITION;
    }

    public DiscordPoll() {}

    public DiscordPoll(long pollId, String title, LocalDateTime startDate, LocalDateTime endDate, long motionedUser, PassCondition passCondition) {
        this.pollId = pollId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.motionedUser = motionedUser;
        this.passCondition = passCondition;
    }

    @Enumerated(EnumType.STRING)
    private PassCondition passCondition;

    public long getPollId() {
        return pollId;
    }

    public void setPollId(long pollId) {
        this.pollId = pollId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public long getMotionedUser() {
        return motionedUser;
    }

    public void setMotionedUser(long motionedUser) {
        this.motionedUser = motionedUser;
    }

    public PassCondition getPassCondition() {
        return passCondition;
    }

    public void setPassCondition(PassCondition passCondition) {
        this.passCondition = passCondition;
    }
}
