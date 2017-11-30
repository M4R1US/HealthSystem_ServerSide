package Controllers;

import Actions.Validation;
import ApplicationUsers.Administrator;
import Listeners.PhoneNumberPropertyListener;
import Registers.SmsFunctionality;
import SavedVariables.FinalConstants;
import ListCells.CountryCodeCell;
import Listeners.TextPropertyListener;
import Registers.DefaultRegister;
import Security.BCrypt;
import Security.RandomPasswordGenerator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * <h2>Created by Marius B on 04-Feb-17.</h2>
 *
 * <p>Controller class to insert administrator to database.</p>
 */
public class InsertAdminController implements Initializable {


    @FXML
    public BorderPane InsertAdminPane;
    public TextField FirstNameField,LastNameField,PhoneNumberField;
    public CheckBox SmsCheckBox,SaveNumberCheckBox;
    public VBox PhoneDetailsVBox;
    public ComboBox SexComboBox;
    public TextArea AdditionalInfoTextArea;
    public Button InsertAdminButton;
    public ComboBox<CountryCodeCell> CountryComboBox;

    private Stage currentStage;
    public CountryCodeCell NorwegianCell,LithuanianCell;
    private Consumer<String> updateTerminalFunction;

    /**
     * Initializes local variables from fxml file, also attaches events for corresponding nodes
     * @param location fxml path
     * @param resources resources from windowLauncher class
     * @see Actions.WindowLauncher
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        updateTerminalFunction = (Consumer<String>)resources.getObject("UpdateTerminalFunction");
        addListeners();
        NorwegianCell = new CountryCodeCell("Icons/x16/NO.png","[NO +47]","+47");
        LithuanianCell = new CountryCodeCell("Icons/x16/LT.png","[LT +370]","+370");
        CountryComboBox.getItems().add(NorwegianCell);
        CountryComboBox.getItems().add(LithuanianCell);

        CountryComboBox.setCellFactory((ListView<CountryCodeCell> param) -> new ListCell<CountryCodeCell>() {

            @Override
            protected void updateItem(CountryCodeCell item, boolean empty) {
                if (empty)
                    super.updateItem(NorwegianCell, empty);
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    CountryCodeCell current = new CountryCodeCell(item.getIconSrc(), item.getCountryCodeDisplayText(),item.getCountryCode());
                    setGraphic(current);
                }
            }
        });

        CountryComboBox.getSelectionModel().select(0);

        SmsCheckBox.selectedProperty().addListener(observable -> {
            boolean disabledSms = (SmsCheckBox.isSelected() ? false : true);
            PhoneDetailsVBox.setDisable(disabledSms);
        });
        InsertAdminButton.setOnMouseClicked(event -> insertAdmin());

    }

    /**
     * Method for insert administrator to database.
     * <p> Data is validated before inserting to database</p>
     */
    private void insertAdmin()
    {
        String phoneNumber = PhoneNumberField.getText();
        String firstName = FirstNameField.getText();
        String lastName = LastNameField.getText();
        String sex = SexComboBox.getSelectionModel().getSelectedItem().toString();
        String additionalInfo = AdditionalInfoTextArea.getText();


        if(firstName.length() < 2)
        {
            FirstNameField.setBorder(FinalConstants.RED_BORDER);
            return;
        }

        if(lastName.length() < 2)
        {
            LastNameField.setBorder(FinalConstants.RED_BORDER);
            return;
        }
        String password = RandomPasswordGenerator.generatePassword();
        String salt = BCrypt.gensalt();
        String encryptedPassword = BCrypt.hashpw(password,salt);
        currentStage = (Stage)InsertAdminPane.getScene().getWindow();

        Administrator administrator = new Administrator(firstName,lastName,sex,additionalInfo,salt,encryptedPassword);

        DefaultRegister administratorRegister = new DefaultRegister(updateTerminalFunction);
        administrator.setPassword(password);

        if(SmsCheckBox.isSelected())
        {
            phoneNumber = CountryComboBox.getSelectionModel().getSelectedItem().getCountryCode() + PhoneNumberField.getText();

            if(!Validation.validInt(PhoneNumberField.getText(),8,8,false))
                return;

            else
            {
                administratorRegister.insertAdmin(administrator,phoneNumber);
                currentStage.close();
                return;
            }
        }

        if(SmsCheckBox.isSelected() && SaveNumberCheckBox.isSelected())
        {
            if(Validation.validInt(PhoneNumberField.getText(),8,8,false))
            {
                SmsFunctionality.insertAdminPhoneNumber(phoneNumber,firstName+" " + lastName);
                administratorRegister.insertAdmin(administrator,phoneNumber);
                currentStage.close();
            } else
                return;
        } else {
            currentStage.close();
            administratorRegister.insertAdmin(administrator,phoneNumber);
        }


    }

    /**
     * Attaches listeners to text fields
     */
    private void addListeners()
    {
        FirstNameField.textProperty().addListener(new TextPropertyListener(FirstNameField));
        LastNameField.textProperty().addListener(new TextPropertyListener(LastNameField));
        PhoneNumberField.textProperty().addListener(new PhoneNumberPropertyListener(PhoneNumberField,8,8));
        AdditionalInfoTextArea.textProperty().addListener(new TextPropertyListener(AdditionalInfoTextArea));

    }

}
