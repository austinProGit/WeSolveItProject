import java.util.Scanner;
import java.io.InputStream;

import java.time.LocalDate;
import java.time.DateTimeException;

// Note that InputScanner cannot (though, should be) a subclass of the final class Scanner.

/**
 * This class prompts the user for input and processes the user's responses. Moreover, the InputScanner class ensures that the user enters the correct data type/format and continually prompts the user until correct data is entered. 
 * @author Thomas Merino, Nam Luu.
 * @version 1.0 (last modified 5/1/21).
 */
public class InputScanner {

  /** The scanner that performs getting raw input. */
  private Scanner scanner;

  /**
   * Constructor that takes an InputStream.
   * @param source the InputStream the scanner will take input from.
   */
  public InputScanner(InputStream source) {
    this.scanner = new Scanner(source);
  }

  /**
   * Get a valid command (an int) from the user.
   * @param numberOfCommands the maximum value the user may enter.
   * @return the valid user-entered command.
   */
  public int getCommand(int numberOfCommands) {

    // Iterate until a valid input is returned
    while (true) {

      // Prompt and get an input
      System.out.print("Enter a command: ");
      String userInput = scanner.nextLine();

      try {
        // Attempt to convert the line into an integer
        int userIntInput = Integer.parseInt(userInput);
        
        // Ensure the integer is within the proper interval
        if (1 <= userIntInput && userIntInput <= numberOfCommands) {
          // The input is valid (return it)
          return userIntInput;
        } 
      } catch (NumberFormatException e) { /* The integer conversion failed */ }

      // The input was not valid (report to the user)
      System.out.println("That is not a valid command.");

    }
  }

  /**
   * Get a line (string) from the user.
   * @return the valid user-entered string.
   */
  public String getLine() {
    System.out.print("Enter here: ");
    return scanner.nextLine();
  }

  /**
   * Get an integer greater than or equal to a specified minimum from the user.
   * @param minimum the minimum value for accepted input.
   * @return the valid user-entered int. 
   */
  public int getIntWithMinimum(int minimum) {

    // Iterate until a valid input is returned
    while (true) {

      // Prompt and get an input
      System.out.print("Enter a whole number no less than " + minimum + ": ");
      String input = scanner.nextLine();

      try {
        // Attempt to convert the line into an integer
        int intInput = Integer.parseInt(input);
        
        // Ensure the integer is above the minimum
        if (minimum <= intInput) {
          // The input is valid (return it)
          return intInput;
        }

      } catch (NumberFormatException e) { /* The integer conversion failed */ }

      // The input was not valid (report to the user)
      System.out.println("Sorry, that is not a valid number.");

    }
  }

  /**
   * Get an integer between or equal to two numbers from the user.
   * @param minimum the minimum value for accepted input.
   * @return the valid user-entered int.
   * @throws IllegalArgumentException if the bounds are not valid/useful.
   */
  public int getIntInRange(int minimum, int maximum) {

    // Ensure the interval is valid/useful (throw an exception if not)
    if (minimum > maximum) {
      throw new IllegalArgumentException("illegal bounds for range when getting input: [" + minimum + ", " + maximum + "]");
    }

    // Iterate until a valid input is returned
    while (true) {

      // Prompt and get an input
      System.out.print("Enter a whole number from " + minimum + " to " + maximum + ": ");
      String input = scanner.nextLine();

      try {
        // Attempt to convert the line into an integer
        int intInput = Integer.parseInt(input);
        
        // Ensure the integer is within the proper interval
        if (minimum <= intInput && intInput <= maximum) {
          // The input is valid (return it)
          return intInput;
        }
      } catch (NumberFormatException e) { /* The integer conversion failed */ }

      // The input was not valid (report to the user)
      System.out.println("Sorry, that is not a valid number.");

    }
  }

