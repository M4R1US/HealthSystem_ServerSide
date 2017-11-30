package DatabaseConnection;

import HelpClasses.ConsoleOutput;
import SavedVariables.DynamicVariables;

import java.sql.DriverManager;
import java.sql.Connection;

/**
 * <h2>Created by Marius Baltramaitis on 09-Feb-17.</h2>
 * <p>Driver for database connection.</p>
* <p>Based on system architecture connection from this server side must be LOCAL! </p>
 */
public class DatabaseConnectionConfiguration {

    public static final String DB_HOST = "127.0.0.1";
    private static final String DB_NAME = "HealthSystem";


    /**
     * Reference to database connection
     * @return database connection object if connection is established, null if otherwise
     */
    public static Connection getConnection() {

        Connection connection = null;

        String path = "jdbc:mysql://"+DB_HOST+"/"+DB_NAME+"?autoReconnect=true&useSSL=false";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(path, DynamicVariables.DB_LOGIN,DynamicVariables.DB_PASSWORD);
            //connection = DriverManager.getConnection(path, DB_USER,DB_PASSWORD);
        } catch (Exception e) {
            ConsoleOutput.print("Couldn't connect to database!");
            e.printStackTrace();
        }

        return connection;
    }
}
