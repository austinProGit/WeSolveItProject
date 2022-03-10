import java.util.ArrayList;
import java.util.TreeMap;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.io.Serializable;

/**
 * The Register class serves as the data model for this program and takes/stores the inputs Patients, Doctors, vaccine doses, response weighting, and other relevant information to produce a schedule. 
 * @author Thomas Merion, Austin Lee, Nam Luu.
 * @version 1.0 (last modified 5/2/21).
 */
public class Register implements Serializable {

  /** The serial version used when decoding. */
  private static final long serialVersionUID = 2000L;

  
  /** The next patient ID to assign. */
  private int currentPatientID;
  /** The next doctor ID to assign. */
  private int currentDoctorID;


  // Note the listings are stored as ordered lists, meaning that manual patient and doctor ordering can be implemented later

  /** The ordered listing of all patients in the system. */
  private ArrayList<Patient> patientListing;
  /** The ordered listing of all doctors in the system. */
  private ArrayList<Doctor> doctorListing;

  /** The mapping of the day index (where Sunday is 1 and Saturday is 7) to the number of doses available for that day. */
  private int[] dosesPerDay;

  /** The mapping of the decadess old someone is to the weighting points granted for their age (where the maximum partition is for those 80 and up). */
  private int[] ageRangeWeights;
  /** The mapping of the keys (questionnaire keys) to the weights for a positive response (a negative weight functionally means additional weighting points for a negative response). */
  private TreeMap<String, Integer> positiveResponseWeights;
  
  

  /** The string that contains the last schedule generated. */
  private String currentSchedule;

  /**
   * The sole constructor, which also provides default values for the weighting system.
   */
  public Register() {

    // Initialize all fields

    currentPatientID = 1000; // All patient IDs will be larger than or equal to 1000 (at least four digits)
    currentDoctorID = 9000; // All doctor IDs will be larger than or equal to 9000 (at least four digits)

    patientListing = new ArrayList<Patient>();
    doctorListing = new ArrayList<Doctor>();

    dosesPerDay = new int[7];

    ageRangeWeights = new int[]{0, 0, 0, 0, 5, 10, 30, 55, 60}; // Provide default weighting
    positiveResponseWeights = new TreeMap<String, Integer>();
    
    currentSchedule = "--No Schedule--\n";


    // Provide default weighting
    positiveResponseWeights.put("High risk employment", 3);
    positiveResponseWeights.put("Asthma", 8);
    positiveResponseWeights.put("COPD", 6);
    positiveResponseWeights.put("Scarred Lung Tissue", 8);
    positiveResponseWeights.put("Smoking", 6);

  }

  /**
   * Static method for quickly generating low-detail Register instances for testing.
   * Note that ageWeighting, dosesPerDay, and the second level of doctorParameters must have a certain length. Meanwhile, weighting and the second level of patientParameters must be as long as the questionnaire. patientAges and patientParameters must have the same number of elements (to be the number of patients), and the length of doctorParameters will determine the number of doctors.
   * This method will generate new Patient and Doctor instances for the register, with each of these instances named p### for patients and d### for doctors (where ### is the unique ascending number associated with that instance).
   * @param ageWeighting the weightings for the different age ranges (must be a 9-element array).
   * @param weighting the weightings for the questionnaire responses (must be as long as the questionnaire).
   * @param dosesPerDay the number of doses available for each day of the week from 1, which is Sunday, to 7, which is Saturday (must be a 7-element array).
   * @param patientAges the ages in years of the patients to add (must be as long as patientParameters).
   * @param patientParameters the questionnaire responses of the patients to add (must be as long as patientAges with second-level arrays being as long as the questionnaire).
   * @param doctorParameters the number of doses each doctor can administer for each day of the week (must have 7-element second-level arrays).
   * @return the fully populated, low-detail register.
   */
  public static Register generateTestRegister(int[] ageWeighting, int[] weighting, int[] dosesPerDay, int[] patientAges, boolean[][] patientParameters, int[][] doctorParameters) {

    // Instantiate a working register
    Register testRegister = new Register();

    // Set the weighting system 

    for (int ageIndex = 0; ageIndex < ageWeighting.length; ageIndex++) {
      testRegister.setAgeRangeWeight(ageIndex, ageWeighting[ageIndex]);
    }
    for (int weightIndex = 0; weightIndex < weighting.length; weightIndex++) {
      testRegister.positiveResponseWeights.put("w" + weightIndex, weighting[weightIndex]);
    }

    // Set the available doses per day
    for (int dosesPerDayIndex = 0; dosesPerDayIndex < 7; dosesPerDayIndex++) {
      testRegister.setDailyDoses(dosesPerDayIndex + 1, dosesPerDay[dosesPerDayIndex]);
    }

    // Create and add the patients
    for (int patientIndex = 0; patientIndex < patientParameters.length; patientIndex++) {

      // Create the new patient with a name of the form p### (where ### coordinates with patientIndex) and an appropriate birthdate for the specified age
      Patient newPatient = new Patient();
      newPatient.setName("p" + BasicFormatter.getThreeDigitString(patientIndex));
      newPatient.setBirthdate(LocalDate.now().minusYears(patientAges[patientIndex]));

      // Add the patient's questionnaire responses
      boolean[] newPatientParameters = patientParameters[patientIndex];
      for (int parameterIndex = 0; parameterIndex < newPatientParameters.length; parameterIndex++) {
        newPatient.appendMedicalBoolean("w" + parameterIndex, newPatientParameters[parameterIndex]);
      }

      // Add the new patient to the working register
      testRegister.addPatient(newPatient);
    }

    // Create and add the doctors
    for (int doctorIndex = 0; doctorIndex < doctorParameters.length; doctorIndex++) {

      // Create the new patient with a name of the form d### (where ### coordinates with doctorIndex)
      Doctor newDoctor = new Doctor();
      newDoctor.setName("d" + BasicFormatter.getThreeDigitString(doctorIndex));

      // Add the doctor's available dose administering per day
      int[] newDoctorParameters = doctorParameters[doctorIndex];
      for (int parameterIndex = 0; parameterIndex < newDoctorParameters.length; parameterIndex++) {
        newDoctor.setDosesAdministeredPerDay(parameterIndex+1, newDoctorParameters[parameterIndex]);
      }

      // Add the new doctor to the working register
      testRegister.addDoctor(newDoctor);
    }

    return testRegister;
  }

