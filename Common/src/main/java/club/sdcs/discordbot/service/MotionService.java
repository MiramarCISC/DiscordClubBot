package club.sdcs.discordbot.service;

import club.sdcs.discordbot.model.Motion;
import club.sdcs.discordbot.repository.MotionRepository;
import org.springframework.stereotype.Service;

@Service
public class MotionService {
    private final MotionRepository motionRepository;

    public MotionService(MotionRepository motionRepository) {
        this.motionRepository = motionRepository;
    }

    public Motion addMotion(Motion motion) {
        return motionRepository.save(motion);
    }
}
