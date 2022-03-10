import java.util.TreeMap;
import java.time.LocalDate;
import java.io.Serializable;

/**
 * The Patient class provides a model for the creation of Patient objects. Patients are the sole recipients of vaccine doses in our program. 
 * @author Thomas Merion, Nam Luu, Austin Lee.
 * @version 1.0 (last modified 5/2/21).
 */
public class Patient extends Person implements Serializable, Comparable<Patient> {

  /** The serial version used when decoding. */
  private static final long serialVersionUID = 1100L;

  /** A unique identifying number used by the Register. */
  private int patientID;

  /** The birthdate of the patient (this is initially set to null, so this must be set outside of the construct before use). */
  private LocalDate birthdate;

  /** The level of priority (used by a Register instance for sorting). */
  private int priorityWeight;

  /** The mapping of question keys to the patient's responses to the questions. */
  private TreeMap<String, Boolean> medicalResponses;

  /**
   * Constructor that takes the name, phone number, and email address.
   * @param name the initial name.
   * @param phone the initial phone number.
   * @param email the initial email address.
   */
  public Patient(String name, String phone, String email) {

    super(name, phone, email);
    
    this.birthdate = null;
    patientID = -1;
    priorityWeight = -1;
    medicalResponses = new TreeMap<String, Boolean>();
  }

  /**
   * Constructor that sets all string fields to "N/A", the birthdate to null, the ID and priority weight to -1 (not specified), and the medical responses to an empty mapping.
   */
  public Patient() {
    super();

    this.birthdate = null;
    patientID = -1;
    priorityWeight = -1;
    medicalResponses = new TreeMap<String, Boolean>();
  }

  /**
   * Setter for the patient ID.
   * @param patientID the new ID.
   */
  public void setPatientID(int patientID) {
    this.patientID = patientID;
  }

  /**
   * Getter for the patient ID.
   * @return the patient ID.
   */
  public int getPatientID() {
    return patientID;
  }

  /**
   * Setter for the birthdate.
   * @param birthdate the new birthdate.
   */
  public void setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
  }

  /**
   * Getter for the birthdate.
   * @return the current birthdate.
   */
  public LocalDate getBirthdate() {
    return birthdate;
  }

  /**
   * Setter for the priority weight.
   * @param priorityWeight the new priority weight.
   */
  public void setPriorityWeight(int priorityWeight) {
    this.priorityWeight = priorityWeight;
  }

  /**
   * Getter for the priority weight.
   * @return the current priority weight.
   */
  public int getPriorityWeight() {
    return priorityWeight;
  }
 
  /**
   * Enter a questionnaire key-response pairing into the patient's medical responses.
   * @param key the question key of the new pairing.
   * @param truthValue the response of the new pairing.
   */
  public void appendMedicalBoolean(String key, boolean truthValue) {
    medicalResponses.put(key, truthValue);
  }

  /**
   * Get the response from a key-response pairing in the patient (from the medical responses).
   * @param key the question key of the desired pairing.
   * @return the response that coordinates with the key (or null if no pairing is found).
   */
  public Boolean getMedicalBoolean(String key) {
    return medicalResponses.get(key);
  }

  /**
   * Get a string of the form "*name*:   ID: *ID*, phone: *phone number*, email: *email address*".
   * @return the basic contact information string.
   */
  public String getContactDetails() {
    return name + "   ID: " + patientID + ", phone: " + phone + ", email: " + email;
  }

  /**
   * Compare this and another patient and get an int that indicates the order in a listing of priority weighting.
   * @param other the patient to compare with.
   * @returns an int that indicates the ordering (negative implies the this comes before the other, zero means they are equivalent, and a postive number means this comes after the other).
   */
  public int compareTo(Patient other) {

    if (priorityWeight == other.priorityWeight) {
      // Use the IDs to break the tie (resulting in a first come, first serve ordering)
      return patientID - other.patientID;

    } else {
      return other.priorityWeight - priorityWeight;
    }
  }

  /**
   * Get a string representation of the instance.
   * @return the string representation. 
   */
  @Override
  public String toString() {
    String result = name + " (patient)\n\tPhone: " + phone + "\n\tEmail: " + email + "\n\tBirthdate: " + BasicFormatter.getDateString(birthdate) + "\n\tPatient ID: " + patientID;

    // Append the questionnaire responses to the result
    for (var pairing : medicalResponses.entrySet()) {
      result += "\n\t" + pairing.getKey() + ": " + (pairing.getValue() ? "yes" : "no");
    }

    return result;
  }
}