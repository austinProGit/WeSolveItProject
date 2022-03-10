import java.time.LocalDate;

/**
 * A class that provides static formatting methods for the dates, days of the week, and tests used in our program.
 * @author Thomas Merino
 * @version 1.0 (last modified 5/1/21)
 */
public class BasicFormatter {

  /** Overriden the default constructor, so BasicFormatter cannot be instantiated. */
  private BasicFormatter() {}
  
  /**
   * Return the string equivalent of the day of the week indicated by the passed index.
   * @param dayIndex the index of the day of the week (where 1 is Sunday and 7 is Saturday).
   * @return the string representation of the day specified by the passed index.
   */
  public static String getDayOfWeekName(int dayIndex) {

    switch (dayIndex) {
      case 1: return "Sunday";
      case 2: return "Monday";
      case 3: return "Tuesday";
      case 4: return "Wednesday";
      case 5: return "Thursday";
      case 6: return "Friday";
      default: return "Saturday";
    }

  }

  /**
   * Format a date into a string in the form month/day/year.
   * @param date the LocalDate to format.
   * @return the string representation of the date in the form month/day/year.
   */
  public static String getDateString(LocalDate date) {
    return date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();
  }

  /**
   * Convert an int between or equal to 0 and 999 to a three-digit string by adding any preceding necessary zeros.
   * @param number the number to be converted into a string.
   * @return the number as a three-digit (character) string.
   * @throws IllegalArgumentException if an int outside of 0 to 999 inclusive is passed.
   */
  public static String getThreeDigitString(int number) {

    // Check the interval the number is on
    if (number >= 0) {
      if (number < 10) {
        return "00" + number;
      } else if (number < 100) {
        return "0" + number;
      } else if (number < 1000) {
        return Integer.toString(number);
      }
    }
    // The number is not in 0 to 999 inclusive (throw an IllegalArgumentException)
    throw new IllegalArgumentException("attempt to format " + number + " as a three-digit (character) string");
  }

}