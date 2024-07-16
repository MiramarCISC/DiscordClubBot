package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Nomination;
import club.sdcs.discordbot.repository.NominationRepository;
import org.springframework.stereotype.Service;

@Service
public class NominationService {
    private final NominationRepository nominationRepository;

    public NominationService(NominationRepository nominationRepository) {
        this.nominationRepository = nominationRepository;
    }

    public Nomination save(Nomination nomination) {
        return nominationRepository.save(nomination);
    }
}
