package PlayerMarshall.Verification;

import java.io.File;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Implement this interface if you want to check student submissions
 * to ensure they are using safe Haskell or not trying to hack each others
 * code or download solutions from the internet ....
 *
 * VerifySubmission is a method that accepts a File object that points
 * directly to the student's submission.
 * Return TRUE if the submission is acceptable and it will be added to the game
 * Return FALSE otherwise, and the submission will be deleted, and the students
 * emailed a stern warning.
 *
 */
public interface IVerification {
    boolean VerifySubmission (File player_submission);
}
