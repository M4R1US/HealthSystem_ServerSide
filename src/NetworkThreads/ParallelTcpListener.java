package NetworkThreads;

import SavedVariables.FinalConstants;
import HelpClasses.ConsoleOutput;
import Interfaces.MultipleParameterFunction;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h2>Created by Marius Baltramaitis on 07/06/2017</h2>
 * <p>
 *     Async tcp listener
 * </p>
 */
public class ParallelTcpListener extends Task<Void> {

    private TextFlow terminal;
    private ServerSocket serverSocket;
    private Socket connectedSocket;
    private ExecutorService threadPool;
    private MultipleParameterFunction<Runnable,Socket,TextFlow> tcpProtocolInterface;

    /**
     * Constructor taking necessary arguments
     * @param terminal Terminal where text should be written
     * @param startupText Text to write out once thread is launched successfully
     * @param tcpProtocolInterface interface with instructions what to do with socket
     * @param port port number to open tcp server socket
     * @throws IOException if port is already taken
     */
    public ParallelTcpListener(TextFlow terminal, String startupText, MultipleParameterFunction<Runnable,Socket,TextFlow> tcpProtocolInterface, int port) throws IOException
    {
        if(port < 0)
            throw new IllegalArgumentException("Frankly ports cannot be < 0!");

        this.terminal = terminal;
        this.tcpProtocolInterface = tcpProtocolInterface;
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newWorkStealingPool();
        Platform.runLater(() -> terminal.getChildren().add(generateText(startupText)));
    }

    /**
     * Method to generate text to terminal
     * @param textSource String value text
     * @return Text object with textSource string value
     */
    private Text generateText(String textSource)
    {
        Text text = new Text(textSource+System.lineSeparator());
        text.setFill(Paint.valueOf(FinalConstants.TERMINAL_DEFAULT_COLOR));
        text.setFont(FinalConstants.TERMINAL_FONT);
        return text;
    }


    /**
     * Method for this thread to be always awake and listen for connection
     * @return recursion call always as long as this thread is not interrupted
     */
    private Void listen()
    {
        try
        {
            connectedSocket = serverSocket.accept();
            if(connectedSocket.isBound() && connectedSocket != null)
            {
                ConsoleOutput.print(getClass().getName(),"Received connection from " + connectedSocket.getInetAddress());
                threadPool.submit(tcpProtocolInterface.apply(connectedSocket,terminal));

            }
        }
        catch (IOException e)
        {
            ConsoleOutput.print(getClass().getName(),"IOException inside parallel tcp listener.Ignore this if you are exiting program");
            ConsoleOutput.print(getClass().getName(),e);
        }

        return (serverSocket.isClosed() ? null : listen());
    }

    /**
     * Start method for listening for connections
     * @return always listen method
     * @throws Exception when thread is interrupted
     * @see ParallelTcpListener#listen()
     */
    @Override
    protected Void call() throws Exception { return  listen(); }

    /**
     * Getter for server socket
     * @return server socket of this thread
     */
    public ServerSocket getServerSocket() {return serverSocket;}
}
