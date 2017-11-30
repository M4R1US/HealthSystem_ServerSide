package Actions;


import Classes.CustomResourceBundle;
import Classes.GenericPair;
import SavedVariables.DynamicVariables;
import SavedVariables.FinalConstants;
import HelpClasses.ConsoleOutput;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.util.function.Consumer;

/**
 * <h2> Created by Marius Baltramaitis on 18-Dec-16. </h2>
 * <p> This class is responsible for launching different windows (Stages)</p>
 */
public final class WindowLauncher {


    /**
     * Launches window by opening MainController and initializing corresponding fxml path with stage object
     * @see Controllers.MainController
     */
    public static void launchMainWindow()
    {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        Stage stage = new Stage();
        Scene defaultScene;
        CustomResourceBundle resourceBundle = new CustomResourceBundle(new GenericPair(stage,"Stage"));
        try {
            root = fxmlLoader.load(WindowLauncher.class.getClassLoader().getResource("FXML/mainController.fxml"),resourceBundle);

        } catch (Exception e)
        {
            ConsoleOutput.print(WindowLauncher.class.toString(),e.getMessage());
        }
        defaultScene = new Scene(root, FinalConstants.MAIN_WINDOW_WIDTH,FinalConstants.MAIN_WINDOW_HEIGHT);
        String ip = DynamicVariables.ipv4Finder();

        if(ip == null)
            ip = "Undefined";

        stage.setTitle("Health System Main Server. MULTITASKING! [" + ip + "]");
        stage.setScene(defaultScene);
        stage.getIcons().add(new Image("Icons/x64/server.png"));
        stage.setResizable(false);
        stage.show();
        stage.alwaysOnTopProperty();


    }

    /**
     * Launches window by opening UserModification and initializing corresponding fxml path with stage object and update function
     * @see Controllers.UserModification
     * @param updateTerminalFunction Terminal update function
     */
    public static void launchActiveUsersWindow(Consumer<String> updateTerminalFunction)
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        BorderPane usersPane = null;
        Stage usersStage = new Stage();
        GenericPair<Consumer<String>,String> terminalReference = new GenericPair(updateTerminalFunction,"UpdateTerminalFunction");
        GenericPair<Consumer<Stage>,String> stageReference = new GenericPair(usersStage,"Stage");
        CustomResourceBundle customResourceBundle = new CustomResourceBundle(terminalReference,stageReference);
        try
        {
            usersPane = fxmlLoader.load(WindowLauncher.class.getClassLoader().getResource("FXML/userModification.fxml"),customResourceBundle);
        } catch (Exception e)
        {
            ConsoleOutput.print(WindowLauncher.class.getName(),e.getCause());
        }
        Scene usersScene = new Scene(usersPane,600,600);
        usersStage.setScene(usersScene);
        usersStage.setTitle("Active Users");
        usersStage.setResizable(false);

        usersStage.show();
    }

    /**
     * Launches window by opening InsertAdminController and initializing corresponding fxml path with stage object and update function
     * @see Controllers.InsertAdminController
     * @param updateTerminalFunction Terminal update function
     */
    public static void launchAddAdministratorWindow(Consumer<String> updateTerminalFunction)
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        BorderPane usersPane = null;
        Stage usersStage = new Stage();
        GenericPair<Consumer<String>,String> terminalReference = new GenericPair(updateTerminalFunction,"UpdateTerminalFunction");
        GenericPair<Consumer<Stage>,String> stageReference = new GenericPair(usersStage,"Stage");
        CustomResourceBundle customResourceBundle = new CustomResourceBundle(terminalReference,stageReference);
        try
        {
            usersPane = fxmlLoader.load(WindowLauncher.class.getClassLoader().getResource("FXML/insertAdmin.fxml"),customResourceBundle);
        } catch (Exception e)
        {
            ConsoleOutput.print(WindowLauncher.class.getName(),e.getCause());
        }
        Scene usersScene = new Scene(usersPane,600,600);
        usersStage.setScene(usersScene);
        usersStage.setTitle("Insert Admin window");
        usersStage.setResizable(false);

        usersStage.show();

    }



}
