package NetworkTcpProtocols;

import Interfaces.TerminalTextGeneration;
import SavedVariables.DynamicVariables;
import SavedVariables.FinalConstants;
import DatabaseConnection.DatabaseConnectionConfiguration;
import HelpClasses.ConsoleOutput;
import Interfaces.MultipleParameterFunction;
import NetworkObjects.FirewallCredentials;
import Security.BCrypt;
import Security.Cryptography;
import javafx.application.Platform;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <h2>Created by Marius Baltramaitis on 06/10/2017.</h2>
 *
 * <p>This class is responsible of releasing through login connections</p>
 *
 */
public class FirewallAction implements MultipleParameterFunction<Runnable,Socket,TextFlow>, TerminalTextGeneration {


    private Socket clientSocket;
    private String decryptedLogin,decryptedPassword, decryptedType;
    private FirewallCredentials firewallCredentials;
    private TextFlow windowTerminal;

    /**
     * This method is called immediately after connection is arrived
     * @param clientSocket Socket to read data from
     * @param windowTerminal terminal where text should be written
     * @return functional interface Runnable with work to execute
     */
    @Override
    public Runnable apply(Socket clientSocket, TextFlow windowTerminal) {

        return  () -> {
            this.clientSocket = clientSocket;
            this.windowTerminal = windowTerminal;

            try {
                clientSocket.setSoTimeout(FinalConstants.SOCKET_TIMEOUT);
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                firewallCredentials = (FirewallCredentials)objectInputStream.readObject();
                ConsoleOutput.print("Arrived connection from " + clientSocket.getInetAddress() + " " + clientSocket.getPort());
                Platform.runLater(() -> windowTerminal.getChildren().add(generateText("Arrived connection from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + ". Analyzing tcp socket and input from client",FinalConstants.TERMINAL_FIREWALL_TEXT_COLOR)));

                try {
                    authenticate();
                } catch (UnsupportedOperationException e) {attemptToCloseSocket();}

            }

            catch (SocketException e) {ConsoleOutput.print(getClass().getName(),e.getMessage());}
            catch (IOException e) {ConsoleOutput.print(getClass().getName(),"Error with input/output streams");}
            catch (ClassNotFoundException e) {ConsoleOutput.print("Class not found exception, possible ");}

        };
    }

    /**
     * <p>
     *  Method for reading data from socket, gathering credentials, decrypting them, checking if they are valid by connecting to database
     *
     * </p>
     * @throws UnsupportedOperationException if user type is not supported
     */
    private void authenticate() throws UnsupportedOperationException
    {
        Cryptography cryptography = new Cryptography();
        try {
            decryptedLogin = cryptography.decrypt(firewallCredentials.getLogin(),DynamicVariables.AES_KEY);
            decryptedType = cryptography.decrypt(firewallCredentials.getType(),DynamicVariables.AES_KEY);

        } catch (Exception e) {ConsoleOutput.print(getClass().getName(),e);}

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        connection = DatabaseConnectionConfiguration.getConnection();
        ResultSet resultSet = null;

        try {

            switch(decryptedType)
            {
                case "Admin":
                    preparedStatement = connection.prepareStatement("Select * from Administrator where Login = ?");
                    preparedStatement.setString(1,decryptedLogin);
                    break;

                case "Pharmacy":
                    preparedStatement = connection.prepareStatement("Select * from Pharmacy where Login = ?");
                    preparedStatement.setString(1,decryptedLogin);
                    break;

                case "Patient":
                    preparedStatement = connection.prepareStatement("Select * from Patient where Login = ?");
                    preparedStatement.setString(1,decryptedLogin);
                    break;

                case "Doctor" :
                    preparedStatement = connection.prepareStatement("Select * from Doctor where Login = ?");
                    preparedStatement.setString(1,decryptedLogin);
                    break;

                default:
                    throw  new UnsupportedOperationException("Unsupported type of user");

            }
            // Sql exception here will never occur because this application and database is running locally
        } catch (SQLException e) { }

        try {
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getResultSet();

            while (resultSet.next())
            {
                int available = resultSet.getInt("available");
                String encryptedPassword = resultSet.getString("password");


                //if user is not available (banned)
                if(available == 0)
                {
                    ConsoleOutput.print("Access denied for " + clientSocket.getInetAddress() + " " + clientSocket.getPort());
                    Platform.runLater(() -> windowTerminal.getChildren().add(generateText("Access denied for" + clientSocket.getInetAddress() + ":" + clientSocket.getPort(),FinalConstants.TERMINAL_FIREWALL_TEXT_COLOR)));

                    firewallCredentials.setDatabaseLoginName("-1");
                    firewallCredentials.setDatabaseLoginPassword("-1");
                    deliverDetails();
                    return;
                }

                cryptography = new Cryptography();

                try {
                    decryptedPassword = cryptography.decrypt(firewallCredentials.getPassword(),DynamicVariables.AES_KEY);
                    firewallCredentials.setAccountSID(cryptography.encrypt(DynamicVariables.ACCOUNT_SID,DynamicVariables.AES_KEY));
                    firewallCredentials.setAuthToken(cryptography.encrypt(DynamicVariables.AUTH_TOKEN,DynamicVariables.AES_KEY));
                    firewallCredentials.setNorwegianNumber(cryptography.encrypt(DynamicVariables.NORWEGIAN_NUMBER,DynamicVariables.AES_KEY));
                    firewallCredentials.setLithuanianNumber(cryptography.encrypt(DynamicVariables.LITHUANIAN_NUMBER,DynamicVariables.AES_KEY));

                } catch (Exception e) {ConsoleOutput.print(getClass().getName(),e);}

                // if usr password matches
                if(BCrypt.checkpw(decryptedPassword,encryptedPassword))
                {
                    ConsoleOutput.print("Releasing through" + clientSocket.getInetAddress() + " " + clientSocket.getPort());
                    Platform.runLater(() -> windowTerminal.getChildren().add(generateText("Releasing through" + clientSocket.getInetAddress() + ":" + clientSocket.getPort(),FinalConstants.TERMINAL_FIREWALL_TEXT_COLOR)));


                    firewallCredentials.setDatabaseLoginPassword(DynamicVariables.ENCRYPTED_DB_PASSWORD);
                    firewallCredentials.setDatabaseLoginName(DynamicVariables.ENCRYPTED_DB_LOGIN);
                    deliverDetails();
                    return;

                }
            }


            ConsoleOutput.print("Login " + decryptedLogin);

            ConsoleOutput.print("Invalid credentials for " + clientSocket.getInetAddress() + " " + clientSocket.getPort() + " No access");
            Platform.runLater(() -> windowTerminal.getChildren().add(generateText("Invalid credentials for " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() +" No access ",FinalConstants.TERMINAL_FIREWALL_TEXT_COLOR)));

            //invalid login credentials
            firewallCredentials.setDatabaseLoginName("0");
            firewallCredentials.setDatabaseLoginPassword("0");
            deliverDetails();

            // Sql exception here will never occur because this application and database is running locally
        } catch (SQLException e) {ConsoleOutput.print(getClass().getName(),e);}

    }

    /**
     * Method to close socket
     */
    private void attemptToCloseSocket()
    {
        try {
            clientSocket.close();
        } catch (IOException e) {
            ConsoleOutput.print(getClass().getName(), "Couldn't close socket properly");
        }
    }

    /**
     * If user credentials are valid, delivering encrypted database password and other sensitive data to back to user through same socket
     * @see FirewallAction#authenticate()
     */
    private void deliverDetails()
    {
        try {
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer.writeObject(firewallCredentials);
            outToServer.close();
            attemptToCloseSocket();
        } catch (IOException e) {ConsoleOutput.print(getClass().getName(),e);}

    }
}
