package club.sdcs.discordbot.config;

import club.sdcs.discordbot.discord.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

        //Register all slash commands created
        if (client != null) {

            //change to needed discord server ID
            long guildId = 1252368620047044648L;
            Snowflake guildSnowflake = Snowflake.of(guildId);

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
