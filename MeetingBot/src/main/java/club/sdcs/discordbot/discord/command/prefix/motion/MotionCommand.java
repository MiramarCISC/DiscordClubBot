package club.sdcs.discordbot.discord.command.prefix.motion;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Motion;
import club.sdcs.discordbot.service.MotionService;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MotionCommand implements PrefixCommand {
    private final MotionService motionService;

    public MotionCommand(MotionService motionService) {
        this.motionService = motionService;
    }

    @Override
    public String getName() {
        return "!motion";
    }

    @Override
    public String getDescription() {
        return "Motions vote on meeting or minutes.";
    }

    // TODO: implement discord poll for vote
    // TODO: specify which motion (separate command classes)
    @Override
    public Mono<Void> handle(Message message) {
        Motion motion = new Motion();
        motionService.addMotion(motion);

        return message.getChannel()
                .flatMap(messageChannel -> messageChannel.createMessage("Starting motion or whatever...")
                        .then());
    }
}
