package NetworkTcpProtocols;

import Interfaces.TerminalTextGeneration;
import SavedVariables.FinalConstants;
import HelpClasses.ConsoleOutput;
import Interfaces.MultipleParameterFunction;
import NetworkObjects.TcpConnectionPair;
import NetworkObjects.UserReference;
import Registers.DeviceReferenceRegister;
import javafx.application.Platform;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * <h2>Created by Marius Baltramaitis on 13/06/2017.</h2>
 * <p>Protocol to register online user references</p>
 *
 * <p>This protocol opens Socket, reads data and adds reference to DeviceReferenceRegister</p>
 * @see DeviceReferenceRegister
 */
public class DeviceRegister implements MultipleParameterFunction<Runnable,Socket,TextFlow>, TerminalTextGeneration {


    /**
     * This method is called immediately after connection is arrived
     * @param clientSocket Socket to read data from
     * @param terminal terminal where information will be written
     * @return functional interface Runnable with work to execute
     */
    @Override
    public Runnable apply(Socket clientSocket, TextFlow terminal) {
        return () -> {
            try {

                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                UserReference userReference = (UserReference) objectInputStream.readObject();
                if(userReference.getLogin() != null && userReference.getName() != null && userReference.getType() != null && userReference.getAction() == 1)
                {
                    DeviceReferenceRegister.add(new TcpConnectionPair(userReference,clientSocket));
                    ConsoleOutput.print("Added " + userReference.toString()  + "to the list");
                    Platform.runLater(() -> terminal.getChildren().add(generateText(userReference.getName() + " just logged inn from " + clientSocket.getInetAddress().toString(),FinalConstants.TERMINAL_DEVICE_CONNECTION_TEXT_COLOR)));
                }
                if(userReference.getLogin() != null && userReference.getName() != null && userReference.getType() != null && userReference.getAction() == 0)
                {
                    ConsoleOutput.print("Removing device");
                    DeviceReferenceRegister.remove(DeviceReferenceRegister.findBySocket(clientSocket));
                    Platform.runLater(() -> terminal.getChildren().add(generateText(userReference.getName() + " just logged out " + clientSocket.getInetAddress(),FinalConstants.TERMINAL_DEVICE_CONNECTION_TEXT_COLOR)));
                    return;
                }

            }
            catch (IOException e)
            {
                ConsoleOutput.print(getClass().getName()," IOException inside task! Possible reasons:" +
                        "\n1) Socket has reached timeout" +
                        "\n2) Error on opening/closing Streams");
                ConsoleOutput.print(getClass().getName(),e);

                try {
                    clientSocket.close();

                } catch (IOException e1) {
                    ConsoleOutput.print(getClass().getName(),"Error on closing socket!");
                }

            } catch (ClassNotFoundException e) {
                ConsoleOutput.print(getClass().getName(),e);
            }
        };
    }


    /**
     * Method for kicking user out of system
     * @param connectionPair pair with socket and user information
     * @see TcpConnectionPair
     */
    public synchronized static void kick(TcpConnectionPair connectionPair)
    {
        if(connectionPair == null)
            return;

        Socket socket = connectionPair.getSecond();


        UserReference userReference = connectionPair.getFirst();

        if(socket == null)
            return;

        userReference.setAction((byte)0);

        try {

            socket.setSoTimeout(FinalConstants.SOCKET_TIMEOUT);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(userReference);

        } catch (SocketException socketException)
        {
            ConsoleOutput.print(DeviceRegister.class.getName(),socketException);
        }
        catch (IOException e)
        {
            ConsoleOutput.print(" Exception! Couldn't open objectOutputStream or socket timeout couldn't be set");
        }
    }
}
