package club.sdcs.discordbot.discord.command.slash.membership;

import club.sdcs.discordbot.model.User;

public class InformationProcessor {

    private final ValidityChecker validityChecker = new ValidityChecker();
    private final User user;
    private final StringBuilder errorMessage;

    public InformationProcessor(User user, StringBuilder errorMessage) {
        this.user = user;
        this.errorMessage = errorMessage;
    } //end constructor

    public boolean setUserName(String name) {
        user.setFullName(name);
        return true;
    } //end setUserName()

    public boolean setUserEmail(String email) {
        if (validityChecker.checkEmailValidity(email)) {
            user.setEmail(email);
            return true;
        } else {
            errorMessage.append(" Invalid email.");
            return false;
        }
    } //end setUserEmail()

    public boolean setUserDistrictID(String districtID) {
        if (validityChecker.checkDistrictIDValidity(districtID)) {
            user.setDistrictId(Long.parseLong(districtID));
            return true;
        } else {
            errorMessage.append(" Invalid district ID.");
            return false;
        }
    } //end setUserDistrictID()

    public boolean setUserPhoneNumber(String phoneNumber) {
        if (validityChecker.checkPhoneNumberValidity(phoneNumber)) {
            user.setMobileNumber(Long.parseLong(phoneNumber));
            return true;
        } else {
            errorMessage.append(" Invalid phone number.");
            return false;
        }
    } //end setUserPhoneNumber()

} //end class
