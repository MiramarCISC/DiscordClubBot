package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Nomination;
import club.sdcs.discordbot.repository.NominationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NominationService {
    private final NominationRepository nominationRepository;

    public NominationService(NominationRepository nominationRepository) {
        this.nominationRepository = nominationRepository;
    }

    public Nomination addNomination(Nomination nomination) {
        return nominationRepository.save(nomination);
    }
    public Nomination getNominationById(long id){
        return nominationRepository.findByNominationId(id);
    }
    public Nomination getNominationByMessageId(long messageId){
        return nominationRepository.findByMessageId(messageId);
    }
    public List<Nomination> getAllNominations() {
        return nominationRepository.findAll();
    }
    public void deleteNomination(long id) {
        nominationRepository.deleteById(id);
    }
    public void updateNomination(Nomination nomination) {
        if (nominationRepository.existsById(nomination.getNominationId())) {
            nominationRepository.save(nomination);
        } else {
            throw new IllegalArgumentException("Nomination with ID " + nomination.getNominationId() + " does not exist.");
        }
    }
}
