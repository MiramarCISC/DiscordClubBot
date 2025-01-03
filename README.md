# Prerequisites
1. JDK 21
2. Maven 3

# Dependencies
1. Spring Boot 3
2. Spring JPA
3. MariaDB 10
4. discord4j (version: 3.2.7-SNAPSHOT commit b476687 required for Polls, released version does not include)
   1. Clone and checkout commit id b476687 from the 3.2.x branch: https://github.com/Discord4J/Discord4J
   2. ./gradew publishToMavenLocal
   3. Update this project's pom.xml Discord4j with the SNAPSHOT version published
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
DISCORD_TOKEN - get from https://discord.com/developers/applications (Each bot needs to be a separate application with different token)
DISCORD_SERVER_ID - get from Discord client
DISCORD_CHANNEL_ID - the #officers text channel id

# Bot user server permissions required 
1. Administrator (TODO: tighten security)

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
9. Commit your changes.
10. Push your changes.
11. Create a pull request onto development.

# How to create a release
1. Make sure JAVA_HOME environment variable matches the jdk you are using to build and run your project.
2. Merge development branch with master
3. Checkout development branch
4. Note that latest jgitflow plugin is compiled with old ssl libs so it cannot communicate with github over ssh, so you must use https with your github username and github personal access token
5. mvn jgitflow:release-start -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT -Dusername=$GITUSER -Dpassword$GITTOKEN
6. mvn jgitflow:release-finish  -Dusername=$GITUSER -Dpassword$GITTOKEN

# Code syntax standards
1. camelCase classes and variables
2. Capitalize class names
3. lowercase first letter of object names
4. Singular class names
5. Plural database table names

# Supported commands
## MembershipBot
### Prefixes
| Command | Description | Example |
| --- | --- | --- |
| !user list | Lists all users | |
| !user info | Gets user information | |
| !user edit | Edit user information | |

### Slash
| Command     | Description | Example |
|-------------| --- | --- |
| /membership | Start registration | |

## MeetingBot
### Prefix
| Command      | Description                                                                   | Example                |
|--------------|-------------------------------------------------------------------------------|------------------------|
| !meeting list | Lists detail of all active and scheduled meetings.                            |                        |
| !meeting log | Gets log of all members who attended a specific meeting.                      | !meeting log [meeting_id] |
| !meeting show | Shows details of a specific meeting.                                          | !meeting show [meeting_id] |
| !meeting remind | Manually sends meeting reminders.                                             ||
| !meeting id | Lists IDs of meetings completed, scheduled, or active.                        ||
| !meeting link | Lists all active and scheduled meetings with their agenda and minutes links.  ||
| !motion | Motions vote on meeting or minutes. ||
| !nominate | Nominate an active user (required second) for officer position. | !nominate <@user> [role] |
| !nominate list | Lists the list of nominated users & respected roles ||
| !nominate drop | Drops nomination of user. Can only be done by nominee themselves (& select roles) ||

### Slash
| Command      | Description                                                                   | Example                |
|--------------|-------------------------------------------------------------------------------|------------------------|
|/rollcall | Starts a roll call poll for the current active meeting ||


