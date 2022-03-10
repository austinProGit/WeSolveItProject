import java.util.Scanner;
import java.util.ArrayList;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.io.EOFException;
import java.io.IOException;

/**
 * The VaccineDriver displays all program commands, accepts user input, saves and loads the state of the program, and stores a register with which it interfaces.
 * @author Thomas Merino, Austin lee, and Nam Luu.
 * @version 0.1 (last modified 5/1/21).
 */
public class VaccineDriver{

  /** The filename of the save state file. */
  private static final String REGISTER_FILE_NAME = "ProgramData.bin";

  /** The standard scanner instance for a driver. */
  private InputScanner stdScanner;

  /** The model (important data) of the program, storing the patients, doctors, weighting system, scheduling data, etc.. */
  private Register register;


  /**
   * The sole constructor.
   */
  public VaccineDriver() {
    stdScanner = new InputScanner(System.in);
    register = new Register();
  }


  /**
   * Save the current state of the program by writing a the Register instance to disk.
   * @param filename the destination of the write.
   */
  public void saveState(String filename) {

    // Create an object output stream reference
    ObjectOutputStream writer = null;

    try {
      // Set up the output stream and attempt to save the register
      writer = new ObjectOutputStream(new FileOutputStream(filename));
      writer.writeObject(register);

    } catch (IOException e) {
      // An error was encountered saving the file (report to the user)
      System.out.println("Error: there was a file error encountered while saving data.");

    } finally {
      if (writer != null) {
        try {
          // Attempt to close the writer
          writer.close();

        } catch (IOException e) {
          // An error was encountered while closing the writer (report to the user)
          System.out.println("Error: there was a file error encountered while closing save data.");
        }
      }
    }
  }

  /**
   * Get the saved state of the program from disk (via a Register instance).
   * @param filename the location data will be read from.
   * @throws FatalError if there is an IO exception throw while reading or closing save state data.
   */
  public void loadState(String filename) {

    // Create an object input stream reference
    ObjectInputStream reader = null;

    try {
      FileInputStream file = new FileInputStream(filename);
      reader = new ObjectInputStream(file);
      register = (Register) reader.readObject();

    } catch (ClassNotFoundException e) {
      // An error was encountered while reading  (report to the user and throw a fatal error)
      System.out.println("Error: there was a formatting error encountered while loading data.");
      throw new FatalError("ClassNotFoundException: " + e.getMessage());

    } catch (EOFException e) {
      // The end of the file was reached (ignore this)

    } catch (IOException e) {
      // An error was encountered while reading  (report to the user and throw a fatal error)
      System.out.println("Error: there was a file error encountered while loading data.");
      
      System.out.println("All previous patient data (if any) will be overriden if you continue. Would you like to exit the program?");
      if (stdScanner.getBoolean()) {
        throw new FatalError("IOException: " + e.getMessage());
      }

      System.out.println(); // spacer

    } finally {

      if (reader != null) {
        try {
          // Attempt to close the reader
          reader.close();

        } catch (IOException e) {
          // An error was encountered while closing the reader (report to the user and throw a fatal error)
          System.out.println("Error: there was a file error encountered while closing the save state file.");
          throw new FatalError("IOException: " + e.getMessage());
        }
      }
    }
  }

  /**
   * Present the user with the available main menu commands.
   */
  private void presentMenu() {
    System.out.println("Main Menu:\n" +
                       " 1) Display available doses and questionnaire weighting\n" +
                       " 2) Set available doses for day\n" +
                       " 3) Set questionnaire response weighting\n" +
                       " 4) Add a patient\n" + 
                       " 5) Search for a patient\n" +
                       " 6) Clear a patient\n" +
                       " 7) Add a doctor\n" +
                       " 8) Search for a doctor \n" +
                       " 9) Remove a doctor\n" +
                       "10) Generate schedule\n" +
                       "11) Display current schedule\n" +
                       "12) Reset system\n" +
                       "13) Quit");
  }

