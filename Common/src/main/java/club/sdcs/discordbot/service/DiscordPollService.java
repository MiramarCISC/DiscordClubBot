package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.DiscordPoll;
import club.sdcs.discordbot.repository.DiscordPollRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscordPollService {
    private final DiscordPollRepository repository;

    public DiscordPollService(DiscordPollRepository repository) {
        this.repository = repository;
    }

    public DiscordPoll addDiscordPoll(DiscordPoll poll) {
        return repository.save(poll);
    }
    public DiscordPoll findDiscordPollById(long id) {
        return repository.findById(id).orElse(null);
    }
    public DiscordPoll updateDiscordPoll(DiscordPoll poll) {
        return repository.save(poll);
    }
}
