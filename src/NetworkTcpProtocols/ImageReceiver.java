package NetworkTcpProtocols;

import Interfaces.TerminalTextGeneration;
import SavedVariables.FinalConstants;
import HelpClasses.ConsoleOutput;
import Interfaces.MultipleParameterFunction;
import NetworkObjects.ImageObject;
import javafx.application.Platform;
import javafx.scene.text.TextFlow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * <h2>Created by Marius Baltramaitis on 07/06/2017.</h2>
 * <p>Protocol for receiving image and saving to SSD</p>
 */
public class ImageReceiver implements MultipleParameterFunction<Runnable,Socket,TextFlow>, TerminalTextGeneration {

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
                clientSocket.setSoTimeout(FinalConstants.SOCKET_TIMEOUT);
                ConsoleOutput.print(getClass().getName(),"Handshake With " + clientSocket.getInetAddress());
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                ImageObject imageObject = (ImageObject)objectInputStream.readObject();
                ConsoleOutput.print(getClass().getName() + " " + clientSocket.getInetAddress().getHostAddress() + " " + imageObject.getName() + " " + imageObject.getID() + " " + imageObject.getMessageToServer());
                Platform.runLater(() -> terminal.getChildren().add(generateText(imageObject.getName() + " [" + imageObject.getType()+"] " + imageObject.getID() +" from " +clientSocket.getInetAddress().getHostAddress() + " " + imageObject.getMessageToServer(),FinalConstants.TERMINAL_IMAGE_RECEIVE_TEXT_COLOR)));

                if(imageObject.getPersonImageBytes() != null)
                {
                    if(!saveImage(imageObject))
                    Platform.runLater(() -> terminal.getChildren().add(generateText("Couldn't update picture",FinalConstants.TERMINAL_IMAGE_RECEIVE_TEXT_COLOR)));

                }

                objectInputStream.close();
                clientSocket.close();

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
                ConsoleOutput.print(getClass().getName(),"ClassNotFoundException Inside Task!");
                ConsoleOutput.print(getClass().getName(),e);
            }
        };
    }

    /**
     * Method to save image from imageObject
     * @param object object to read,decode byte array to image and save file to SSD
     * @return true if image is saved, false otherwise
     * @see ImageObject
     */
    private boolean saveImage(ImageObject object)
    {
        String path;
        switch (object.getType())
        {
            case "Doctor" :
                path = FinalConstants.DOCTOR_IMAGE_PATH;
                break;

            case "Admin" :
                path = FinalConstants.ADMIN_IMAGE_PATH;
                break;

            case "Patient" :
                path = FinalConstants.PATIENT_IMAGE_PATH;
                break;

            default:
                throw new UnsupportedOperationException("Unknown person type!");
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(path+object.getID()+".gif")) {
            fileOutputStream.write(object.getPersonImageBytes());
            return true;

        } catch (IOException e) {
            ConsoleOutput.print(getClass().getName(),"Couldn't write image");
            return false;

        }

    }

}
