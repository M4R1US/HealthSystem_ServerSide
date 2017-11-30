package SavedVariables;

import Classes.Reboot;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * <h2>Created by Marius Baltramaitis on 03-Feb-17.</h2>
 *
 * <p>This class is holder for static final variables that can not be changed in runtime</p>
 */
public final class FinalConstants {

    //Timeouts
    public static final int SOCKET_TIMEOUT = 5000; //5 seconds

    //Colors
    public static final String BORDER_RED_COLOR = "#D80027";
    public static final String BORDER_GREEN_COLOR = "#228B22";
    public static final String TERMINAL_IMAGE_RECEIVE_TEXT_COLOR = "#006400";
    public static final String TERMINAL_IMAGE_SEND_TEXT_COLOR = "#DC143C";
    public static final String TERMINAL_DEVICE_CONNECTION_TEXT_COLOR = "#009999";
    public static final String TERMINAL_SMS_INBOUND_TEXT_COLOR = "#0000FF";
    public static final String TERMINAL_FIREWALL_TEXT_COLOR = "#F2784B";
    public static final String TERMINAL_DEFAULT_COLOR = "#f8f8f8";


    //Ports
    public static final int TCP_DEVICE_CONNECTION_PORT = 5554;
    public static final int SMS_INBOUND_RECEIVE_PORT = 5555;
    public static final int IMAGE_SENDER_PORT = 5556;
    public static final int IMAGE_RECEIVE_PORT = 5557;
    public static final int FIREWALL_PORT = 5558;

    //Sizes
    public static final int MAIN_WINDOW_WIDTH = 1200;
    public static final int MAIN_WINDOW_HEIGHT = 600;
    public static final int MAX_TERMINAL_LINES = 1000;

    // Functional interfaces
    public static final Runnable REBOOT_FUNCTION = () -> Reboot.unixReboot();

    //PATHS

    public static final String DOCTOR_IMAGE_PATH= "Doctors/";
    public static final String PATIENT_IMAGE_PATH="Patients/";
    public static final String ADMIN_IMAGE_PATH="Admins/";
    //public static final String CRYPTOGRAPHY_KEY_PATH = "/Users/mariusbaltramaitis/Documents/keys/aes128.key";
    public static final String CRYPTOGRAPHY_KEY_PATH = "aes128.key";
    public static final String CREDENTIALS_FILE_PATH = "credentials.dta";
    public static final String ADMIN_PHONE_BOOK_FILE_PATH = "adminPhoneBook.dta";
    //public static final String ADMIN_PHONE_BOOK_FILE_PATH = "/Users/mariusbaltramaitis/Documents/keys/adminPhoneBook.dta";
    public static final String SMS_QUESTIONS_FILE_PATH = "smsQuestions.dta";
    //public static final String CREDENTIALS_FILE_PATH = "/Users/mariusbaltramaitis/Documents/keys/credentials.dta";

    //Censure
    //Validation Borders

    public static final Border RED_BORDER = new Border(new BorderStroke(Paint.valueOf(BORDER_RED_COLOR), BorderStrokeStyle.SOLID,new CornerRadii(3.0),null));
    public static final Border GREEN_BORDER = new Border(new BorderStroke(Paint.valueOf(BORDER_GREEN_COLOR), BorderStrokeStyle.SOLID,new CornerRadii(3.0),null));

    //Legal input chars
    public static final  String validTextLettersLowercase = "qwertyuiopåasdfghjkløæzxcvbnm_";
    public static final  String allValidTextLetters = validTextLettersLowercase + validTextLettersLowercase.toUpperCase();
    public static final  String INTEGERS = "0123456789";
    public static final  String allValidTextAreaLetters = allValidTextLetters + ".!?';, "+INTEGERS;

    //Font
    public static final Font TERMINAL_FONT = new Font(20);

}
