
/**
 * This VaccineRegisterTesting class tests adding and sorting Patients, Doctors, and all other inputs to ensure the production of correct schedules.
 * @author Thomas Merino, Austin Lee, Nam Luu.
 * @version 1.0 (last modified 5/2/21).
 */
public class VaccineRegisterTesting {

  public static void main(String[] args) {

    System.out.println("Test started.");

    // Set up some reusable parameter arrays for testing

    // (a)ge (W)eight:
    int[] aW1 = { 0,  0,  0,  0,  2, 10, 50, 60, 70};
    int[] aW2 = {70, 50, 40, 30, 20, 10,  5,  1,  0};

    // (q)uestionnaire (W)eight:
    int[] qW1 = { 0,  1, 10, 20, 30};
    int[] qW2 = {-2,  1,  2,  3,  4};
    int[] qW3 = { 1,  1,  3, -1, -3};
    int[] qW4 = { 1,  1,  1,  2,  2};

    // (d)aily (D)oses availble for day of the week:
    int[] dD1 = {10, 70, 70, 70, 70, 90,  0};
    int[] dD2 = { 0,  5,  5,  5,  5,  5,  5};
    int[] dD3 = { 0,  4,  3,  0,  0,  5,  3};

    // (p)atient (P)arameters:
    boolean[] p01P = {true,  true,  true,  true,  true };
    boolean[] p02P = {false, true,  true,  true,  true };
    boolean[] p03P = {false, false, true,  true,  true };
    boolean[] p04P = {false, false, false, true,  true };
    boolean[] p05P = {false, false, false, false, true };
    boolean[] p06P = {false, false, false, false, false};

    boolean[] p07P = {true,  false, false, false, false};
    boolean[] p08P = {false, true,  false, false, false};
    boolean[] p09P = {false, false, true,  false, false};
    boolean[] p10P = {false, false, false, false, true };


    // (d)octor (P)arameters:
    int[] d01P = { 1,  1,  1,  1,  1,  1,  1};
    int[] d02P = { 0,  1,  0,  1,  0,  1,  0};
    int[] d03P = { 1,  1,  0,  0,  1,  1,  0};
    int[] d04P = { 0,  0,  0,  0,  0,  0,  1};

    int[] d05P = { 4,  4,  1,  1,  5,  5,  0};
    int[] d06P = { 2,  2,  1,  1,  3,  3,  0};
    int[] d07P = { 3,  2,  1,  3,  5, 10, 10};
    int[] d08P = { 0,  0,  0,  0,  0,  0,  0};

    int[] d09P = {90, 90, 90, 90, 90, 90, 90};


    // Set up some the registers and test them
    
    // generateTestRegister parameters: ageWeighting, weighting, dosesPerDay, patientAges, patientParameters, doctorParameters
    // testRegisterSchedule parameters: testName, testRegister, startingDayIndex, numberOfDays, expectedExpressionPattern

    // Test age sorting (with ties) and recompiling
    Register r1 = Register.generateTestRegister(aW1, qW1, dD1, new int[]{49, 59, 50, 51, 53, 51, 60, 48, 49, 50}, new boolean[][]{p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P}, new int[][]{d09P});
    String regex1 = ".*Sunday.*d000.*p006.*p001.*p002.*p003.*p004.*p005.*p009.*p000.*p007.*p008.*";
    Register.testRegisterSchedule("1", r1, 1, 1, regex1 );
    r1.recompilePatients();
    Register.testRegisterSchedule("2", r1, 1, 1, regex1);


    // Test general weight sorting (with ties)
    Register r2 = Register.generateTestRegister(aW1, qW2, dD1, new int[]{30, 30, 30, 40, 40, 40}, new boolean[][]{p06P, p07P, p10P, p09P, p04P, p01P}, new int[][]{d09P});
    Register.testRegisterSchedule("3", r2, 3, 1,  ".*Tuesday.*d000.*p005.*p004.*p002.*p003.*p000.*p001.*");


    // Test doctor dispatch, age sorting (with ties), and recompiling
    Register r3 = Register.generateTestRegister(aW2, qW1, dD1, new int[]{60, 60, 60, 40, 40, 30, 30, 20, 20, 20}, new boolean[][]{p08P, p08P, p08P, p08P, p08P, p08P, p08P, p08P, p08P, p08P}, new int[][]{d04P, d01P, d06P});
    String regex3 = ".*Friday.*d001.*p007.*d002.*p008.*p009.*p005.*Sunday.*d001.*p006.*d002.*p003.*p004.*";
    Register.testRegisterSchedule("4", r3, 6, 2, regex3);
    r3.recompilePatients();
    Register.testRegisterSchedule("5", r3, 6, 2, regex3);


    // Test doctor dispatch and weight sorting
    Register r4 = Register.generateTestRegister(aW2, qW3, dD2, new int[]{30, 30, 30, 30, 30, 30}, new boolean[][]{p01P, p02P, p03P, p04P, p05P, p06P}, new int[][]{d02P, d02P, d07P, d07P, d08P});
    String regex4 = ".*Saturday.*d002.*p000.*p005.*p004.*d003.*p001.*p002.*Monday.*d003.*p003.*";
      Register.testRegisterSchedule("6", r4, 7, 2, regex4);


    // Test doctor dispatch and recompiling
    Register r5 = Register.generateTestRegister(aW1, qW4, dD3, new int[]{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30}, new boolean[][]{p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P, p01P}, new int[][]{d01P, d02P, d03P, d05P});
    String regex5 = ".*Monday.*d000.*p000.*d001.*p001.*d002.*p002.*d003.*p003.*Tuesday.*d000.*p004.*d003.*p005.*Friday.*d000.*p006.*d001.*p007.*d002.*p008.*d003.*p009.*p010.*Saturday.*d000.*p011.*Monday.*d000.*p015.*d001.*p012.*d002.*p013.*d003.*p014.*Tuesday.*d000.*p017.*d003.*p016.*";
    Register.testRegisterSchedule("7", r5, 2, 10, regex5);
    r4.recompilePatients();
    Register.testRegisterSchedule("8", r5, 2, 10, regex5);

    System.out.println("Test completed.");

  }

}