  /**
   * Display the available doses per day and the questionnaire weighting system as set by the user.
   */
  private void displayAvailableDosesAndWeights() {

    // Display the available doses per day

    System.out.println("\nAvailable Doses:");

    for (int dayIndex = 1; dayIndex <= 7; dayIndex++) {
      System.out.println(BasicFormatter.getDayOfWeekName(dayIndex) + " : " + register.getDailyDoses(dayIndex));
    }
    
    // Display the questionnaire weighting system

    System.out.println("\nWeighting:");

    // Iterate over age weighting
    for (int decadeAgeStart = 0; decadeAgeStart <= 7; decadeAgeStart++) {
      System.out.println("Age in " + decadeAgeStart + "0s : " + register.getAgeWeight(decadeAgeStart));
    }
    System.out.println("Age in 80s+ : " + register.getAgeWeight(8));

    // Iterate over question-response weighting via key-weight pairings
    for (var pairing : register.getPositiveResponseWeights().entrySet()) {
      int weight = pairing.getValue();
      if (0 <= weight) {
        System.out.println(pairing.getKey() + " : " + weight + " for yes");
      } else {
        System.out.println(pairing.getKey() + " : " + (-weight) + " for no");
      }
    }

    System.out.println(); // spacer
  }

  /**
   * Set the number of doses that can be administered at max for a specified day (get parameters via user input).
   */
  private void setDosesForDay() {

    System.out.println("\nHow many vaccines at max are able to be administered per day (use 0 to indicate no doses are given that day)?\n");

    // Iterate over each day of the week
    for (int dayIndex = 1; dayIndex <= 7; dayIndex++) {

      // Get the number of doses the user expects to be able to administer on the specified day (print the current value as well)
      System.out.println("How many vaccines can be administered on " + BasicFormatter.getDayOfWeekName(dayIndex) + " (currently set to " + register.getDailyDoses(dayIndex) + "):");
      int dosesToSet = stdScanner.getIntWithMinimum(0);

      // Update the register with the input
      register.setDailyDoses(dayIndex, dosesToSet);

      System.out.println(); // Spacer
    }

  }

  
  /**
   * Set the weighting system for questionnaire (in the register field) responses via user input.
   */
  private void setWeightsForResponses() {

    System.out.println("\nSetting the weighting points will require that all points are set, patient priorities will be recompiled, and a new schedule will have to be generated to apply the new weighting. Would you like to continue?");

    // Ensure the user wants to change the weighting
    if (stdScanner.getBoolean()) {

      // Get the weighting for those under 10
      System.out.println("What is the new number of points for those whose age is less than 10?");
      register.setAgeRangeWeight(0, stdScanner.getIntWithMinimum(0));

      // Get the weighting for those in the range between 10 and 80
      for (int decadeAgeStart = 1; decadeAgeStart <= 7; decadeAgeStart++) {
        System.out.println("\nWhat is the new number of points for those whose age is in the " + decadeAgeStart + "0s?");
        register.setAgeRangeWeight(decadeAgeStart, stdScanner.getIntWithMinimum(0));
      }

      // Get the weighting for those at or over 80
      System.out.println("\nWhat is the new number of points for those whose age is or is greater than 80?");
      register.setAgeRangeWeight(8, stdScanner.getIntWithMinimum(0));
      System.out.println(); // spacer

      // Get the weighting for the questionnaire responses (via a Form method)
      register.setPositiveResponseWeights(Form.getResponseWeightsByForm(register.getPositiveResponseWeights(), stdScanner));

      // Resquest that the register recompile the ordering if there are patients in the system
      if (register.hasPatients()) {
        register.recompilePatients();
      }
    }

    System.out.println(); // spacer
  }

  /**
   * Present a form to generate a new patient and add that patient to the register's listing.
   */
  private void addPatient() {

    System.out.println("\nPlease fill out the following form:\n");

    // Create the new patient from a form filled out by user input (or null if the patient does not qualify)
    Patient newPatient = Form.getPatientByForm(stdScanner);

    // Add the new patient to the register if not null
    if (newPatient != null) {
      register.addPatient(newPatient);

      // Save the state and present the new patient's ID
      saveState(REGISTER_FILE_NAME);
      System.out.println("You have been added to the register.\nYour patient ID number is " + newPatient.getPatientID() + ".\nThank you.\n");
    }
  }

