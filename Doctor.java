import java.util.TreeMap;
import java.io.Serializable;

/**
 * This Doctor class provides a model for Doctor objects that can then by utlized by the Register class. A Doctor in this program is the sole entity that administers vaccinations to Patients.
 * @author Thomas Merion, Austin Lee, Nam Luu.
 * @version 1.0 (last modified 5/1/21).
 */
public class Doctor extends Person implements Serializable {

  /** The serial version used when decoding. */
  private static final long serialVersionUID = 1200L;

  /** A unique identifying number used by the Register. */
  private int doctorID;

  /** The mapping of the day of the week to the number of doses the doctor can administer on that day (where 0 is Sunday and 6 is Saturday). */
  private int[] dosesAdministeredPerDay;

  /**
   * Constructor that takes the name, phone number, and email address.
   * @param name the initial name.
   * @param phone the initial phone number.
   * @param email the initial email address.
   */
  public Doctor(String name, String phone, String email) {
    super(name, phone, email);
    this.doctorID = -1;
    dosesAdministeredPerDay = new int[7];
  }

  /**
   * Constructor that sets all string fields to "N/A", the ID to -1 (not specified), and the doses per day to a zero-ed array.
   */
  public Doctor() {
    super();
    this.doctorID = -1;
    dosesAdministeredPerDay = new int[7];
  }

  /**
   * Setter for the doctor ID.
   * @param doctorID the new ID.
   */
  public void setDoctorID(int doctorID) {
    this.doctorID = doctorID;
  }

  /**
   * Getter for the doctor ID.
   * @return the current ID.
   */
  public int getDoctorID() {
    return doctorID;
  }

  /**
   * Setter for the number of doses that can be administered on a given day.
   * @param dayIndex the day of the week to modify (where 1 is Sunday and 7 is Saturday).
   * @param numberOfDoses the new number of doses. 
   */
  public void setDosesAdministeredPerDay(int dayIndex, int numberOfDoses) {
    dosesAdministeredPerDay[dayIndex - 1] = numberOfDoses;
  }

  /**
   * Getter for the number of doses that can be administered on a given day.
   * @param dayIndex the day of the week in question (where 1 is Sunday and 7 is Saturday).
   * @return the number of doses that can be administered by the doctor that day of the week. 
   */
  public int getDosesAdministeredPerDay(int dayIndex) {
    return dosesAdministeredPerDay[dayIndex - 1];
  }

  /**
   * Get a string representation of the instance.
   * @return the string representation. 
   */
  @Override
  public String toString() {
    String result = name + " (doctor)\n\tPhone: " + phone + "\n\tEmail: " + email + "\n\tDoctor ID: " + doctorID + "\n\tVaccinations per day:";

    // Append the number of doses per day of the week to the result
    for (int dayIndex = 1; dayIndex <= 7; dayIndex++) {
      result += "\n\t\t" + BasicFormatter.getDayOfWeekName(dayIndex) + ": " + dosesAdministeredPerDay[dayIndex - 1];
    }

    return result;
  }

}