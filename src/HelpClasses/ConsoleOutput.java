package HelpClasses;

/**
 * <h2>Created by Marius Baltramaitis on 13-Dec-16.</h2>
 * <p>Class with various methods to print out text to OS console</p>
 */


public final class ConsoleOutput {


    /**
     * Prints text with line separator
     * @param message text to print
     */
    public static void print(String message)
    {
        System.out.println(message + System.getProperty("line.separator"));
    }

    /**
     * Prints text with class name and line separator
     * @param classname class name
     * @param message text to print
     */
    public static void print(String classname,String message)
    {
        System.out.print("Class: " + classname + " Message: " + message + System.getProperty("line.separator"));
    }

    /**
     * Prints text with classname,exception message and line separator
     * @param classname name of class
     * @param t exception
     */
    public static void print(String classname, Throwable t)
    {
        System.out.print("Class: " + classname + " threw exception " + t.getMessage() + System.getProperty("line.separator"));
    }

}
