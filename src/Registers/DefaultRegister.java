package Registers;

import Actions.SmsOutPut;
import ApplicationUsers.Administrator;
import DatabaseConnection.DatabaseConnectionConfiguration;
import HelpClasses.ConsoleOutput;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * <h2>Created by Marius Baltramaitis on 07/04/2017.</h2>
 *
 * <p>Default register to handle data from database</p>
 */
public class DefaultRegister {

    public Consumer<String> updateTerminalFunction;

    private String loginDetails;

    /**
     * Constructor taking necessary parameters
     * @param updateTerminalFunction function used to update terminal
     */
    public DefaultRegister(Consumer<String> updateTerminalFunction)
    {
        this.updateTerminalFunction = updateTerminalFunction;
    }


    /**
     * Method to disable user
     * @param login login of user
     * @param userType type of user
     */
    public void disable(String login, String userType)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        connection = DatabaseConnectionConfiguration.getConnection();

        try {

            switch(userType)
            {
                case "Admin" :
                    preparedStatement = connection.prepareStatement("Update Administrator set available = 0 where login  = ?");
                    break;

                case "Pharmacy" :
                    preparedStatement = connection.prepareStatement("Update Pharmacy set available = 0 where login  = ?");
                    break;

                case "Patient":
                    preparedStatement = connection.prepareStatement("Update Patient set available = 0 where login  = ?");
                    break;

                case "Doctor" :
                    preparedStatement = connection.prepareStatement("Update Doctor set available = 0 where login  = ?");
                    break;

                default :
                    return;

            }

            preparedStatement.setString(1,login);

            preparedStatement.executeUpdate();

            updateTerminalFunction.accept("Disabling " + userType+ " " + login);
            DeviceReferenceRegister.kickByLoginAndType(login,userType);

        }


        catch (SQLException sqlException)
        {
            ConsoleOutput.print(getClass().getName(),"Couldn't make prepared statement");
        }
        finally {

            if(preparedStatement != null)
                try {preparedStatement.close();} catch (SQLException s) {}

            if(connection != null)
                try {connection.close();} catch (SQLException s) {}

        }


    }


    /**
     * Method to insert Administrator to database.
     * @param administrator Administrator object with data inside
     * @param phoneNumber phone number where login credentials should be delivered. If phone number is null, login credentials will be printed in terminal
     * @see Administrator
     */
    public void insertAdmin(Administrator administrator,String phoneNumber)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement idStatement = null;
        PreparedStatement loginStatement = null;
        ResultSet resultSet = null;
        int ID = 0;


        try {
            updateTerminalFunction.accept("AdministratorRegister: trying to insert administrator to database");
            ConsoleOutput.print("AdministratorRegister: trying to insert administrator to database");
            connection = DatabaseConnectionConfiguration.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO Administrator (firstName,lastName,sex,additionalInfo,salt,password,primaryPassword)" +
                    "VALUES (?, ?, ?, ?, ?,?,?)");
            preparedStatement.setString(1, administrator.getFirstName());
            preparedStatement.setString(2, administrator.getLastName());
            preparedStatement.setString(3, administrator.getSex());
            preparedStatement.setString(4, administrator.getAdditionalInfo());
            preparedStatement.setString(5, administrator.getSalt());
            preparedStatement.setString(6,administrator.getEncryptedPassword());
            preparedStatement.setString(7,administrator.getEncryptedPassword());
            preparedStatement.executeUpdate();
            ConsoleOutput.print("AdministratorRegister: SQL statement executed successfully.Updating login details now");
            idStatement = connection.prepareStatement("SELECT LAST_INSERT_ID();");
            resultSet = idStatement.executeQuery();


            while (resultSet.next())
            {
                ID = resultSet.getInt(1);

            }

            String namePart = (administrator.getFirstName().length() < 3) ? administrator.getFirstName() : administrator.getFirstName().substring(0,3);
            String lastNamePart = (administrator.getLastName().length() < 3) ? administrator.getLastName() : administrator.getLastName().substring(0,3);
            String login = (namePart+lastNamePart+ID).toLowerCase();
            resultSet = null;

            loginStatement = connection.prepareStatement("UPDATE Administrator SET login = ? WHERE ID = ?;");
            loginStatement.setString(1,login);
            loginStatement.setInt(2,ID);
            loginStatement.executeUpdate();
            loginDetails = "Login : " + login + "; password = " + administrator.getPassword() + ";";
            if(phoneNumber == null || phoneNumber.isEmpty())
            {
                ConsoleOutput.print("DONE! login details: " + "Login: " + login + " password " + administrator.getPassword());
                updateTerminalFunction.accept("DONE! login details: " + "Login: " + login + " password " + administrator.getPassword());
                return;
            }

            else {
                SmsOutPut smsOutPut = new SmsOutPut(loginDetails,phoneNumber);
                smsOutPut.deliverMessage();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if(loginStatement != null)
                {
                    loginStatement.close();
                    ConsoleOutput.print("Closing login statement");
                }
                if(idStatement != null)
                {
                    idStatement.close();
                    ConsoleOutput.print("Closing id statement");
                }
                if(resultSet != null)
                {
                    resultSet.close();
                    ConsoleOutput.print("Closing resultSet");
                }
                if(preparedStatement != null)
                {
                    preparedStatement.close();
                    ConsoleOutput.print("Closing prepared insert Statement");
                }

                if(connection != null)
                {
                    connection.close();
                    ConsoleOutput.print("Closing database Connection");
                }


            } catch (SQLException e)
            {
                ConsoleOutput.print("Couldn't close sql connection or statements");
            }

        }

    }
}
