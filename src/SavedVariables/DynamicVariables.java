package SavedVariables;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * <h2>Created by Marius Baltramaitis on 07/10/2017.</h2>
 *
 * <p>This class is holder for static dynamic variables that can be changed in runtime</p>
 */
public final class DynamicVariables {

    public static String AES_KEY;
    public static String DB_LOGIN;
    public static String DB_PASSWORD;
    public static String ENCRYPTED_DB_LOGIN;
    public static String ENCRYPTED_DB_PASSWORD;
    public static String LITHUANIAN_NUMBER;
    public static String NORWEGIAN_NUMBER;
    public static String ACCOUNT_SID;
    public static String AUTH_TOKEN;

    /**
     * Method to find ipv4
     * @return ip address of version 4
     */
    public static String ipv4Finder()
    {
        try
        {
            URL url = new URL("http://checkip.amazonaws.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            return br.readLine();
        } catch (Exception e)
        {
            return null;
        }
    }
}