  /**
   * Find and display a patient or doctor in the register by a user-entered name.
   * @param isPatient whether to check in the patient listing (if false, check the doctor listing)
   */
  private void searchInRegisterByName(boolean isPatient) {
    // Get the name to search for
    System.out.println("What is the name to search for?");
    String name = stdScanner.getLine();

    // Get the search results from the appropriate listing
    var searchResults = isPatient ? register.getPatientsByName(name) : register.getDoctorsByName(name);

    // Check if any results were found
    if (searchResults.size() == 0) {
      System.out.println("Sorry, no one matching that name was found.");

    } else {
      // Display all results
      System.out.println("Results:");
      for (Person person : searchResults) {
        System.out.println(person);
      }
    }
  }

  /**
   * Find and display a patient or doctor in the register by a user-entered ID.
   * @param isPatient whether to check in the patient listing (if false, check the doctor listing)
   */
  private void searchInRegisterByID(boolean isPatient) {
    // Get the ID to search for
    System.out.println("What is the ID to search for?");
    int id = stdScanner.getIntWithMinimum(0);

    // Get the search results from the appropriate listing
    Person searchResult = isPatient ? register.getPatientByID(id) : register.getDoctorByID(id);

    // Check if any results were found
    if (searchResult == null) {
      System.out.println("Sorry, no one matching that id was found.");

    } else {
      // Display all results
      System.out.println("Results:");
      System.out.println(searchResult);
    }
  }

  /**
   * Display all patients or doctors in the register.
   * @param isPatient whether to check in the patient listing (if false, check the doctor listing)
   */
  private void listAllInRegister(boolean isPatient) {
    // Get the appropriate listing
    var listing = isPatient ? register.getPatientsList() : register.getDoctorsList();

    // Check if there is anyone to list
    if (listing.size() == 0) {
      System.out.println("Sorry, there is no one to list.");

    } else {
      // Display the listing
      System.out.println("Listing:");
      for (Person person : listing) {
        System.out.println(person);
      }
    }
  }

  /**
   * Provide an interface for the user to find and display a patient or doctor in the register
   * @param isPatient whether to check in the patient listing (if false, check the doctor listing)
   */
  private void searchInRegister(boolean isPatient) {

    // Get the type of search
    System.out.println("\nWould you like to search by name (1), search by ID (2), or list all (3)?");
    int choice = stdScanner.getIntInRange(1, 3);

    // Switch over the choice 
    switch (choice) {
      case 1: // search by name
        searchInRegisterByName(isPatient);
        break;
      
      case 2: // search by ID
        searchInRegisterByID(isPatient);
        break;

      case 3: // display all in the listing
        listAllInRegister(isPatient);
        break;
    }
    
    System.out.println(); // spacer
  }

  /**
   * Clear a patient (remove them from the register) by their user-specified ID.
   */
  private void clearPatient() {

    // Get the ID of the patient to clear
    System.out.println("\nWhat is the ID of the patient to clear?");
    int id = stdScanner.getIntWithMinimum(0);

    // Search for a patient matching the user-specified ID
    Patient patientToRemove = register.getPatientByID(id);

    // Check if anyone was found
    if (patientToRemove == null) {
      System.out.println("Sorry, no one matching that id was found.\n");

    } else {
      // Remove the patient from the register and print a confirmation
      register.removePatient(patientToRemove);
      System.out.println(patientToRemove.getName() + " has been cleared.\n");
    }

  }

  /**
   * Present a form to generate a new doctor and add that doctor to the register's listing.
   */
  private void addDoctor() {

    System.out.println("\nPlease fill out the following form:\n");

    // Create the new doctor from a form filled out by user input and add that doctor to the register
    Doctor newDoctor = Form.getDoctorByForm(stdScanner);
    register.addDoctor(newDoctor);

    // Save the state and present the new doctor's ID
    saveState(REGISTER_FILE_NAME);
    System.out.println("You have been added to the register.\nYour doctor ID number is " + newDoctor.getDoctorID() + ".\nThank you.\n");
  }


