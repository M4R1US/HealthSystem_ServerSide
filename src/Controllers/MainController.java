package Controllers;

import Actions.WindowLauncher;
import SavedVariables.DynamicVariables;
import SavedVariables.FinalConstants;
import HelpClasses.ConsoleOutput;
import NetworkTcpProtocols.*;
import NetworkThreads.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 *  <h2>Created by Marius Baltramaitis</h2>
 *  <p>Controller for main window</p>
 */
public class MainController implements Initializable {


    private ExecutorService imageSendingService,imageReceiveService,userDeviceReferenceService,smsInboundService,firewallListenerThreadPool;
    @FXML
    public Pane MainPane;
    public TextFlow TerminalTextFlow;
    public Label ClearLabel,AddAdminLabel, RebootLabel, ActiveUsersLabel,SaveLogLabel;
    public ScrollPane ScrollPane;
    private Stage runningStage;
    private ParallelTcpListener imageSender,imageReceiver,userDeviceReference,smsService,firewallService;


    /**
     * Initializes local variables from fxml file, also attaches events for corresponding nodes
     * <p>Also launches threads</p>
     * @param location fxml path
     * @param resources resources from windowLauncher class
     * @see Actions.WindowLauncher
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {


        launchThreads();


        ScrollPane.vvalueProperty().bind(MainPane.heightProperty());
        runningStage = (Stage)resources.getObject("Stage");
        runningStage.setOnCloseRequest(event -> terminateThreads());

        TerminalTextFlow.heightProperty().addListener((observable,oldValue,newValue) -> {
            if(oldValue.doubleValue() == 0 || newValue.doubleValue() == 0)
                ScrollPane.vvalueProperty().unbind();
            else
            ScrollPane.setVvalue(newValue.doubleValue());
        });

        TerminalTextFlow.getChildren().addListener((ListChangeListener<Node>) children -> {
            if(children != null && children.getList().size() > FinalConstants.MAX_TERMINAL_LINES)
            {
                Node last = children.getList().get(children.getList().size()-1);
                children.getList().removeAll(children.getList());
                TerminalTextFlow.getChildren().add(last);
            }

        });

        SaveLogLabel.setOnMouseClicked(event -> {
            try { saveLogFile(); } catch (Exception e) {ConsoleOutput.print(getClass().getName(),e);}
        });
        ActiveUsersLabel.setOnMouseClicked(event -> WindowLauncher.launchActiveUsersWindow(updateTerminalFunction()));

        RebootLabel.setOnMouseClicked(event -> FinalConstants.REBOOT_FUNCTION.run());
        ClearLabel.setOnMouseClicked(event ->TerminalTextFlow.getChildren().clear());
        AddAdminLabel.setOnMouseClicked(event -> WindowLauncher.launchAddAdministratorWindow(updateTerminalFunction()));

    }

    /**
     * Function to update terminal
     * @return Consumer with string arguments where string itself is the text to be printed inside terminal window
     */
    public Consumer<String> updateTerminalFunction()
    {
        return  (String) -> {
            Text text = new Text(String);
            text.setFill(Paint.valueOf("#ffffff"));
            text.setFont(Font.font(20));
            text.setText(String+System.lineSeparator());
            TerminalTextFlow.getChildren().add(text);
        };
    }


    /**
     * Terminates all threads that are initialized in this class
     * @see MainController#launchThreads()
     */
    public void terminateThreads()
    {
        if(imageSender != null)
        {
            try {
                imageSender.getServerSocket().close();
                imageSendingService.shutdown();
                imageReceiver.getServerSocket().close();
                imageReceiveService.shutdown();
                userDeviceReference.getServerSocket().close();
                userDeviceReferenceService.shutdown();
                smsService.getServerSocket().close();
                smsInboundService.shutdown();
                firewallService.getServerSocket().close();
                firewallListenerThreadPool.shutdown();
                Platform.exit();
            }catch (IOException e)
            {
                ConsoleOutput.print("IOException on terminating threads!");
                Platform.exit();
            }
            imageSendingService.shutdown();
            Platform.exit();
        }
    }

    /**
     * Method to add text from this class to terminal
     * @param information text to be added inside terminal
     */
    private void addText(String information)
    {
        Text text = new Text("From MainController: " + information+"\n");
        text.setFont(FinalConstants.TERMINAL_FONT);
        text.setFill(Paint.valueOf("#FFD700"));
        TerminalTextFlow.getChildren().add(text);

    }

    /**
     * Launches imageSender service pool and applies targeted network thread to run asynchronously in order to handle multiple tcp requests from clients
     * @throws Exception if service can not be launched (mostly because of taken port)
     */
    private void launchImageSender() throws Exception
    {
        imageSender = new ParallelTcpListener(TerminalTextFlow,"Image sender thread running. Listening port : " + FinalConstants.IMAGE_SENDER_PORT,new ImageSender(),FinalConstants.IMAGE_SENDER_PORT);
        imageSendingService = Executors.newWorkStealingPool();
        imageSendingService.execute(imageSender);
    }

