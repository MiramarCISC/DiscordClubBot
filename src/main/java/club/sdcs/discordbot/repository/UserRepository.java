package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByDiscordId(long id);
    List<User> findByRoleIn(List<User.Role> roles);
}
