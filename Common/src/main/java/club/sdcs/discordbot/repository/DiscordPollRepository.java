package club.sdcs.discordbot.repository;

import club.sdcs.discordbot.model.DiscordPoll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscordPollRepository extends JpaRepository<DiscordPoll, Long> {
}
