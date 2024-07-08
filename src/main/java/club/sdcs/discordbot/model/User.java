package club.sdcs.discordbot.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name="users")
public class User extends Auditable {
    public enum Role {
        // officers
        PRESIDENT,
        VP_EXTERNAL,
        VP_INTERNAL,
        VP_OPERATIONS,
        SECRETARY,
        TREASURER,
        MARKETING_OFFICER,
        SOCIAL_MEDIA_OFFICER,
        ASG_REPRESENTATIVE,

        // non-officers
        ACTIVE,     // voting member
        INACTIVE,   // non-voting member
        ALUMNI,     // graduate, non-voting
        ADVISOR     // faculty advisor, non-voting
    }

    public enum Status {
        UNREGISTERED,
        REGISTERED
    }

    @Id
    private long discordId;
    private long districtId;
    private String fullName;
    private String discordName;
    private String email;
    private long mobileNumber;
    private Timestamp joinDate;

    // subscription flags
    private boolean subscribedToEmails;
    private boolean subscribedToSMS;

    @Enumerated(EnumType.STRING)
    private Role role;
    private Status status;

    public User(){}

    public User(long discordId, long districtId, String fullName, String discordName, String email, long mobileNumber, Timestamp joinDate, boolean subscribedToEmails, boolean subscribedToSMS, Role role, Status status) {
        this.discordId = discordId;
        this.districtId = districtId;
        this.fullName = fullName;
        this.discordName = discordName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.joinDate = joinDate;
        this.subscribedToEmails = subscribedToEmails;
        this.subscribedToSMS = subscribedToSMS;
        this.role = role;
        this.status = status;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public String getDistrictId() {

        return String.format("%010d", districtId);
    }

    public void setDistrictId(long districtId) {
        this.districtId = districtId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean isSubscribedToEmails() {
        return subscribedToEmails;
    }

    public void setSubscribedToEmails(boolean subscribedToEmails) {
        this.subscribedToEmails = subscribedToEmails;
    }

    public boolean isSubscribedToSMS() {
        return subscribedToSMS;
    }

    public void setSubscribedToSMS(boolean subscribedToSMS) {
        this.subscribedToSMS = subscribedToSMS;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }
}
