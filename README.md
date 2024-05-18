# Prerequisites
1. JDK 21
2. Maven 3

# Dependencies
1. Spring Boot 3
2. Spring JPA
3. H2 SQL Database
4. discord4j
5. flyway db version management

# Folder Structure
- /config spring bean configurations
- /discord the bot's listeners
- /model classes persisted to DB
- /repository JPA database interfaces
- /security access control by discordId and services
- /service wrapper around databases with access control and input/output validation
- SdcsDiscordBotApplication - the main class

# Required environment variables to run and test
DISCORD_TOKEN - get from https://discord.com/developers/applications
DISCORD_SERVER_ID - get from Discord client

# How to compile and build executable jar
mvn clean install spring-boot:repackage

# How to run
java -jar target/DiscordClubBot-0.0.1-SNAPSHOT.jar

Or using IntelliJ, just press the green Play button next to DiscordClubBotApplication.main()