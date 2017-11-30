package main;

import Actions.WindowLauncher;
import HelpClasses.ConsoleOutput;
import Registers.SmsFunctionality;
import SavedVariables.DynamicVariables;
import SavedVariables.FinalConstants;
import Security.Cryptography;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * <h2>Created by Marius Baltramaitis</h2>
 *
 * <p>Main class</p>
 */

public class Main extends Application {

    private static int delay = 0;


    @Override
    public void start(Stage primaryStage) throws Exception{
        readDataFromFiles();
        WindowLauncher.launchMainWindow();
    }


    /**
     * Main method, args [0] = delay in milliseconds
     * @param args args
     */
    public static void main(String[] args) {
        if(args != null)
        {
            try{
                 delay = Integer.parseInt(args[0]);
                ConsoleOutput.print(Main.class.getName(),"Launching application in " + args[0] + " milliseconds");
                //t.run();

                do
                {
                    Thread t = new Thread();
                    t.run();
                    delay -=100;
                    ConsoleOutput.print(Main.class.getName(),"Launching application in " + delay + " milliseconds");
                    t.sleep(100);

                } while (delay > 0);

            }
            catch (NumberFormatException e)
            {
                ConsoleOutput.print(Main.class.getName(),"Couldn't convert args to integer");
            }
            catch (InterruptedException e)
            {
                ConsoleOutput.print("OS interrupted delay");
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                ConsoleOutput.print("No arguments for delay");
            }
        }
        launch(args);
    }

    /**
     * Scanning data from files and initializing runtime variables
     * @see DynamicVariables
     */
    private static void readDataFromFiles()
    {
        try  {

            SmsFunctionality.scanInput();
            DynamicVariables.AES_KEY=Files.readAllLines(Paths.get(FinalConstants.CRYPTOGRAPHY_KEY_PATH)).get(0);

            List<String> credentials =  Files.readAllLines(Paths.get(FinalConstants.CREDENTIALS_FILE_PATH));

            DynamicVariables.DB_LOGIN = credentials.get(1);
            DynamicVariables.DB_PASSWORD = credentials.get(2);
            DynamicVariables.LITHUANIAN_NUMBER = credentials.get(3);
            DynamicVariables.NORWEGIAN_NUMBER = credentials.get(4);
            DynamicVariables.ACCOUNT_SID = credentials.get(5);
            DynamicVariables.AUTH_TOKEN = credentials.get(6);
            Cryptography cryptography = new Cryptography();


            try {
                DynamicVariables.ENCRYPTED_DB_LOGIN = cryptography.encrypt(DynamicVariables.DB_LOGIN,DynamicVariables.AES_KEY);
                DynamicVariables.ENCRYPTED_DB_PASSWORD = cryptography.encrypt(DynamicVariables.DB_PASSWORD,DynamicVariables.AES_KEY);

            } catch (Exception e)
            {
                ConsoleOutput.print(Main.class.getName(),e);
                Platform.exit();
            }

            ConsoleOutput.print("Database password is " + DynamicVariables.DB_PASSWORD + " And encrypted is " + DynamicVariables.ENCRYPTED_DB_PASSWORD);



        } catch (IOException e)
        {
            ConsoleOutput.print(Main.class.getName(),e);
            Platform.exit();
        }
    }

}
