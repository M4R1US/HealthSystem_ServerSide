package NetworkTcpProtocols;

import Actions.SmsOutPut;
import Classes.GenericPair;
import Interfaces.TerminalTextGeneration;
import Registers.DefaultRegister;
import Registers.SmsFunctionality;
import SavedVariables.DynamicVariables;
import SavedVariables.FinalConstants;
import HelpClasses.ConsoleOutput;
import Interfaces.MultipleParameterFunction;
import Registers.DeviceReferenceRegister;
import javafx.application.Platform;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * <h2>Created by Marius Baltramaitis on 13/06/2017.</h2>
 * <p> Protocol to receive sms message and analyze input</p>
 */
public class SmsInput implements MultipleParameterFunction<Runnable,Socket,TextFlow>, TerminalTextGeneration {

    private GenericPair<String,String> numberAndMessage;
    private String phoneNumber,message;
    private TextFlow terminal;

    /**
     * This method is called immediately after connection is arrived
     * @param clientSocket Socket to read data from
     * @param terminal terminal where information will be written
     * @return functional interface Runnable with work to execute
     */
    @Override
    public Runnable apply(Socket clientSocket, TextFlow terminal) {

        this.terminal = terminal;
        return () -> {
            if(clientSocket.isBound() && !clientSocket.isClosed())
            {
                try {
                    Scanner s = new Scanner(clientSocket.getInputStream()).useDelimiter("\\A");
                    String gsmInput = s.hasNext() ? s.next() : "";
                    ConsoleOutput.print("GSM INPUT " + gsmInput);
                    clientSocket.close();

                    detectMessageAndNumber(gsmInput,terminal);

                } catch (IOException e)
                {
                    ConsoleOutput.print(getClass().getName(),e);
                }
            }
        };
    }

    /**
     * Method to separate phone number and message content from one long string value
     * @param gsmInput whole input
     * @param terminal terminal where text should be written
     */
    private void detectMessageAndNumber(String gsmInput,TextFlow terminal)
    {
        try {

            List<String> input = Arrays.asList(gsmInput.split("/"));
            input.forEach(info -> ConsoleOutput.print(info));
            phoneNumber = input.get(0);
            message = input.get(1);
            String accountSid = input.get(2);

            if(!accountSid.equalsIgnoreCase(DynamicVariables.ACCOUNT_SID))
            {
                ConsoleOutput.print("Someone is spoofing messages again. Abort");
                return;
            }
            numberAndMessage = new GenericPair<>(phoneNumber,message);
            Platform.runLater(() -> terminal.getChildren().add(generateText("Inbound sms from : " + numberAndMessage.getFirst()+ " with context : " + numberAndMessage.getSecond(),FinalConstants.TERMINAL_SMS_INBOUND_TEXT_COLOR)));
            analyzeMessage();
        } catch (IndexOutOfBoundsException e)
        {
            ConsoleOutput.print("Couldn't detect sms message and identify phone number");
        }
    }

    /**
     * Method to analyze message and deciding what to do depending on message content and phone number
     */
    private void analyzeMessage()
    {
        Platform.runLater(() ->terminal.getChildren().add(generateText("Analyzing message",FinalConstants.TERMINAL_SMS_INBOUND_TEXT_COLOR)));
        if(numberAndMessage == null)
            return;

        String [] commands = numberAndMessage.getSecond().split("#");

        if(commands == null)
            return;

        switch (commands[0])
        {
            case "!disable":
                String name = SmsFunctionality.isAdministrator(phoneNumber);
                if(name != null)
                {
                    Platform.runLater(() ->terminal.getChildren().add(generateText(phoneNumber + " is authenticated as " + name,FinalConstants.TERMINAL_SMS_INBOUND_TEXT_COLOR)));
                    disable(commands[1]);
                }
                break;

            case "!reboot_server_control_panel":
                if(SmsFunctionality.isAdministrator(phoneNumber) != null)
                    FinalConstants.REBOOT_FUNCTION.run();
                break;

            case "!ask":
                if(commands[1] != null)
                {
                    String answer = SmsFunctionality.askQuestion(commands[1]);
                    if(answer != null)
                    {
                        Platform.runLater(() -> terminal.getChildren().add(generateText("Replying to :" + phoneNumber + " : " + answer,FinalConstants.TERMINAL_SMS_INBOUND_TEXT_COLOR)));
                        SmsOutPut smsOutPut = new SmsOutPut(answer,phoneNumber);
                        smsOutPut.deliverMessage();
                    }
                }

                break;

            default:
                break;
        }
    }

    /**
     * Method to disable user and kick from system
     * @param messageContext whole message content
     */
    private void disable(String messageContext)
    {
        String [] commands = messageContext.split("\\s+");
        String type = commands[0];
        String login = commands[1];

        if(type.equalsIgnoreCase("Admin"))
            return;

        DefaultRegister defaultRegister = new DefaultRegister((String text) -> Platform.runLater(() -> terminal.getChildren().add(generateText(text,FinalConstants.TERMINAL_SMS_INBOUND_TEXT_COLOR))));
        defaultRegister.disable(login,type);
        DeviceReferenceRegister.kickByLoginAndType(login,type);
    }
}
