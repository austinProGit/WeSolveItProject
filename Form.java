import java.util.Scanner;
import java.util.TreeMap;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * This class provides static methods for processing form responses. Specifically, the Form class allows the administrator and patient to interface with their relevant forms. Note that the qualification questionnaire and the weighting questionnaire must be in the same directory and named "QualificationQuestionnaire.txt" and "Questionnaire.txt" respectively. The qualification questionnaire must alternate lines with the asserted answer ("NO" or assumedly "YES") and the coordinating question prompt, and the questionnaire must alternate lines with the response key and the coordinating question prompt.
 * @author Thomas Merino, Nam Luu, Austin Lee.
 * @version 1.0 (last modified 5/1/21).
 * @see Questionnaire questions provided by www.cdc.gov/vaccines/covid-19/downloads/pre-vaccination-screening-form.pdf, and comorbidities for covid 19 provided by www.cdc.gov/coronavirus/2019-ncov/need-extra-precautions/people-with-medical-conditions.html#:~:text=Chronic%20lung%20diseases%2C%20including%20COPD,severely%20ill%20from%20COVID%2D19.
 */
public class Form {

  /** The filename of the qualification questionnaire. */
  private static final String QUALIFICATION_QUESTIONNAIRE_FILE_NAME = "QualificationQuestionnaire.txt";

  /** The filename of the questionnaire. */
  private static final String QUESTIONNAIRE_FILE_NAME = "Questionnaire.txt";

  /** Overridden  default constructon, so Form cannot be instantiated */
  private Form() {}

  /**
   * Helper method for closing a buffered reader.
   * @param reader the reader to close.
   */
  private static void closeReader(BufferedReader reader) {

    if (reader != null) {
        try {
          // Attempt to close the reader
          reader.close();

        } catch (IOException e) {
          // A fatal error was thrown (so throw a FatalError)
          System.out.println("Error: a problem was encountered while closing questionnaire data.");
          throw new FatalError("IOException: " + e.getMessage());
        }
      }
  }

  /**
   * Present a qualification questionnaire to ensure the patient can be registered.
   * @param scanner the InputScanner to receive user responses from.
   * @returns whether the user is qualified to join the register.
   * @throws FatalError if there is an IOException throw while reading questionnaire data.
   */
  public static boolean ensureQualificationByForm(InputScanner scanner) {

    // Create a reader to parse the questionnaire
    BufferedReader questionReader = null;

    try {
      // Set up the reader
      questionReader = new BufferedReader(new FileReader(QUALIFICATION_QUESTIONNAIRE_FILE_NAME));
      // Create a variable to track the qualifying user response
      String assertedStringInput = null;

      // Continue to get the next asserted input if not at the end of the file
      while ( (assertedStringInput = questionReader.readLine()) != null) {

        // Present the prompt
        String description = questionReader.readLine();
        System.out.println(description);

        // Get the user response
        boolean responseTruthValue = scanner.getBoolean();

        // Ensure the user response matches the expected response
        if (assertedStringInput.equals("NO") == responseTruthValue ) {
          // The user does not qualify (return false)
          return false;
        }

        System.out.println(); // Spacer

      }

    } catch (IOException e) {
      // A fatal error was thrown (so throw a FatalError)
      System.out.println("Error: a problem was encountered while reading questionnaire data.");
      throw new FatalError("IOException: " + e.getMessage());

    } catch (NullPointerException e) {
      // Note: this catch block will be executed when there are an odd number of lines in the questionnaire file
      System.out.println("Error: there is a problem with the questionnaire formatting. There may have been a lost question.");

    } finally {
      closeReader(questionReader);
    }

    // The user did not enter an invalid response (return true)
    return true;
  }

  /**
   * Present a questionnaire to generate a new patient from user input.
   * @param scanner the InputScanner to receive user responses from.
   * @returns the new patient from the form information or null if the patient does not qualify.
   * @throws FatalError if there is an IOException throw while reading questionnaire data.
   */
  public static Patient getPatientByForm(InputScanner scanner) {

    // Present the qualification questionnaire to ensure the user can be put into the register
    if (!ensureQualificationByForm(scanner)) {
      // The user does not qualify (report and return null)
      System.out.println("Sorry, this means you do not qualify for vaccination at this moment. Thank you.\n");
      return null; 
    }

    // Create a working patient to modify as input is collected (to be returned)
    Patient newPatient = new Patient();

    // Present the general form to collect generic information about the new patient
    processGeneralForm(newPatient, scanner);

    // Collect the birthdate
    System.out.println("What is your date of birth?");
    newPatient.setBirthdate(scanner.getDate());

    System.out.println(); // Spacer

    // Create a reader to parse the questionnaire
    BufferedReader questionReader = null;

    try {
      // Set up the reader
      questionReader = new BufferedReader(new FileReader(QUESTIONNAIRE_FILE_NAME));
      // Create a variable to track the key for storing the user's response
      String responseKey = null;

      // Continue to get the next key if not at the end of the file
      while ( (responseKey = questionReader.readLine()) != null) {

        // Present the prompt
        String description = questionReader.readLine();
        System.out.println(description);

        // Get and process the user response
        boolean responseTruthValue = scanner.getBoolean();
        newPatient.appendMedicalBoolean(responseKey, responseTruthValue);

        System.out.println(); // Spacer

      }

    } catch (IOException e) {
      // A fatal error was thrown (so throw a FatalError)
      System.out.println("Error: a problem was encountered while reading questionnaire data.");
      throw new FatalError("IOException: " + e.getMessage());

    } catch (NullPointerException e) {
      // This catch block will be executed when there are an odd number of lines in the questionnaire file
      System.out.println("Error: there is a problem with the questionnaire formatting. There may have been a lost question.");

    } finally {
      closeReader(questionReader);
    }

    // Return the new patient
    return newPatient;
  }

