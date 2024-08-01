package club.sdcs.discordbot.discord.listener.modal;

import club.sdcs.discordbot.discord.command.slash.EmbedUtils;
import club.sdcs.discordbot.discord.command.slash.membership.InformationProcessor;
import club.sdcs.discordbot.discord.listener.EventListener;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static club.sdcs.discordbot.discord.listener.button.RegistrationButtonListener.optInActive;
import static club.sdcs.discordbot.discord.listener.button.RegistrationButtonListener.optInVoter;

@Component
public class RegistrationModalSubmitListener implements EventListener<ModalSubmitInteractionEvent> {

    private final UserService userService;
    private static final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    @Value("${spring.discord.server-id}")
    private String guildId;

    public RegistrationModalSubmitListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Class<ModalSubmitInteractionEvent> getEventType() {
        return ModalSubmitInteractionEvent.class;
    } //end getEventType()

    @Override
    public Mono<Void> execute(ModalSubmitInteractionEvent event) {

        boolean validInformation = true;
        StringBuilder errorMessage = new StringBuilder("Error received:");

        String discordID = event.getInteraction().getUser().getId().asString();
        User currentUser = users.computeIfAbsent(discordID, id -> new User());

        InformationProcessor informationProcessor = new InformationProcessor(currentUser, errorMessage);

        String customId = event.getCustomId();

        currentUser.setSubscribedToEmails(false);
        currentUser.setSubscribedToSMS(false);

        for (TextInput component: event.getComponents(TextInput.class)) {
            String value = component.getValue().orElse(null);

            if ((customId + "name").equals(component.getCustomId())) {
                validInformation &= informationProcessor.setUserName(value);
            }

            else if ((customId + "districtID").equals(component.getCustomId()) && value != null) {
                validInformation &= informationProcessor.setUserDistrictID(value);
            }

            else if ((customId + "email").equals(component.getCustomId()) && value != null && !value.isEmpty()) {
                validInformation &= informationProcessor.setUserEmail(value);
                currentUser.setSubscribedToEmails(true);
            }

            else if ((customId + "phonenumber").equals(component.getCustomId()) && value != null && !value.isEmpty()) {
                validInformation &= informationProcessor.setUserPhoneNumber(value);
                currentUser.setSubscribedToSMS(true);
            }
        }

        if (!validInformation) {
            errorMessage.append(" Try again.");
            return event.reply(String.valueOf(errorMessage));
        } else {
            return endRegistrationProcess(event, currentUser)
                    .then(event.edit().withEmbeds(EmbedCreateSpec.builder()
                                    .title("User Information Completed")
                                    .color(Color.SUMMER_SKY)
                                    .description("Your details have been confirmed and saved!")
                                    .build())
                            .withComponents())
                    .then(EmbedUtils.createEmbedMessage(event.getMessage().get(),
                            "Registration Completed", "Welcome **" + currentUser.getFullName() + "** to the **Miramar SDCS Club**! We hope you enjoy your stay."));
        }
    } //end execute()

    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error("Error processing modal submission: {}", error.getMessage());
        return Mono.empty();
    } //end handleError()

    /**
     * update user role
     * @param event takes in modal submit interaction event
     * @param discordID takes in user discord ID
     * @return updated role
     */
    private Mono<Void> updateUserRole(ModalSubmitInteractionEvent event, User user, String discordID) {

        return getRoleByName(event, optInActive ? "Active" : "Inactive")
                .flatMap(role -> event.getClient().getGuildById(Snowflake.of(guildId))
                        .flatMap(guild -> guild.getMemberById(Snowflake.of(discordID)))
                        .flatMap(member -> {
                            Mono<Void> addRole = member.addRole(role.getId());
                            if (optInActive) {
                                user.setRole(User.Role.ACTIVE);
                                userService.addUser(user);
                            }

                            if (optInVoter) {
                                return getRoleByName(event, "Voter")
                                        .flatMap(voterRole -> addRole.then(member.addRole(voterRole.getId())));

                            } else {
                                user.setRole(User.Role.INACTIVE);
                                userService.addUser(user);
                                return addRole;
                            }
                        }));
    } //end updateUserRole()

    /**
     * ends the registration process
     * @param event takes in modal submit interaction event
     * @param user takes in user who submitted
     * @return ended interaction
     */
    private Mono<Void> endRegistrationProcess(ModalSubmitInteractionEvent event, User user) {
        user.setDiscordId(event.getInteraction().getUser().getId().asLong());
        user.setDiscordName(event.getInteraction().getUser().getUsername());
        user.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        user.setStatus(User.Status.REGISTERED);

        return updateUserRole(event, user, event.getInteraction().getUser().getId().asString())
                .then(event.getInteraction().getUser().getPrivateChannel()
                .flatMap(channel -> EmbedUtils.createEmbedMessage(channel, "User Information", "This is all the information you have saved" +
                        " to the SDCS Club.\n\nName: **" + user.getFullName() + "**\nEmail: **" + user.getEmail() + "**\nDistrict ID: **" +
                        user.getDistrictId() + "**\nPhone Number: **" + user.getMobileNumber() + "**\nRole: **" + user.getRole() + "**\n\nIf at anytime you wish to **update** your details, please" +
                        "\nrefer to the following **command** and answer in this **DM**. You do not have the ability to update your role. That is entirely dependent\non your participation in the club and is subject to change.\n\n**`!user edit [insert_field_name] [insert_information]`**\n\nExamples Provided Below:\n",
                        "https://i.imgur.com/mOB0kaf.png"))
                        .then());
    } //end endRegistrationProcess()

    /**
     * retrieves a role by name
     * @param event ModalSubmitInteraction event
     * @param roleName name of the role
     * @return the role
     */
    private Mono<Role> getRoleByName(ModalSubmitInteractionEvent event, String roleName) {
        return event.getClient().getGuildById(Snowflake.of(guildId))
                .flatMap(guild -> guild.getRoles()
                        .filter(role -> role.getName().equalsIgnoreCase(roleName))
                        .next());
    } //end getRoleByName()

}
