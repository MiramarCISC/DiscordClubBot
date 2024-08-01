package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        // TODO: validate user and throw exception if invalid
        userRepository.save(user);
    }

    public User getUserByDiscordId(long id) {
        System.out.println("User ID: " + id);
        return userRepository.findByDiscordId(id);
    }

    public List<User> getOfficers() {
        List<User.Role> officerRoles = Arrays.asList(
                User.Role.PRESIDENT,
                User.Role.VP_EXTERNAL,
                User.Role.VP_INTERNAL,
                User.Role.VP_OPERATIONS,
                User.Role.SECRETARY,
                User.Role.TREASURER,
                User.Role.MARKETING_OFFICER,
                User.Role.SOCIAL_MEDIA_OFFICER,
                User.Role.ASG_REPRESENTATIVE
        );
        return userRepository.findByRoleIn(officerRoles);
    }

    public List<User> getAllActiveUsers() {
        List<User.Role> activeRoles = List.of(User.Role.ACTIVE);
        return userRepository.findByRoleIn(activeRoles);

    }
}
