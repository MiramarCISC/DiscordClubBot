package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User addUser(User user) {
        // TODO: validate user and throw exception if invalid
        return userRepository.save(user);
    }

    public User getUserByDiscordId(long id){
        return userRepository.findByDiscordId(id);
    }
}
