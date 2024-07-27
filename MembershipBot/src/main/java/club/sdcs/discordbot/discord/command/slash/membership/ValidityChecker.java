package club.sdcs.discordbot.discord.command.slash.membership;

import java.util.regex.Pattern;

public class ValidityChecker {

    /**
     * checks if email during registration process is formatted correctly
     * @param email takes in message sent by user
     * @return the validity of the email sent
     */
    public boolean checkEmailValidity(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                            "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);

        // valid email if the email is in proper format
        return pattern.matcher(email).matches();
    } //end checkEmailValidity()

    /**
     * checks if district ID during registration process is formatted correctly
     * @param districtID takes in message sent by user
     * @return the validity of the district ID sent
     */
    public boolean checkDistrictIDValidity(String districtID) {
        // valid district ID if is all numeric and is 10 digits long
        return districtID.matches("\\d{10}");
    } //end checkDistrictIDValidity()

    /**
     * checks if phone number during registration process is formatted correctly
     * @param phoneNumber takes in message sent by user
     * @return the validity of the phone number sent
     */
    public boolean checkPhoneNumberValidity(String phoneNumber) {
        // valid phone number if is all numeric and is 10 digits long
        return phoneNumber.matches("\\d{10}");
    } //end checkPhoneNumberValidity()
}