    /**
     * Launches imageReceiver service pool and applies targeted network thread to run asynchronously in order to handle multiple tcp requests from clients
     * @throws Exception if service can not be launched (mostly because of taken port)
     */
    private void launchImageReceiver() throws Exception
    {
        imageReceiver = new ParallelTcpListener(TerminalTextFlow,"Image receiver thread running. Listening port : " + FinalConstants.IMAGE_RECEIVE_PORT,new ImageReceiver(),FinalConstants.IMAGE_RECEIVE_PORT);
        imageReceiveService = Executors.newWorkStealingPool();
        imageReceiveService.execute(imageReceiver);
    }

    /**
     * Launches user reference register service pool and applies targeted network thread to run asynchronously in order to handle multiple tcp requests from clients
     * @throws Exception if service can not be launched (mostly because of taken port)
     */
    private void launchUserDeviceReferenceRegister() throws Exception
    {
        userDeviceReference = new ParallelTcpListener(TerminalTextFlow,"User device receive thread running. Listening port : " + FinalConstants.TCP_DEVICE_CONNECTION_PORT,new DeviceRegister(),FinalConstants.TCP_DEVICE_CONNECTION_PORT);
        userDeviceReferenceService = Executors.newWorkStealingPool();
        userDeviceReferenceService.execute(userDeviceReference);

    }

    /**
     * Launches sms input service pool and applies targeted network thread to run asynchronously in order to handle multiple tcp requests from clients
     * @throws Exception if service can not be launched (mostly because of taken port)
     */
    private void launchSmsSensor() throws Exception
    {
        smsService = new ParallelTcpListener(TerminalTextFlow,"Sms input thread running. Listening port : " + FinalConstants.SMS_INBOUND_RECEIVE_PORT,new SmsInput(),FinalConstants.SMS_INBOUND_RECEIVE_PORT);
        smsInboundService = Executors.newWorkStealingPool();
        smsInboundService.execute(smsService);
    }

    /**
     * Launches firewall service pool and applies targeted network thread to run asynchronously in order to handle multiple tcp requests from clients
     * @throws Exception if service can not be launched (mostly because of taken port)
     */
    private void launchFirewall() throws Exception
    {
        firewallService = new ParallelTcpListener(TerminalTextFlow,"Firewall is running. Listening port : " + FinalConstants.FIREWALL_PORT,new FirewallAction(),FinalConstants.FIREWALL_PORT);
        firewallListenerThreadPool = Executors.newWorkStealingPool();
        firewallListenerThreadPool.execute(firewallService);
    }

    /**
     * Launches all network threads
     * @see MainController#launchFirewall()
     * @see MainController#launchImageReceiver()
     * @see MainController#launchImageSender()
     * @see MainController#launchSmsSensor()
     * @see MainController#launchUserDeviceReferenceRegister()
     */
    private void launchThreads() {

        printThreads();
        try {
            launchImageSender();

        } catch (Exception e) {
            ConsoleOutput.print(getClass().getName(), "Couldn't launch image sending thread. Abort");
            Platform.exit();
        }
        printThreads();
        try
        {
            launchImageReceiver();
        } catch (Exception e) {
            ConsoleOutput.print(getClass().getName(),"Couldn't launch image receiver thread. Abort");
            Platform.exit();
        }

        try
        {
            launchUserDeviceReferenceRegister();
        } catch (Exception e)
        {
            ConsoleOutput.print(getClass().getName(),"Couldn't launch device receiver thread. Abort");
            Platform.exit();
        }
        printThreads();

        try{

            launchSmsSensor();
        }catch (Exception e)
        {
            ConsoleOutput.print(getClass().getName(),"Couldn't launch sms service thread. Abort");
            Platform.exit();
        }
        printThreads();

        try{
            launchFirewall();
        } catch (Exception e)
        {
            ConsoleOutput.print(getClass().getName(),"Couldn't launch firewall thread pool. Abort");
            Platform.exit();
        }
        printThreads();

        Platform.runLater(() ->addText("All threads seems to work properly. I am now accepting TCP connections from anyone"));
        Platform.runLater(() ->addText("Possible gsm input delay approximately 30 seconds"));
        Platform.runLater(() ->addText("\u24D2 http://www.health-system.eu hosting on " + DynamicVariables.ipv4Finder()));

    }

    /**
     * Method for saving all terminal data to file
     * @throws IOException if file can not be written
     */
    private void saveLogFile() throws IOException
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("log");
        fileChooser.setTitle("Save Terminal Information");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TextFiles",".*txt"));
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);
        if (TerminalTextFlow.getChildren() != null)
        {
            String text = "";
            for(Node n : TerminalTextFlow.getChildren())
            {
                if(n instanceof Text)
                {
                    text += ((Text) n).getText() + System.getProperty("line.separator");
                }
            }

            PrintWriter printWriter = new PrintWriter(file+".txt");
            printWriter.print(text);
            printWriter.close();
        }
    }


    /**
     * Method to print amount threads currently running in application
     */
    private void printThreads()
    {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

        ConsoleOutput.print("Thread count: " + threadArray.length);
    }

}