package Registers;

import HelpClasses.ConsoleOutput;
import NetworkObjects.TcpConnectionPair;
import NetworkTcpProtocols.DeviceRegister;

import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <h2>Created by Marius Baltramaitis on 13/04/2017.</h2>
 *
 * <p>Final register class to register device references</p>
 * <p>Java collections are not supporting multithreading so therefore all methods are synchronized</p>
 *  @see TcpConnectionPair
 */
public final class DeviceReferenceRegister {

    private static ArrayList<TcpConnectionPair> activeUsers = new ArrayList<>();

    /**
     * Method to get function that is iterating through array list and looking for equal connection pair
     * @param tcpConnectionPair connection pair to find
     * @return function that is looking for connection pair equal to parameters
     */
    private static Supplier<TcpConnectionPair> finder(TcpConnectionPair tcpConnectionPair)
    {
        if(tcpConnectionPair == null || activeUsers.isEmpty())
            return null;

        return () -> {

            for(TcpConnectionPair x : activeUsers)
            {
                if(x.equals(tcpConnectionPair))
                    return x;
            }
            return null;
        };
    }

    /**
     * Method to insert connection pair
     * @param connectionPair pair to insert into register
     */
    public synchronized static void add(TcpConnectionPair connectionPair)
    {
        if(finder(connectionPair) == null || finder(connectionPair).get() == null)
        {
            activeUsers.add(connectionPair);
            ConsoleOutput.print("Added new connection pair to register" + activeUsers.size());
        }
    }

    /**
     * Method to remove connection pair from register
     * @param pair connection pair to remove
     */
    public synchronized static void remove(TcpConnectionPair pair)
    {
        ConsoleOutput.print("Remove method");
        if(pair == null)
            return;

        ConsoleOutput.print("Socket and port: " + pair.getSecond().getInetAddress().toString() + " : " + pair.getSecond().getPort());

        activeUsers.remove(pair);

    }

    /**
     *  Method to find connection pair by Socket
     * @param socket socket with port number and ipv4 address
     * @return connection pair if found, null otherwise
     */
    public synchronized static TcpConnectionPair findBySocket(Socket socket)
    {
        if(socket == null || activeUsers.isEmpty())
            return null;

        for(TcpConnectionPair x : activeUsers)
            if(x.getSecond().getInetAddress().toString().equals(socket.getInetAddress().toString()) && x.getSecond().getLocalPort() == socket.getLocalPort())
                return x;

        return null;
    }


    /**
     * Getter for list
     * @return array list with all tcp connection objects
     */
    public synchronized static ArrayList<TcpConnectionPair> getList() {return activeUsers;}

    public synchronized static void kickByLoginAndType(String login, String type)
    {
        if(activeUsers.size() == 0 || login.equals("") || login == null || type.equals("") || type == null)
            return;

        activeUsers.forEach(user -> {
            if(user.equalsByLoginAndType(login, type))
                DeviceRegister.kick(user);
        });
    }

    /**
     * Method to disable all online users that share same ipv4 address.
     * @param ipv4 ip version 4 address
     * @param terminalUpdateFunction function to update terminal
     */
    public synchronized static void disableAllByIP(String ipv4, Consumer<String> terminalUpdateFunction)
    {
        if(activeUsers.size() == 0 || ipv4.equals("") || ipv4 == null)
            return;

        activeUsers.forEach(user -> {
            if(user.equalByIpv4(ipv4))
            {
                DefaultRegister defaultRegister = new DefaultRegister(terminalUpdateFunction);
                defaultRegister.disable(user.getFirst().getLogin(),user.getFirst().getType());
                DeviceRegister.kick(user);
            }
        });
    }

}