  /**
   * Generate a register schedule with given parameters and assert that it coordinates with the provided regular expression. Please note that this method uses assert statements for testing.
   * @param testName the name of the test and what will be reported if the assertion is not satified.
   * @param testRegister the register to generate the schedule from.
   * @param startingDayIndex the day-of-the-week index to start the schedule from.
   * @param numberOfDays the number of days to generate the schedule for.
   * @param expectedRegularExpression the regular expression to assert matches up with the schedule.
   */
  public static void testRegisterSchedule(String testName, Register testRegister, int startingDayIndex, int numberOfDays, String expectedRegularExpression) {

    // Get the schedule to compare
    String testSchedule = testRegister.generateScheduleString(startingDayIndex, numberOfDays);
    // Check the schedule with newlines removed against the passed regular expression
    assert testSchedule.replace('\n', ' ').matches(expectedRegularExpression) : "Error in test " + testName + ": expected to match with:\n" + expectedRegularExpression + "\nGot schedule:\n" + testSchedule;

  }

  /**
   * Get the next available patient ID, which also increases the next available patient ID for the next call.
   * @return the available patient ID.
   */
  private int getAndUpdatePatientID() {
    return currentPatientID++;
  }

  /**
   * Get the next available doctor ID, which also increases the next available doctor ID for the next call.
   * @return the available doctor ID.
   */
  private int getAndUpdateDoctorID() {
    return currentDoctorID++;
  }

  
  /**
   * Add a patient to the listing, ensuring the higher priority patients are listed first (and those who entered first come before those with equal priority).
   * @param newPatient the new patient to append.
   */
  public void addPatient(Patient newPatient) {
    
    // Get the age of the new patient in whole decades (for example, a nine year old is 0 decades old)
    int patientAgeInDecades = (int) newPatient.getBirthdate().until(LocalDate.now(), ChronoUnit.DECADES);

    // Create a variable to track the running weight (set initially to the weighting points awarded based on age)
    int calculatedWeight = ageRangeWeights[Math.max(0, Math.min(patientAgeInDecades, 8))];

    // Add weighting points for the questionnaire responses
    for (var pairing : positiveResponseWeights.entrySet()) {

      Boolean patientBoolean = newPatient.getMedicalBoolean(pairing.getKey());

      // Add points to the weight if the patient answered yes (questions that award upon answering no simply assign negative points for yes responses)
      if (patientBoolean != null && patientBoolean == true) {
        calculatedWeight += pairing.getValue();
      }
    }

    // Set the patients priority weight and unique ID for this register
    newPatient.setPriorityWeight(calculatedWeight);
    newPatient.setPatientID(getAndUpdatePatientID());
    

    // Use a simple linear search to determine the new index of the new patient (so that it is sorted)
    // Note that the a binary search could also be implemented here if desired and if manual ordering (with flagged patients) is not implemented
    int index;

    // Iterate until the proper insertion index is found
    for (index = 0; index < patientListing.size(); index++) {

      Patient comparisonPatient = patientListing.get(index);

      // Leave the index search when the patient lies at the correct index
      if (newPatient.compareTo(comparisonPatient) < 0) {
        break;
      }
    }

    // Append the patient at the proper index
    patientListing.add(index, newPatient);
  }

