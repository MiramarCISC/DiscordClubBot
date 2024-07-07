package club.sdcs.discordbot.config;

import club.sdcs.discordbot.discord.listener.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.gateway.GuildMemberAdd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class DiscordClient {
    Logger LOG = LoggerFactory.getLogger(DiscordClient.class);

    @Value("${spring.discord.token}")
    private String token;

    @Value("${spring.discord.server-id}")
    private String serverId;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        LOG.debug(token);
        LOG.debug(serverId);
        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        for(EventListener<T> listener : eventListeners) {
            LOG.debug(listener.getClass().getName());
            assert client != null;
            client.on(listener.getEventType())
                    .flatMap(listener::execute)
                    .onErrorResume(listener::handleError)
                    .subscribe();
        }

        //slash commands
        if (client != null) {

            Snowflake guildSnowflake = Snowflake.of(serverId);

            ApplicationCommandRequest membershipCommandRequest = ApplicationCommandRequest.builder()
                    .name("membership")
                    .description("Register for SDCS Club membership")
                    .build();

            client.getRestClient().getApplicationService()
                    .createGuildApplicationCommand(client.getSelfId().asLong(), guildSnowflake.asLong(), membershipCommandRequest)
                    .subscribe();
        }

        return client;
    }
}
