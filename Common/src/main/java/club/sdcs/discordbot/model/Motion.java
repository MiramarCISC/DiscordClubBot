package club.sdcs.discordbot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="motion")
public class Motion extends Auditable {
    public enum MotionType {
        MINUTES,
        MEETING,
        GENERAL
    }

    @Id
    private long motionId;
    @OneToOne
    private User creator;
    @OneToOne
    private User second;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isPassed;

    @Enumerated(EnumType.STRING)
    private MotionType motionType;

    public Motion() {}

    public Motion(long motionId, User creator, User second, LocalDateTime startTime, LocalDateTime endTime, boolean isPassed, MotionType motionType) {
        this.motionId = motionId;
        this.creator = creator;
        this.second = second;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isPassed = isPassed;
        this.motionType = motionType;
    }

    public long getMotionId() {
        return motionId;
    }

    public void setMotionId(long motionId) {
        this.motionId = motionId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getSecond() {
        return second;
    }

    public void setSecond(User second) {
        this.second = second;
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

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public MotionType getMotionType() {
        return motionType;
    }

    public void setMotionType(MotionType motionType) {
        this.motionType = motionType;
    }

    @Override
    public String toString() {
        return "Motion{" +
                "motionId=" + motionId +
                ", creator=" + creator +
                ", second=" + second +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isPassed=" + isPassed +
                ", motionType=" + motionType +
                '}';
    }
}
