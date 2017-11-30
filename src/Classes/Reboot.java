package Classes;

import HelpClasses.ConsoleOutput;
import javafx.application.Platform;

import java.io.IOException;

/**
 * <h2>Created by Marius Baltramaitis on 24/04/2017.</h2>
 *
 * <p>This class is used to reboot application</p>
 */
public final class Reboot {

    /**
     * Reboot method for windows OS
     */
    public static void windowsReboot()
    {
        String filePath = System.getProperty("user.dir") +"\\" + "HealthSystem_server_connections.jar 5000";
        try{

            ConsoleOutput.print("Reboot command: " + filePath);
            Process reboot = Runtime.getRuntime().exec("cmd.exe /c start java -jar "+filePath);
            Platform.exit();
        } catch (IOException e)
        {
            ConsoleOutput.print("Couldn't reboot");
            ConsoleOutput.print(Reboot.class.toString(),e);
        }
    }

    /**
     * Reboot method for unix (linux) OS for xfce4 terminal only!
     */
    public static void unixReboot()
    {
        try {

            String command = "xfce4-terminal -x sh reboot.sh";
            Process reboot = Runtime.getRuntime().exec(command);
            Platform.exit();

        } catch (IOException e) {

            ConsoleOutput.print("Couldn't reboot");
            ConsoleOutput.print(Reboot.class.toString(),e);
        }
    }
}