  /**
   * Present a questionnaire to generate a new doctor from user input.
   * @param scanner the InputScanner to receive user responses from.
   * @returns the new doctor from the form information.
   * @throws FatalError if there is an IOException throw while reading questionnaire data.
   */
  public static Doctor getDoctorByForm(InputScanner scanner) {

    // Create a working doctor to modify as input is collected (to be returned)
    Doctor newDoctor = new Doctor();

    // Present the general form to collect generic information about the new patient
    processGeneralForm(newDoctor, scanner);

    // Collect the doses the doctor will be able to administer for each day of the week (day indices 1 through 7)

    System.out.println("How many vaccines will you be able to administer per day?\n");

    for (int dayIndex = 1; dayIndex <= 7; dayIndex++) {
      System.out.println("For " + BasicFormatter.getDayOfWeekName(dayIndex) + ":");
      newDoctor.setDosesAdministeredPerDay(dayIndex, scanner.getIntWithMinimum(0));
      System.out.println(); // Spacer
    }

    //Return the new doctor
    return newDoctor;
  }


  /**
   * Helper method that presents a form subsection to collect a person's name, phone number, and email address from user input.
   * @param processingPerson the person to which the information will be added.
   * @param scanner the InputScanner to receive user responses from.
   * @throws FatalError if there is an IOException throw while reading questionnaire data.
   */
  private static void processGeneralForm(Person processingPerson, InputScanner scanner) {

    // Collect and set the name
    System.out.println("What is your name?");
    processingPerson.setName(scanner.getLine());

    // Collect and set the phone number
    System.out.println("\nWhat is a good phone number to reach you by (or enter \"N/A\")?");
    processingPerson.setPhone(scanner.getProbablePhoneNumber());

    // Collect and set the email address
    System.out.println("\nWhat is a good email address to reach you by (or enter \"N/A\")?");
    processingPerson.setEmail(scanner.getProbableEmail());

    System.out.println(); // Spacer
  }

  /**
   * Present a form to generate a new weighting system (a mapping of keys to weights) for the questionnaire based on user input.
   * @param oldWeighting the current values in the weighting system (for user reference).
   * @param scanner the InputScanner to receive user responses from.
   * @return the new weighting system from the user input.
   */
  public static TreeMap<String, Integer> getResponseWeightsByForm(TreeMap<String, Integer> oldWeighting, InputScanner scanner) {

    // Create a working mapping to modify as input is collected (to be returned)
    TreeMap<String, Integer> newWeighting = new TreeMap<String, Integer>();

    // Create a reader to parse the questionnaire
    BufferedReader questionReader = null;

    
    try {
      // Set up the reader
      questionReader = new BufferedReader(new FileReader(QUESTIONNAIRE_FILE_NAME));
      // Create a variable to track the key used for mapping weights
      String responseKey = null;

      // Continue to get the next key if not at the end of the file
      while ( (responseKey = questionReader.readLine()) != null) {
        
        // Present the prompt as seen by patients
        String description = questionReader.readLine();
        System.out.println("\"" + description + "\"");

        // Attempt to get the old weight (if it exists)
        Integer oldWeight = oldWeighting.get(responseKey);

        // If there is no weight for the current key, assign the old weight to be 0 (which is how the key is treated during actual sorting)
        if (oldWeight == null) {
          oldWeight = 0;
        }
        
        // Print the affect the current weight has
        if (0 <= oldWeight) {
          System.out.println("\nCurrently, the patient receives " + oldWeight + " weighting points when they response is yes");
        } else {
          System.out.println("\nCurrently, the patient receives " + (-oldWeight) + " weighting points when they response is no");
        }
        
        // Get and set the new weight
        System.out.println("What is the new number of points for this question?");
        int newWeight = scanner.getIntWithMinimum(0);
        
        // Create a variable to track the associated (rewarding) response 
        boolean forResponse = true;

        // If the weight is not 0 (is relevant), get the associated (rewarding) response
        if (newWeight != 0) {
          System.out.println("What is the rewarding reponse?");
          forResponse = scanner.getBoolean(); 
        }

        // Add the weight to the mapping
        newWeighting.put(responseKey, newWeight * (forResponse ? 1 : -1));

        System.out.println(); // Spacer

      }

    } catch (IOException e) {
      // A fatal error was thrown (so throw a FatalError)
      System.out.println("Error: a problem was encountered while reading questionnaire data.");
      throw new FatalError("IOException: " + e.getMessage());

    } catch (NullPointerException e) {
      // This catch block will be executed when there are an odd number of lines in the questionnaire file
      System.out.println("Error: there is a problem with the questionnaire formatting. There may have been a lost question.");

    } finally {
      closeReader(questionReader);
    }

    // Return the new mapping
    return newWeighting;

  }
}

