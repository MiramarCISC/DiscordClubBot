package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.User;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MembershipService {

    @Value("${spring.discord.server-id}")
    private long guildId;

    @Value("${spring.discord.active.condition.num-meetings-attended}")
    private int requiredMeetingsAttended;

    @Value("${spring.discord.active.condition.in-past-num-days}")
    private int pastNumDays;

    private final UserService userService;
    private final GatewayDiscordClient gatewayClient;
    private static final long ONE_HOUR_MILLIS = 60L * 60 * 1000;
    private static final Logger LOG = LoggerFactory.getLogger(MembershipService.class);

    public MembershipService(UserService userService, GatewayDiscordClient gatewayClient) {
        this.userService = userService;
        this.gatewayClient = gatewayClient;
    }

    private List<User> getUsers() {
        return userService.getAllUsers();
    }

    @Scheduled(fixedRate = ONE_HOUR_MILLIS)
    public void checkUserStatus() {
        LOG.info("Checking all users...");

        List<User> users = getUsers();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (!users.isEmpty()) {
            gatewayClient.getGuildById(Snowflake.of(guildId))
                    .flatMapMany(Guild::getMembers)
                    .collectMap(Member::getId, member -> member)
                    .flatMap(memberMap -> {
                        for (User user : users) {
                            boolean isActiveVoter = user.getAttendanceLog().stream()
                                    .map(dateString -> LocalDateTime.parse(dateString, formatter))
                                    .filter(date -> Duration.between(date, now).toDays() < pastNumDays)
                                    .count() >= requiredMeetingsAttended;

                            Member member = memberMap.get(Snowflake.of(user.getDiscordId()));

                            if (member != null) {
                                if (isActiveVoter) {
                                    user.setRole(User.Role.ACTIVE);
                                    getRoleByName(guildId, "Active")
                                            .flatMap(role -> addRoleToMember(member, role))
                                            .subscribe();
                                    getRoleByName(guildId, "Voter")
                                            .flatMap(role -> addRoleToMember(member, role))
                                            .subscribe();
                                    getRoleByName(guildId, "Inactive")
                                            .flatMap(role -> removeRoleFromMember(member, role))
                                            .subscribe();
                                } else {
                                    user.setRole(User.Role.INACTIVE);
                                    getRoleByName(guildId, "Inactive")
                                            .flatMap(role -> addRoleToMember(member, role))
                                            .subscribe();
                                    getRoleByName(guildId, "Active")
                                            .flatMap(role -> removeRoleFromMember(member, role))
                                            .subscribe();
                                    getRoleByName(guildId, "Voter")
                                            .flatMap(role -> {
                                                if (member.getRoleIds().contains(role.getId())) {
                                                    return removeRoleFromMember(member, role);
                                                }
                                                return Mono.empty();
                                            })
                                            .subscribe();
                                }
                                LOG.info("Checked user {} role: {}", user, user.getRole().toString());
                                userService.addUser(user);
                            }
                        }
                        return Mono.empty();
                    })
                    .subscribe();
        } else {
            LOG.info("No users found.");
        }
    }

    private Mono<Role> getRoleByName(long guildId, String roleName) {
        return gatewayClient.getGuildById(Snowflake.of(guildId))
                .flatMap(guild -> guild.getRoles()
                        .filter(role -> role.getName().equalsIgnoreCase(roleName))
                        .next());
    }

    private Mono<Void> addRoleToMember(Member member, Role role) {
        return member.addRole(role.getId())
                .doOnSuccess(success -> LOG.info("Added role {} to member {}", role.getName(), member.getId().asString()))
                .doOnError(error -> LOG.error("Failed to add role {} to member {}", role.getName(), member.getId().asString(), error));
    }

    private Mono<Void> removeRoleFromMember(Member member, Role role) {
        return member.removeRole(role.getId())
                .doOnSuccess(success -> LOG.info("Removed role {} from member {}", role.getName(), member.getId().asString()))
                .doOnError(error -> LOG.error("Failed to remove role {} from member {}", role.getName(), member.getId().asString(), error));
    }
}
