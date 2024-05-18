package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SecurityService implements UserDetailsService {
    private final UserService userService;
    public SecurityService(UserService userService) {
        this.userService = userService;
    }
    @Override
    public UserDetails loadUserByUsername(String discordId) throws UsernameNotFoundException {
        User user = userService.getUserByDiscordId(Long.parseLong(discordId));
        if(user == null) throw new UsernameNotFoundException("Invalid discord id");
        return new org.springframework.security.core.userdetails.User(user.getDiscordName(), Long.toString(user.getDiscordId()), Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())));
    }
}
