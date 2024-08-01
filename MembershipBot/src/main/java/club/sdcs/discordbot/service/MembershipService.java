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

    private final UserService userService;
    private final GatewayDiscordClient gatewayClient;
    private static final long ONE_MONTH_MILLIS = 30L * 24 * 60 * 60 * 1000;
    private static final long ONE_HOUR_MILLIS = 60L * 60 * 1000;
    private static final Logger LOG = LoggerFactory.getLogger(MembershipService.class);

    @Value("${spring.discord.server-id}")
    private long guildId;

    public MembershipService(UserService userService, GatewayDiscordClient gatewayClient) {
        this.userService = userService;
        this.gatewayClient = gatewayClient;
    }

    private List<User> getUsers() {
        return userService.getAllActiveUsers();
    }

    @Scheduled(fixedRate = ONE_HOUR_MILLIS)
    public void checkActiveUserStatus() {
        LOG.info("Checking active users...");

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
                                    .anyMatch(date -> Duration.between(date, now).toMillis() < ONE_MONTH_MILLIS);

                            Member member = memberMap.get(Snowflake.of(user.getDiscordId()));

                            if (member != null) {
                                if (isActiveVoter) {
                                    user.setRole(User.Role.ACTIVE);
                                    getRoleByName(guildId, "Active")
                                            .flatMap(role -> addRoleToMember(member, role))
                                            .subscribe();
                                    getRoleByName(guildId, "Voter")
                                            .flatMap(role -> removeRoleFromMember(member, role))
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
                                            .flatMap(role -> removeRoleFromMember(member, role))
                                            .subscribe();
                                }
                                LOG.info("Checking user {} role: {}", user, user.getRole().toString());
                            }
                        }
                        return Mono.empty();
                    })
                    .subscribe();
        } else {
            LOG.info("No active users found.");
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
