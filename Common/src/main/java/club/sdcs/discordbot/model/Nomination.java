package club.sdcs.discordbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Nomination extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long nominationId;
    @ManyToOne
    private User nominator;
    @ManyToOne
    private User second;
    @ManyToOne
    private User nominee;
    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime;
    private boolean isPassed = false;
    private User.Role role;
    private long messageId;

    public Nomination() {}

    public Nomination(long nominationId, User nominator, User second, User nominee, LocalDateTime startTime, LocalDateTime endTime, boolean isPassed, User.Role role) {
        this.nominationId = nominationId;
        this.nominator = nominator;
        this.second = second;
        this.nominee = nominee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isPassed = isPassed;
        this.role = role;
    }

    public long getNominationId() {
        return nominationId;
    }

    public void setNominationId(long nominationId) {
        this.nominationId = nominationId;
    }

    public User getNominator() {
        return nominator;
    }

    public void setNominator(User nominator) {
        this.nominator = nominator;
    }

    public User getSecond() {
        return second;
    }

    public void setSecond(User second) {
        this.second = second;
    }

    public User getNominee() {
        return nominee;
    }

    public void setNominee(User nominee) {
        this.nominee = nominee;
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

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "Nomination{" +
                "nominationId=" + nominationId +
                ", nominator=" + nominator +
                ", second=" + second +
                ", nominee=" + nominee +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isPassed=" + isPassed +
                ", role=" + role +
                '}';
    }
}