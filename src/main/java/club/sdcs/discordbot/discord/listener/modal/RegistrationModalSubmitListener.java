package club.sdcs.discordbot.discord.listener.modal;

import club.sdcs.discordbot.discord.commands.slash.MembershipManagement.EmbedUtils;
import club.sdcs.discordbot.discord.commands.slash.MembershipManagement.InformationProcessor;
import club.sdcs.discordbot.discord.listener.EventListener;
import club.sdcs.discordbot.discord.listener.button.RegistrationButtonListener;
import club.sdcs.discordbot.model.User;
import club.sdcs.discordbot.service.UserService;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationModalSubmitListener implements EventListener<ModalSubmitInteractionEvent> {

    private final UserService userService;
    private static final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public RegistrationModalSubmitListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Class<ModalSubmitInteractionEvent> getEventType() {
        return ModalSubmitInteractionEvent.class;
    } //end getEventType()

    @Override
    public Mono<Void> execute(ModalSubmitInteractionEvent event) {

        if (event.getInteraction().getUser().getId().asLong() != RegistrationButtonListener.userID) {
            return event.getInteraction().getChannel()
                    .flatMap(channel -> channel.createMessage("You are not the person who started the registration process." +
                            "\nIf you wish to register, please use the command `/membership` and start the process yourself."))
                    .then();
        }

        boolean validInformation = true;
        StringBuilder errorMessage = new StringBuilder("Error received:");

        String discordID = event.getInteraction().getId().asString();
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
                    .then(event.reply("Your details have been saved and confirmed."))
                    .then(EmbedUtils.createEmbedMessage(event.getMessage().get(),
                            "Registration Complete", "Welcome **" + currentUser.getFullName() + "** to the **Miramar SDCS Club**! We hope you enjoy your stay."));
        }
    } //end execute()

    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error("Error processing modal submission: {}", error.getMessage());
        return Mono.empty();
    } //end handleError()

    private Mono<Void> endRegistrationProcess(ModalSubmitInteractionEvent event, User user) {
        user.setDiscordId(event.getInteraction().getUser().getId().asLong());
        System.out.println(user.getDiscordId());

        user.setDiscordName(event.getInteraction().getUser().getUsername());
        user.setJoinDate(Timestamp.valueOf(LocalDateTime.now()));
        user.setStatus(User.Status.REGISTERED);

        userService.addUser(user);

        return event.getInteraction().getUser().getPrivateChannel()
                .flatMap(channel -> EmbedUtils.createEmbedMessage(channel, "User Information", "This is all the information you have saved" +
                        " to the SDCS Club.\n\nName: **" + user.getFullName() + "**\nEmail: **" + user.getEmail() + "**\nDistrict ID: **" +
                        user.getDistrictId() + "**\nPhone Number: **" + user.getMobileNumber() + "**\n\nIf at anytime you wish to **update** your details, please" +
                        "\nrefer to the following **command** and answer in this **DM**.\n\n**`!user edit [insert_field_name] [insert_information]`**\n\nExamples Provided Below:\n",
                        "https://i.imgur.com/4vmaCzq.png"));
    } //end endRegistrationProcess()
}
