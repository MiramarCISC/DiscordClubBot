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

# Version control
- Use [gitflow](https://jeffkreeftmeijer.com/git-flow/)
- Git branches:
  - master - mainline stable branch, latest released production code
  - hotfixes/HOTFIX_NAME - branched from master, contains fix for production code
  - development - mainline stable branch, latest development code
  - features/FEATURE_NAME - branched from development, brand new feature
  - releases/VERSION - working branch to merge release code, typically development onto master
  - tags/VERSION - stable release candidate for production

# Adding a new feature
1. Clone this repo.
2. Fetch all remotes.
3. Update local development.
4. Checkout feature/branch from development.
5. Add new classes to model:
   1. nouns = classes
   2. attributes = member variables
   3. verbs = methods
6. Create JPA repositories for any new classes
7. Create Spring service that performs actions (create/read/update/delete objects of your classes)
8. Add message listener(s) that calls the Spring services
9. Commit 