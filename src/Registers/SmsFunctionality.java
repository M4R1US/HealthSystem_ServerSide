package Registers;

import Classes.GenericPair;
import HelpClasses.ConsoleOutput;
import SavedVariables.FinalConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * <h2>Created by Marius Baltramaitis on 28/11/2017.</h2>
 *
 * <p>Register of administrator phone numbers and sms message answers </p>
 */
public final class SmsFunctionality {

    // T1 question T2 answer;
    private static  ArrayList<GenericPair<String,String>> questionsAndAnswers = new ArrayList<>();

    // T1 phone number T2 administrator name
    private static ArrayList<GenericPair<String,String>> adminPhoneBook = new ArrayList<>();

    private static File phoneBookFile,questionsAndAnswersFile;

    /**
     * Method to scan administrator phone numbers, sms question and answers and register data to array lists
     */
    public static synchronized void scanInput()
    {
        phoneBookFile = new File(FinalConstants.ADMIN_PHONE_BOOK_FILE_PATH);
        questionsAndAnswersFile = new File(FinalConstants.SMS_QUESTIONS_FILE_PATH);

        try {
            Scanner phoneNumberScanner = new Scanner(phoneBookFile);

            while (phoneNumberScanner.hasNext())
            {
                String line = phoneNumberScanner.nextLine();

                if(line != null &&!line.startsWith("#"))
                {
                  String phoneNumber = line.substring(0,line.indexOf("-"));
                  String name = line.substring(line.indexOf("-")+1,line.length());
                  adminPhoneBook.add(new GenericPair<>(phoneNumber,name));
              }

            }

            phoneNumberScanner.close();
            adminPhoneBook.forEach(pair -> ConsoleOutput.print(pair.getFirst()+ "," + pair.getSecond()));

            Scanner questionsScanner = new Scanner(questionsAndAnswersFile);

            while (questionsScanner.hasNext())
            {
                String line = questionsScanner.nextLine();

                if(line != null && !line.startsWith("#"))
                {
                    String question = line.substring(0,line.indexOf("="));
                    String answer = line.substring(line.indexOf("=")+1,line.length());
                    questionsAndAnswers.add(new GenericPair<>(question,answer));
                }
            }

            questionsScanner.close();


        } catch (IOException e)
        {
            ConsoleOutput.print(SmsFunctionality.class.getName(),"Error on scanning files");
            return;
        }

    }

    /**
     * Method to find answer for question
     * @param question question that is asked by user
     * @return answer to the question if its is found in list, null otherwise
     */
    public static synchronized String askQuestion(String question)
    {
      for(GenericPair<String,String> pair : questionsAndAnswers)
      {
          if(pair.getFirst().equalsIgnoreCase(question))
              return pair.getSecond();
      }

      return null;
    }

    /**
     * Method to check if phone number is belonging to administrator
     * @param phoneNumber phone number to check
     * @return Administrator name if phone number is found in register, null otherwise
     */
    public static synchronized String isAdministrator(String phoneNumber)
    {
        for(GenericPair<String,String> pair : adminPhoneBook)
        {
            if(pair.getFirst().equalsIgnoreCase(phoneNumber))
                return pair.getSecond();
        }

        return null;
    }


    /**
     * Method to register new administrator number and name
     * @param number number to register
     * @param name name to register
     */
    public static synchronized void insertAdminPhoneNumber(String number, String name)
    {
        String foundData = null;

        for(GenericPair<String,String> pair : adminPhoneBook)
        {
            if(pair.getFirst().equalsIgnoreCase(number))
                foundData = pair.getFirst();
        }

        if(foundData == null)
        {
            adminPhoneBook.add(new GenericPair<>(number,name));

            try {

                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FinalConstants.ADMIN_PHONE_BOOK_FILE_PATH),true));
                writer.append(number+"-"+name);
                writer.append(System.lineSeparator());
                writer.close();
            } catch (IOException e) {ConsoleOutput.print(SmsFunctionality.class.getName(),e);}

        }
    }


}