  /**
   * Input method that gets a date in the form mm/dd/yyyy or mm.dd.yyyy.
   * @return the valid user-entered date.
   */
  public LocalDate getDate() {

    // Iterate until a valid input is returned
    while (true) {

      // Prompt and get an input
      System.out.print("Enter a date (MM/DD/YYYY): ");
      String input = scanner.nextLine().trim();

      try {

        // Attempt to convert the line into a date
        if (input.length() == 10 && (input.charAt(2) == '/' || input.charAt(2) == '.') && (input.charAt(5) == '/' || input.charAt(5) == '.')) {
          int month = Integer.parseInt(input.substring(0, 2));
          int day = Integer.parseInt(input.substring(3, 5));
          int year = Integer.parseInt(input.substring(6));
          return LocalDate.of(year, month, day);
        }

      } catch (NumberFormatException e) { // The date conversion failed
      } catch (DateTimeException e) { /* The date conversion failed */ }

      // The input was not valid (report to the user)
      System.out.println("Sorry, that is not a valid date.");
    }
  }

  /**
   * Input method that gets a index for the entered day of the week (where 1 is Sunday and 7 is Saturday).
   * @return the day index (where 1 is Sunday and 7 is Saturday) of the user-entered day.
   */
  public int getWeekIndex() {

    // Iterate until a valid input is returned
    while(true) {

      // Prompt and get an input
      System.out.print("Enter a day of the week: ");
      String input = scanner.nextLine().trim().toLowerCase();

      // Check for a valid responses (a day of the week) and return the appropriate value if the input matches
      if (input.equals("sunday")) {
        return 1;
      } else if (input.equals("monday")) {
        return 2;
      } else if (input.equals("tuesday")) {
        return 3;
      } else if (input.equals("wednesday")) {
        return 4;
      } else if (input.equals("thursday")) {
        return 5;
      } else if (input.equals("friday")) {
        return 6;
      } else if (input.equals("saturday")) {
        return 7;
      }

      // The input was not valid (report to the user)
      System.out.println("Sorry, that is not a valid response.");

    }

  }
  

  /**
   * Input method that gets a probable phone number (a string with at least 7 characters with at least one numerical) or "N/A".
   * @returns the user-entered string (assumed to be a valid phone number or "N/A").
   */
  public String getProbablePhoneNumber() {

    // Iterate until a valid input is returned
    while (true) {

      // Prompt and get an input
      System.out.print("Enter a phone number: ");
      String input = scanner.nextLine().trim();

      // Check if the input is equivalent to "N/A" or is long enough
      if (input.toUpperCase().equals("N/A")) {
        return "N/A";
      } else if (input.length() >= 7) {
        // Return the input if a digit character is found
        for (int index = 0; index < input.length(); index++) {
          if (Character.isDigit(input.charAt(index))) {
            return input;
          }
        }
      }
      // The input was not valid (report to the user)
      System.out.println("Sorry, that does not appear to be a valid phone number.");
    }
  }

  /**
   * Input method that gets a probable email (a string with at least 7 characters, and "@", and a succeeding ".") or "N/A".
   * @return the user-entered string (assumed to be a valid email address or "N/A").
   */
  public String getProbableEmail() {

    // Iterate until a valid input is returned
    while (true) {

      // Prompt and get an input
      System.out.print("Enter an email address: ");
      String input = scanner.nextLine().trim();

      // Get the indices of '@' and '.' in the input (if present)
      int atSymbolIndex = input.indexOf('@');
      int dotSymbolIndex = input.indexOf('.');

      // Check if the input is equivalent to "N/A" or is a probable email address
      if (input.toUpperCase().equals("N/A")) {
        return "N/A";

      } else if (input.length() >= 7 && atSymbolIndex != -1 && dotSymbolIndex != -1 && atSymbolIndex < dotSymbolIndex) {
        return input;
      }

      // The input was not valid (report to the user)
      System.out.println("Sorry, that does not appear to be a valid email.");
    }
  }
  
  
  /**
   * Get a yes-or-no response (boolean) from the user.
   * @return the user-entered boolean.
   */
  public boolean getBoolean() {

    // Iterate until a valid input is returned
    while (true) {

      // Prompt and get an input
      System.out.print("Enter \"y\" or \"n\": ");
      String input = scanner.nextLine().trim().toLowerCase();

      // Check for some basic responses and return the appropriate value if the input matches
      if (input.equals("y") || input.equals("y.") || input.equals("yes") || input.equals("yes.") || input.equals("t")) {
        return true;
      } else if (input.equals("n") || input.equals("n.") || input.equals("no") || input.equals("no.") || input.equals("f")) {
        return false;
      }

      // The input was not valid (report to the user)
      System.out.println("Sorry, that is not a valid response.");
    }
  }

}

