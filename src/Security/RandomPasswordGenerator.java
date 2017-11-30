package Security;

import java.util.Random;

/**
 * <h2>Created by Marius B on 09-Feb-17.</h2>
 * <p>Class to generate random password</p>
 */
public final class RandomPasswordGenerator {

    private static String letters = "qwertyuiopasdfghjklzxcvbnm";
    private static String integers = "1234567890";
    private static final String passwordChars = letters + letters.toUpperCase() + integers;
    private static Random random = new Random();

    /**
     * Method to generate random password
     * @return generated random password
     */
    public static String generatePassword()
    {
        String password = "";
        for(int i = 0; i < 8; i++)
            password+=passwordChars.charAt(random.nextInt(passwordChars.length()));

        return password;
    }

}