  /**
   * Remove the passed patient from the listing if present (return false if not present).
   * @param patient the patient to remove.
   * @return true if the patient was found and removed or false otherwise.
   */
  public boolean removePatient(Patient patient) {
    return patientListing.remove(patient);
  }

  /**
   * Check if there are any patients in the listing.
   * @return whether there are patients in the listing or not (a boolean).
   */
  public boolean hasPatients() {
    return !patientListing.isEmpty();
  }

  /**
   * Get all patients in the listing that contain a given name (a pattern) in their name. 
   * @param name the name/pattern to search for.
   * @return the list of the search results.
   */
  public ArrayList<Patient> getPatientsByName(String name) {

    // Create a working result list
    ArrayList<Patient> searchResult = new ArrayList<Patient>();

    // Iterate over each patient and append one to the result if they contain the name
    for (Patient patient : patientListing) {
      if (patient.getName().contains(name)) {
        searchResult.add(patient);
      }
    }

    return searchResult;
  }

  /**
   * Get the patient in the listing that has a given patient ID or, if no matching patient is found, null.
   * @param patientID the patient ID to search for.
   * @return the patient with the specified patient ID or null.
   */
  public Patient getPatientByID(int patientID) {

    // Search for and return the patient with the matching ID
    for (Patient patient : patientListing) {
      if (patient.getPatientID() == patientID) {
        return patient;
      }
    }

    // No match was found (return null)
    return null;
  }

  /**
   * Get the entire patient listing (it is not recommended that the returned list be modified).
   * @return the patient listing.
   */
  public ArrayList<Patient> getPatientsList() {
    return patientListing;
  }

  /**
   * Re-sort the patient listing by re-adding each patient back into the listing.
   */
  public void recompilePatients() {

    // Make a reference to the old listing
    ArrayList<Patient> oldPatientListing = patientListing;

    // Clear the current listing
    patientListing = new ArrayList<Patient>();

    // Re-add each patient from the old listing
    for (Patient patient : oldPatientListing) {
      addPatient(patient);
    }

  }


  
  /**
   * Add a doctor to the listing, where the newest doctor is stored at the end of the list.
   * @param newDoctor the new doctor to append.
   */
  public void addDoctor(Doctor newDoctor) {
    newDoctor.setDoctorID(getAndUpdateDoctorID());
    doctorListing.add(newDoctor);
  }

  /**
   * Remove the passed doctor from the listing if present (return false if not present).
   * @param doctor the doctor to remove.
   * @return true if the doctor was found and removed or false otherwise.
   */
  public boolean removeDoctor(Doctor doctor) {
    return doctorListing.remove(doctor);
  }

  /**
   * Check if there are any doctors in the listing.
   * @return whether there are doctors in the listing or not (a boolean).
   */
  public boolean hasDoctors() {
    return !doctorListing.isEmpty();
  }

  /**
   * Get all doctors in the listing that contain a given name (a pattern) in their name.
   * @param name the name/pattern to search for.
   * @return the list of the search results.
   */
  public ArrayList<Doctor> getDoctorsByName(String name) {

    // Create a working result list
    ArrayList<Doctor> searchResult = new ArrayList<Doctor>();

    // Iterate over each doctor and append one to the result if they contain the name
    for (Doctor doctor : doctorListing) {
      if (doctor.getName().contains(name)) {
        searchResult.add(doctor);
      }
    }

    return searchResult;
  }

  /**
   * Get the doctor in the listing that has a given doctor ID or, if no matching patient is found, null.
   * @param doctorID the doctor ID to search for.
   * @return the doctor with the specified doctor ID or null.
   */
  public Doctor getDoctorByID(int doctorID) {

    // Search for and return the patient with the matching ID
    for (Doctor doctor : doctorListing) {
      if (doctor.getDoctorID() == doctorID) {
        return doctor;
      }
    }

    // No match was found (return null)
    return null;
  }

