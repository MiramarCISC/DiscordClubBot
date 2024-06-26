package club.sdcs.discordbot.discord.commands.prefix.UserRegistration;

import java.util.regex.Pattern;

public class ValidityChecker {

    /**
     * checks if name during registration process is valid
     * @param content takes in message sent by user
     * @return the validity of the name sent
     */
    public boolean checkNameValidity(String[] content) {
        // valid name if the message split up has a length of 4
        return content.length == 4;
    } //end checkName()

    /**
     * checks if email during registration process is formatted correctly
     * @param content takes in message sent by user
     * @return the validity of the email sent
     */
    public boolean checkEmailValidity(String[] content) {
        if (content.length != 3) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                            "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);

        // valid email if the email is in proper format
        String email = content[2];
        return pattern.matcher(email).matches();
    } //end checkEmailValidity()

    /**
     * checks if district ID during registration process is formatted correctly
     * @param content takes in message sent by user
     * @return the validity of the district ID sent
     */
    public boolean checkDistrictIDValidity(String[] content) {
        if (content.length != 3) {
            return false;
        }

        String districtID = content[2];
        // valid district ID if is all numeric and is 10 digits long
        return districtID.matches("\\d{10}");
    } //end checkDistrictIDValidity()

    /**
     * checks if phone number during registration process is formatted correctly
     * @param content takes in message sent by user
     * @return the validity of the phone number sent
     */
    public boolean checkPhoneNumberValidity(String[] content) {
        if (content.length != 3) {
            return false;
        }

        String phoneNumber = content[2];
        // valid phone number if is all numeric and is 10 digits long
        return phoneNumber.matches("\\d{10}");
    } //end checkPhoneNumberValidity()
}
