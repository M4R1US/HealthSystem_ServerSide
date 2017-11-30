package ApplicationUsers;

/**
 * <h2>Created by Marius Baltramaitis on 07/04/2017.</h2>
 *
 * Administrator class
 */
public class Administrator {

    private String login,firstName,lastName,sex,additionalInfo,salt,password,encryptedPassword,lastUsedDevice;

    /**
     * Default constructor
     */
    public Administrator() {}

    /**
     *
     * @param firstName First name
     * @param lastName Last Name
     * @param sex sex (gender)
     * @param additionalInfo additional information
     * @param salt salt using to encrypt password
     * @param encryptedPassword encrypted password
     */
    public Administrator(String firstName, String lastName, String sex, String additionalInfo, String salt, String encryptedPassword)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.additionalInfo = additionalInfo;
        this.salt = salt;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * Getter for login
     * @return login name
     */
    public String getLogin() {
        return login;
    }

    /**
     * Setter for login
     * @param login login name
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Getter for first name
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for first name
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for last name
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter for last name
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for sex (gender)
     * @return sex (gender) Male of Female
     */
    public String getSex() {
        return sex;
    }

    /**
     * setter for sex (gender)
     * @param sex sex (gender) male of female
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Getter for additional information of administrator
     * @return additional information of administrator
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Setter for additional information of administrator
     * @param additionalInfo additional information of administrator
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * Getter for salt
     * @return salt used to encrypt password
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Setter for salt
     * @param salt salt used to encrypt password
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     *  Getter for not encrypted password
     * @return not encrypted password (plain text)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for password
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for encrypted password
     * @return encurypted password
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * Setter for encrypted password
     * @param encryptedPassword encrypted password
     */
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * Getter for last used device of this used
     * @return last used device of this user
     */
    public String getLastUsedDevice() {
        return lastUsedDevice;
    }

    /**
     * Setter for last used device of this used
     * @param lastUsedDevice last used device of this user
     */
    public void setLastUsedDevice(String lastUsedDevice) {
        this.lastUsedDevice = lastUsedDevice;
    }
}