  /**
   * Get the entire doctor listing (it is not recommended that the returned list be modified).
   * @return the doctor listing.
   */
  public ArrayList<Doctor> getDoctorsList() {
    return doctorListing;
  }



  /**
   * Set the doses available for the specified day.
   * @param dayNumber the day to set (this must be 1 through 7 inclusive where 1 is Sunday and 7 is Saturday).
   * @param numberOfDoses the number of doses available on the specified day.
   */
  public void setDailyDoses(int dayNumber, int numberOfDoses) {
    dosesPerDay[dayNumber - 1] = numberOfDoses;
  }

  /**
   * Get the doses available for the specified day.
   * @param dayNumber the day to check (this must be 1 through 7 inclusive where 1 is Sunday and 7 is Saturday).
   * @return the number of doses on the specified day.
   */
  public int getDailyDoses(int dayNumber) {
    return dosesPerDay[dayNumber - 1];
  }

  /**
   * Set the priority weight associated with lying in a given age range (by decade).
   * @param decade the age range to set (this must be 0 through 8 inclusive where 0 is [0,10), 7 is [70, 80), and 8 is [80, inf)).
   * @param newWeight the weight to set the age range to.
   */
  public void setAgeRangeWeight(int decade, int newWeight) {
    ageRangeWeights[decade] = newWeight;
  }

  /**
   * Get the priority weight associated with lying in a given age range (by decade).
   * @param decade the age range to check (this must be 0 through 8 inclusive where 0 is [0,10), 7 is [70, 80), and 8 is [80, inf)).
   * @return the weight to associated with the specified age range.
   */
  public int getAgeWeight(int forDecade) {
    return ageRangeWeights[forDecade];
  }

  /**
   * Setter for the entire questionnaire-response weighting system.
   * @param newWeighting the new value of the weighting system.
   */
  public void setPositiveResponseWeights(TreeMap<String, Integer> newWeighting) {
    positiveResponseWeights = newWeighting;
  }

  /**
   * Getter for the entire questionnaire-response weighting system.
   * @return the response weighting system.
   */
  public TreeMap<String, Integer> getPositiveResponseWeights() {
    return positiveResponseWeights;
  }

  /**
   * Get the maximum doses that can be administered (the lesser between the maximum the doctors can administer and the available doses) for a given day of the week.
   * @param dayIndex the day to check (must be 1 through 7 inclusive where 1 is Sunday and 7 is Saturday)
   * @return the maximum number of doses that can be administered on the specified day.
   */
  public int maximumDosesForDay(int dayIndex) {

    // Calculate the sum of the doses that all doctors can administer in the day
    int maximumDoctorDoses = 0;
    for (Doctor doctor : doctorListing) {
      maximumDoctorDoses += doctor.getDosesAdministeredPerDay(dayIndex);
    }

    // Get the number of vaccines available on the day
    int availableVaccines = dosesPerDay[dayIndex - 1];
    
    // Return the lesser of the two (the maximum that can be done)
    return Math.min(availableVaccines, maximumDoctorDoses);
  }


  /**
   * Determine whether any doses can be administered with the current state, requiring only one day of the week to be able administer doses (excluding patient availability).
   * @return the whether doses can be administered or not (a boolean).
   */
  public boolean canAdminister() {

    // Return true if any day allows for some doses to be administered 
    for (int dayIndex = 1; dayIndex <= 7; dayIndex++) {
      if (maximumDosesForDay(dayIndex) != 0) {
        return true;
      }
    }
    
    // No valid days were found (return false)
    return false;
  }

  /**
   * Helper method for generating a schedule that performs the buffer writing for each day.
   * @param workingSchedule the buffer to append to.
   * @doctorIndexToPatientsList the mapping from doctor indices to the list of patients that doctor will vaccinate.
   */
  private void appendDayToSchedule(StringBuffer workingSchedule, ArrayList<ArrayList<Patient>> doctorIndexToPatientsList) {
    
    // Iterate over each doctor index (alike each doctor)
    for (int doctorIndex = 0; doctorIndex < doctorListing.size(); doctorIndex++) {

      // Check if the current doctor has been scheduled to vaccinate anyone
      if (doctorIndexToPatientsList.get(doctorIndex).size() != 0) {

        // Append a head to the buffer
        workingSchedule.append(doctorListing.get(doctorIndex).getName() + " will vaccinate:\n");
        
        // Get the list of all the patients the current doctor is scheduled to vaccinate on the current day
        ArrayList<Patient> patientsToVaccinate = doctorIndexToPatientsList.get(doctorIndex);

        // Append each patient the doctor is now scheduled to vaccinate to the buffer
        for (Patient patient : patientsToVaccinate) {
          workingSchedule.append("\t" + patient.getContactDetails() +  "\n");
        }

        // Remove the appended patients
        doctorIndexToPatientsList.get(doctorIndex).clear();
      }
      
    }
  }

