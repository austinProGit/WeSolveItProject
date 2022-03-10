import java.io.Serializable;
/**
 * This abstract Person class serves as a simple model to create Person objects to be stored in the Register. Primarily stores name and contact information. Serves as the parent class of both the Patient and Doctor subclasses.
 * @author Thomas Merion, Nam Luu, Austin Lee.
 * @version 1.0 (last modified 5/2/21).
 */
public abstract class Person implements Serializable {
  
  /** The serial version used when decoding. */
  private static final long serialVersionUID = 1000L;

  /** The full name of the person (or "N/A"). */
  protected String name;

  /** The phone number of the person (or "N/A"). */
  protected String phone;

  /** The email address of the person (or "N/A"). */
  protected String email;
  
  /**
   * No-argument constructor that sets all fields to "N/A".
   */
  public Person(){
    name = "N/A";
    phone = "N/A";
    email = "N/A";
  }

  /**
   * Full constructor that sets all fields.
   * @param name the initial name.
   * @param phone the initial phone number.
   * @param email the initial email address.
   */
  public Person(String name, String phone, String email){
    this.name = name;
    this.phone = phone;
    this.email = email;
  }

  /**
   * Setter for the name.
   * @param name the new name.
   */
  public void setName(String name){
    this.name = name;
  }

  /**
   * Getter for the name.
   * @return the current name.
   */
  public String getName(){
    return name;
  }

  /**
   * Setter for the phone number.
   * @param phone the new phone number.
   */
  public void setPhone(String phone){
    this.phone = phone;
  }

  /**
   * Getter for the phone number.
   * @return the current phone number.
   */
  public String getPhone(){
    return phone;
  }

  /**
   * Setter for the email.
   * @param email the new email.
   */
  public void setEmail(String email){
    this.email = email;
  }

  /**
   * Getter for the email address.
   * @return the current email address.
   */
  public String getEmail(){
    return email;
  }

  
}