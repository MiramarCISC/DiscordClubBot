package club.sdcs.discordbot.discord.listener;

import club.sdcs.discordbot.discord.command.slash.EmbedUtils;
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
                .flatMap(guild -> guild.getChannelById(Snowflake.of(1239977129869967494L)))
                .cast(MessageChannel.class)
                .flatMap(channel -> EmbedUtils.createEmbedMessage(channel, "Welcome " + event.getMember().getUsername().toUpperCase(),
                        "Welcome to the **Miramar SDCS Club** " + event.getMember().getMention() + "! To begin your **registration** as a member of the club, " +
                        "type the command **`/membership`** in any channel. We hope you enjoy your stay!"))
                .then();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return EventListener.super.handleError(error);
    }
}
