package club.sdcs.discordbot.discord.listener;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WelcomeMessageListener implements EventListener<MemberJoinEvent> {

    @Override
    public Class<MemberJoinEvent> getEventType() {
        return MemberJoinEvent.class;
    }

    @Override
    public Mono<Void> execute(MemberJoinEvent event) {
        return event.getGuild()
                .flatMap(guild -> guild.getChannelById(Snowflake.of(1258908355058204732L)))
                .cast(MessageChannel.class)
                .flatMap(channel -> channel.createMessage("Welcome to the server!"))
                .then();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
