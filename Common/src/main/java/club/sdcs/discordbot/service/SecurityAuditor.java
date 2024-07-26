package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditor implements AuditorAware<User> {
    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return Optional.of((User) auth.getPrincipal());
    }
}