  /**
   * Generate the schedule for the current state, save the schedule in the currentSchedule field, and return the new schedule.
   * @param startingDayIndex the index for the day of the week to start generating the schedule (must be bewteen 1 and 7 inclusive, where 1 is Sunday and 7 is Saturday).
   * @param numberOfDays the number of active days to schedule out to.
   * @return the new schedule.
   * @throws FatalError if the call will result in an infinite loop (there is no way of administering doses).
   */
  public String generateScheduleString(int startingDayIndex, int numberOfDays) {

    // Create a working string to present as the schedule, and set it initially to the schedule's header
    StringBuffer workingSchedule = new StringBuffer("Schedule as of " + BasicFormatter.getDateString(LocalDate.now()) + ":\n");

    // Ensure there will be infinite looping upon a call
    if (!hasDoctors()) {
      throw new FatalError("No doctors in the register at schedule generation");
    } else if (!canAdminister()) {
      throw new FatalError("No scheduling alignments in the register at schedule generation");
    }

    // Create variable to track the active day number (this is not incremented for idle days)
    int dayNumber = 1;
    // Create a variable to track at which index the next patient to process should be pulled from
    int currentPatientToProcessIndex = 0;
    // Create a variable to track at which index the next doctor to perform a vaccination should be pulled from (this will rotate across all available indices)
    int currentDoctorToAdminIndex = 0;

    // Create a mapping from a doctor's index in the listing to the list of patients that doctor will vaccinate, which will be set intially to empty lists
    ArrayList<ArrayList<Patient>> doctorIndexToPatientsList = new ArrayList<ArrayList<Patient>>();
    for (int doctorIndex = 0; doctorIndex < doctorListing.size(); doctorIndex++) {
      doctorIndexToPatientsList.add(new ArrayList<Patient>());
    }

    // Create a variable to track the running day of the week
    int dayOfWeek = startingDayIndex;

    // Iterate while there are days to schedule and there are patients to process for those days
    while (dayNumber <= numberOfDays && currentPatientToProcessIndex < patientListing.size()) {

      // Set the number of vaccines to administer in the day to the maximum number that can be administered on the (dayNumber)th day of the week
      int maximumDosesLeft = maximumDosesForDay(dayOfWeek);
      
      // Check if any doses can be administered on the day of the week
      if (maximumDosesLeft != 0) {

        // Doses can be administered (append this day to the schedule)

        workingSchedule.append("Day " + dayNumber + " (" + BasicFormatter.getDayOfWeekName(dayOfWeek) + "):\n");

        // Iterate while there vaccinations due and there are still patients to process
        while(maximumDosesLeft > 0 && currentPatientToProcessIndex < patientListing.size()) {

          // Get information about the next up doctor
          Doctor doctorToAdmin = doctorListing.get(currentDoctorToAdminIndex);
          int patientsHandledByDoctor = doctorIndexToPatientsList.get(currentDoctorToAdminIndex).size();

          // Check if the doctor has room to add another patient to their list of those to vaccinate
          if (patientsHandledByDoctor < doctorToAdmin.getDosesAdministeredPerDay(dayOfWeek)) {
            // Add the patient to the doctor's list
            Patient patientToProcess = patientListing.get(currentPatientToProcessIndex);
            doctorIndexToPatientsList.get(currentDoctorToAdminIndex).add(patientToProcess);
            currentPatientToProcessIndex += 1;
            maximumDosesLeft -= 1;
          }

          // Cycle to the next doctor to administer doses
          currentDoctorToAdminIndex = (currentDoctorToAdminIndex + 1) % doctorListing.size();
        }

        // Append the doctors' patients to the schedule buffer and clear doctorIndexToPatientsList for the next iteration 
        appendDayToSchedule(workingSchedule, doctorIndexToPatientsList);
        
        // Increase the active day index
        dayNumber += 1;
        
      }

      // Cycle to the next day of the week
      dayOfWeek = (dayOfWeek % 7) + 1;
      
    }

    // Save and return the resulting schedule
    currentSchedule = new String(workingSchedule);
    return currentSchedule;

  }

  /**
   * Get the current schedule string.
   * @return the current schedule.
   */
  public String getCurrentScheduleString() {
    return currentSchedule;
  }
  
}