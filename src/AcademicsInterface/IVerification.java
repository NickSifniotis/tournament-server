package AcademicsInterface;

import java.io.File;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Implement this interface if you want to check student submissions
 * to ensure they are using safe Haskell or not trying to hack each others
 * code or download solutions from the internet ....
 *
 */
public interface IVerification
{
    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * This method accepts a raw submission from the Gitlab server and
     * does whatever it needs to do to extract the relevant code / files
     * that form the student submission.
     *
     * @param player_submission - the raw data from Gitlab
     * @return a file object that holds the student's code
     */
    File ExtractSubmission (File player_submission);


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Accepts a raw student submission from Gitlab, and extracts from that
     * submission metadata including a team name, team email address and so forth.
     *
     * The metadata object initialises itself with default values so
     * if this method fails to extract the data, it will fail elegantly. Just return
     * the blank metadata object.
     *
     * @param player_submission - the raw data from Gitlab
     * @return - a SubmissionMetadata object populated with data.
     */
    SubmissionMetadata ExtractMetaData (File player_submission);


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * VerifySubmission is a method that accepts a File object that points
     * directly to the student's submission.
     * Return TRUE if the submission is acceptable and it will be added to the game
     * Return FALSE otherwise, and the submission will be deleted, and the students
     * emailed a stern warning.
     *
     * @param extracted_submission - the student's submission after extraction from
     *                             the ExtractSubmission method.
     * @return - true if the submission is ok, false otherwise
     *
     */
    boolean VerifySubmission (File extracted_submission);

}
