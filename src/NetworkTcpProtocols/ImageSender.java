package NetworkTcpProtocols;

import SavedVariables.FinalConstants;
import HelpClasses.ConsoleOutput;
import Interfaces.MultipleParameterFunction;
import NetworkObjects.ImageObject;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <h2>Created by Marius Baltramaitis on 07/06/2017.</h2>
 *
 * <p>Opposite of ImageReceiver. This protocol is responsible for reading image from SSD, converting it to byte array and sending back to client</p>
 * @see ImageObject
 * @see ImageReceiver
 */
public class ImageSender implements MultipleParameterFunction<Runnable,Socket,TextFlow> {


    /**
     * Method to map image and convert data to byte array. Images are sorted by user type and ID
     * @param imageObject object with information of user
     * @return byte array if image of imageObject exits, null otherwise
     * @see ImageObject
     */
    private byte[] getImage(ImageObject imageObject)
    {
        String imgPath;
        ConsoleOutput.print(getClass().getName(),"Mapping image for " + imageObject.getName());
        switch (imageObject.getType())
        {
            case "Doctor":
                imgPath = FinalConstants.DOCTOR_IMAGE_PATH+ imageObject.getID()+".gif";
                ConsoleOutput.print("Path " + imgPath);
                break;
            case "Patient":
                imgPath = FinalConstants.PATIENT_IMAGE_PATH+"/"+ imageObject.getID()+".gif";
                break;
            case "Admin":
                imgPath = FinalConstants.ADMIN_IMAGE_PATH+"/"+ imageObject.getID()+".gif";
                break;
            default:
                return null;
        }

        try {

            Path path = Paths.get(imgPath);
            byte [] imgBytes = Files.readAllBytes(path);
            return imgBytes;

        } catch (IOException e)
        {
            ConsoleOutput.print(getClass().getName(),"IOException inside getImage method. Don't worry. It might be that requested image is not existing. Check message");
            ConsoleOutput.print(getClass().getName(),e);
            return null;

        }

    }


    /**
     * This method is called immediately after connection is arrived
     * @param clientSocket Socket to read data from
     * @param terminal terminal where information will be written
     * @return functional interface Runnable with work to execute
     */
    @Override
    public Runnable apply (Socket clientSocket, TextFlow terminal) {
        return () -> {

            try {
                clientSocket.setSoTimeout(FinalConstants.SOCKET_TIMEOUT);
                ConsoleOutput.print(getClass().getName(),"Handshake with " + clientSocket.getInetAddress());
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                ImageObject imageObject = (ImageObject)objectInputStream.readObject();
                ConsoleOutput.print(getClass().getName() + " " + clientSocket.getInetAddress().getHostAddress() + " " + imageObject.getName() + " " + imageObject.getID() + " " + imageObject.getMessageToServer());
                imageObject.setPersonImageBytes(getImage(imageObject));


                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(imageObject);
                clientSocket.close();

            }
            catch (IOException e)
            {
                ConsoleOutput.print(getClass().getName()," IOException " + getClass().getName() + " Possible reasons:" + System.lineSeparator() +
                        "1) Socket has reached timeout" + System.lineSeparator() +
                        "2) Error on opening/closing Streams");
                ConsoleOutput.print(getClass().getName(),e);
                try {
                    clientSocket.close();
                } catch (IOException e1) {
                    ConsoleOutput.print(getClass().getName(),"Error on closing socket!");
                }

            }catch (ClassNotFoundException e)
            {
                ConsoleOutput.print(getClass().getName(),"ClassNotFoundException");
                ConsoleOutput.print(getClass().getName(),e);
            }

        };

    }
}
