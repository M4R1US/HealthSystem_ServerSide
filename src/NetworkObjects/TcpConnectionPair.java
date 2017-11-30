package NetworkObjects;

import Classes.GenericPair;
import com.sun.istack.internal.NotNull;

import java.net.Socket;

/**
 * <h2>Created by Marius Baltramaitis on 15/04/2017.</h2>
 *  <p> Object pointing to User reference and tcp Socket </p>
 *  @see GenericPair
 *  @see UserReference
 */
public class TcpConnectionPair extends GenericPair<UserReference,Socket> {

    /**
     * Constructor taking necessary parameters. None of these can be null
     * @param userReference UserReference
     * @param socket Socket with pipeline
     * @see UserReference
     */
    public TcpConnectionPair(@NotNull UserReference userReference, @NotNull Socket socket)
    {
        super(userReference, socket);
    }

    /**
     * Overriding toString method
     * @return information of this connection pair
     */
    public String toString()
    {
        return super.getFirst().getName() + ", " + "Type = " + super.getFirst().getType() + ", Login = " + super.getFirst().getLogin() + ", IPv4 and port " + super.getSecond().getInetAddress() + ":" + super.getSecond().getPort();
    }

    /**
     * Method to compare objects
     * @param o object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof TcpConnectionPair))
            return false;

        return (((TcpConnectionPair) o).getFirst().equals(this.getFirst()) && this.getSecond().getInetAddress().toString().equals(((TcpConnectionPair) o).getSecond().getInetAddress().toString()) && this.getSecond().getPort() == ((TcpConnectionPair) o).getSecond().getPort());

    }


    /**
     * @param userReference object compare to
     * @return this class if user reference is equal
     */
    public TcpConnectionPair equivalentByUserReference(UserReference userReference)
    {
        return getFirst().equals(userReference) ? this : null;
    }


    /**
     * method to compare ipv4 addresses
     * @param ipv4 ip version 4 adress
     * @return true if ip addresses are equal
     */
    public boolean equalByIpv4(String ipv4)
    {
        return super.getSecond().getInetAddress().toString().equals(ipv4);
    }


    /**
     * Method to compare if this pair has same login and type
     * @param login login to compare
     * @param type type to compare
     * @return if login and type are the same as login and type registered in this class
     */
    public boolean equalsByLoginAndType(String login, String type)
    {
        return super.getFirst().getLogin().equals(login) && super.getFirst().getType().equalsIgnoreCase(type);
    }



}
