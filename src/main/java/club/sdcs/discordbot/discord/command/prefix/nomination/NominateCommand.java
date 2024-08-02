package club.sdcs.discordbot.discord.command.prefix.nomination;

import club.sdcs.discordbot.discord.command.prefix.PrefixCommand;
import club.sdcs.discordbot.model.Nomination;
import club.sdcs.discordbot.service.NominationService;
import club.sdcs.discordbot.service.UserService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NominateCommand implements PrefixCommand {
    private final UserService userService;
    private final NominationService nominationService;
    private final int DELAY_MINUTES = 60;

    public NominateCommand(UserService userService, NominationService nominationService) {
        this.userService = userService;
        this.nominationService = nominationService;
    }

    @Override
    public String getName() {
        return "!nominate <";
    }

    @Override
    public String getDescription() {
        return "Nominate an active user (required second) for officer position `!nominate <@user> [role]`.";
    }

    @Override
    public Mono<Void> handle(Message message) {
        String content = message.getContent();

        // Handle the nomination list command separately
        if (content.startsWith("!nominate list")) {
            return Mono.empty();
        }

        String[] parts = content.split(" ");

        // Validate the command input
        if (parts.length < 3) {
            return sendMessage(message, "Usage: `!nominate <@user> [role]`");
        }

        Long nomineeId = extractNomineeId(parts[1]);
        if (nomineeId == null) {
            return sendMessage(message, "Invalid user mention.");
        }

        Long nominatorId = message.getAuthor().map(User::getId).map(Snowflake::asLong).orElse(null);
        String role = parts[2];

        // Prevent self-nomination
        if (nomineeId.equals(nominatorId)) {
            return sendImageResponse(message);
        }

        // Validate the role
        if (!isValidRole(role)) {
            return sendMessage(message, "Not a valid role. `!roles` to see available roles.");
        }

        // Check if both nominee and nominator are active members
        if (!isActiveMember(nomineeId, nominatorId)) {
            return sendMessage(message, "Nominator and/or nominee are not active or registered user.");
        }

        // Check if the nominee already has an existing nomination
        if (isExistingNomination(nomineeId)) {
            return sendMessage(message, "User already has nomination. `!nominate list` to see nominations. `!nominate drop` to drop nomination");
        }

        // Create the nomination message
        return createNominationMessage(message, nomineeId, nominatorId, role);
    }

    // Extracts the nominee's Discord ID from the mention
    private Long extractNomineeId(String mention) {
        Pattern pattern = Pattern.compile("<@(\\d+)>");
        Matcher matcher = pattern.matcher(mention);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }

    // Sends (funny) image response for self-nomination
    private Mono<Void> sendImageResponse(Message message) {
        String IMG_URL = "https://i.imgflip.com/1hhv9m.jpg?a478296";
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .image(IMG_URL)
                .build();
        MessageCreateSpec messageCreateSpec = MessageCreateSpec.builder()
                .addEmbed(embed)
                .build();
        return message.getChannel()
                .flatMap(channel -> channel.createMessage(messageCreateSpec))
                .then();
    }

    // Sends a text message response
    private Mono<Void> sendMessage(Message message, String text) {
        return message.getChannel()
                .flatMap(channel -> channel.createMessage(text))
                .then();
    }

    // Validates the user input role
    private boolean isValidRole(String role) {
        List<club.sdcs.discordbot.model.User.Role> officerRoles = Arrays.asList(
                club.sdcs.discordbot.model.User.Role.PRESIDENT,
                club.sdcs.discordbot.model.User.Role.VP_EXTERNAL,
                club.sdcs.discordbot.model.User.Role.VP_INTERNAL,
                club.sdcs.discordbot.model.User.Role.VP_OPERATIONS,
                club.sdcs.discordbot.model.User.Role.SECRETARY,
                club.sdcs.discordbot.model.User.Role.TREASURER,
                club.sdcs.discordbot.model.User.Role.MARKETING_OFFICER,
                club.sdcs.discordbot.model.User.Role.SOCIAL_MEDIA_OFFICER,
                club.sdcs.discordbot.model.User.Role.ASG_REPRESENTATIVE
        );

        try {
            club.sdcs.discordbot.model.User.Role userRole = club.sdcs.discordbot.model.User.Role.valueOf(role.toUpperCase());
            if (!officerRoles.contains(userRole)) {
                return false;
            }

            return userService.getOfficers().stream().noneMatch(user -> user.getRole() == userRole);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Checks if both nominee and nominator are active members
    private boolean isActiveMember(Long nomineeId, Long nominatorId) {
        club.sdcs.discordbot.model.User nominee = userService.getUserByDiscordId(nomineeId);
        club.sdcs.discordbot.model.User nominator = userService.getUserByDiscordId(nominatorId);

        return nominator != null && nominee != null
                && nominator.getRole() != club.sdcs.discordbot.model.User.Role.INACTIVE
                && nominee.getRole() != club.sdcs.discordbot.model.User.Role.INACTIVE;
    }

    // Checks if there is an existing nomination for the nominee
    private boolean isExistingNomination(Long nomineeId) {
        return nominationService.getAllNominations().stream()
                .anyMatch(nomination -> nomination.getNominee().getDiscordId() == nomineeId);
    }

    /** Creates the nomination embedded message, handles nomination entity creation.
     * (drops nomination from DB after DELAY_MINUTES)
     * @see club.sdcs.discordbot.discord.listener.button.SecondNominationButtonListener button listening/handling
    */
    private Mono<Void> createNominationMessage(Message message, Long nomineeId, Long nominatorId, String role) {
        Nomination nomination = new Nomination();
        nomination.setNominee(userService.getUserByDiscordId(nomineeId));
        nomination.setNominator(userService.getUserByDiscordId(nominatorId));
        nomination.setRole(club.sdcs.discordbot.model.User.Role.valueOf(role.toUpperCase()));
        Nomination savedNomination = nominationService.addNomination(nomination);

        String nomineeMention = "<@" + nomineeId + ">";
        String nominatorMention = "<@" + nominatorId + ">";

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Nomination for " + nomination.getRole().toString())
                .addField("", nomineeMention + " nominated by " + nominatorMention, false)
                .build();

        MessageCreateSpec messageCreateSpec = MessageCreateSpec.builder()
                .addEmbed(embed)
                .addComponent(ActionRow.of(Button.secondary("second-nominate-"
                        + savedNomination.getNominationId(), "Second")))
                .build();

        return message.getChannel()
                .flatMap(channel -> channel.createMessage(messageCreateSpec))
                .flatMap(createdMessage ->
                        Mono.delay(Duration.ofMinutes(DELAY_MINUTES))
                                .then(createdMessage.delete())
                                .then(Mono.fromRunnable(() -> nominationService.deleteNomination(savedNomination.getNominationId())))
                                .then(createdMessage.getChannel().flatMap(channel -> channel.createMessage("Nomination has expired.")))
                )
                .then();
    }
}