  /**
   * Remove a doctor from the register by their user-specified ID.
   */
  private void removeDoctor() {

    // Get the ID of the doctor to remove
    System.out.println("\nWhat is the ID of the doctor to remove?");
    int id = stdScanner.getIntWithMinimum(0);

    // Search for a doctor matching the user-specified ID
    Doctor doctorToRemove = register.getDoctorByID(id);

    // Check if anyone was found
    if (doctorToRemove == null) {
      System.out.println("Sorry, no one matching that id was found.\n");

    } else {
      // Remove the doctor from the register and print a confirmation
      register.removeDoctor(doctorToRemove);
      System.out.println(doctorToRemove.getName() + " has been removed from the listing.\n");
    }

  }

  /**
   * Generate and print a schedule based upon the register's data, and then save the schedule to the register for later retrieval.
   */
  private void generateSchedule() {

    // Ensure the register can produce a schedule (if it can, control will be handed to the final else block)
    if (!register.hasPatients()) {
      System.out.println("\nSorry, there are no patients in the system.\n");

    } else if (!register.hasDoctors()) {
      System.out.println("\nSorry, there are no doctors in the system.\n");

    } else if (!register.canAdminister()) {
      System.out.println("\nSorry, no doctors can meet on days where vaccines are administered.\n");

    } else {

      // Get the starting day of the week for the schedule
      System.out.println("\nWhich day would you like to start from?");
      int startingDayIndex = stdScanner.getWeekIndex();

      // Get the number of work days out to generate the schedule
      System.out.println("How many days of administering would you like to schedule out to?");
      int numberOfDays = stdScanner.getIntWithMinimum(1);
      
      // Generate, save, and present the schedule
      String schedule = register.generateScheduleString(startingDayIndex, numberOfDays);
      System.out.println("\n" + schedule);
    }
  }

  /**
   * Reset the entire register (listings, weighting system, and scheduling) after user confirmation.
   */
  private void reset() {

    // Confirm the reset with the user
    System.out.println("\nAre you sure you want to reset the entire register (listings, weighting system, and scheduling)?");
    if (stdScanner.getBoolean()) {

      // Instantiate a new register and save to disk
      register = new Register();
      saveState(REGISTER_FILE_NAME);

      System.out.println("\nRegister reset.");
    }
    System.out.println(); // spacer
  }

  /**
   * Present and process everything from the program menu (main function of the program).
   */
  public void run() {
    
    System.out.println("Welcome! This is the Vaccine Scheduler.\n" +
                       "Below you'll find the list of available options.");

    boolean isRunning = true;

    try {

      // Load the last state
      loadState(REGISTER_FILE_NAME);

      while(isRunning){

        // Present the available commands
        presentMenu();

        // Get the user-selected command
        int userInput = stdScanner.getCommand(13);
      
        // Switch over the command and execute it
        switch (userInput) {
          case 1:
            // Display available doses and questionnaire weighting
            displayAvailableDosesAndWeights();
            break;

          case 2:
            // Set available dosses for day
            setDosesForDay();
            saveState(REGISTER_FILE_NAME);
            break;

          case 3:
            // Set questionnaire response weighting
            setWeightsForResponses();
            saveState(REGISTER_FILE_NAME);
            break;

          case 4:
            // Add a patient
            addPatient();
            break;

          case 5:
            // Search for a patient
            searchInRegister(true);
            break;

          case 6:
            // Clear a patient
            clearPatient();
            saveState(REGISTER_FILE_NAME);
            break;

          case 7:
            // Add a doctor
            addDoctor();
            break;

          case 8:
            // Search for a doctor
            searchInRegister(false);
            break;

          case 9:
            // Remove a doctor
            removeDoctor();
            saveState(REGISTER_FILE_NAME);
            break;

          case 10:
            // Generate schedule
            generateSchedule();
            break;

          case 11:
            // Display current schedule
            System.out.println("\n" + register.getCurrentScheduleString());
            break;

          case 12:
            // Reset system
            reset();
            break;

          case 13:
            // Quit
            saveState(REGISTER_FILE_NAME);
            isRunning = false;
            break;
        }
      }
    } catch (FatalError e) {
      // A fatal error was encountered during the execution of the program (exit the run method)
      System.out.println("A fatal error was encountered, so the program will now terminate...");
    }
  }

  public static void main(String[] args) {
    
    // Create a driver instance and have it run
    VaccineDriver driver = new VaccineDriver();
    driver.run();
      
  }
}