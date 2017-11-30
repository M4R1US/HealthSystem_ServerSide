package Controllers;

import NetworkObjects.TcpConnectionPair;
import Registers.DefaultRegister;
import Registers.DeviceReferenceRegister;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * <h2>Created by Marius Baltramaitis on 13/04/2017</h2>
 * <p>This class is attached to UserModification window which has list of all active users and ability to disable them</p>
 */
public class UserModification implements Initializable {

    public Button DisableByLoginButton,DisableByIpButton;
    private Consumer<String> updateTerminalFunction;
    public ListView<TcpConnectionPair> activeUserList;
    public ObservableList<TcpConnectionPair> myObservableList;
    public Stage currentStage;
    public DefaultRegister defaultRegister;

    /**
     * Initializes local variables from fxml file, also attaches events for corresponding nodes
     * @param location fxml path
     * @param resources CustomResourceBundle object to initialize bypassed objects
     * @see Actions.WindowLauncher
     * @see Classes.CustomResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        updateTerminalFunction = (Consumer<String>)resources.getObject("UpdateTerminalFunction");
        currentStage = (Stage)resources.getObject("Stage");

        DisableByLoginButton.setOnMouseClicked(event -> {
            if(activeUserList.getSelectionModel().getSelectedItem() != null)
            {
                defaultRegister = new DefaultRegister(updateTerminalFunction);
                defaultRegister.disable(activeUserList.getSelectionModel().getSelectedItem().getFirst().getLogin(),activeUserList.getSelectionModel().getSelectedItem().getFirst().getType());
                currentStage.close();

            }
        });

        DisableByIpButton.setOnMouseClicked(event -> {
            if(activeUserList.getSelectionModel().getSelectedItem() != null)
            {
                TcpConnectionPair pair = activeUserList.getSelectionModel().getSelectedItem();
                updateTerminalFunction.accept("Kicking everyone from " + pair.getSecond().getInetAddress());
                DeviceReferenceRegister.disableAllByIP(pair.getSecond().getInetAddress().toString(),updateTerminalFunction);
                currentStage.close();

            }
        });
        myObservableList = FXCollections.observableList(DeviceReferenceRegister.getList());
        activeUserList.setItems(myObservableList);
    }

}
