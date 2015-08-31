package PlayerMarshall.Verification;

import java.io.File;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * This class is more about testing the code plugin integration system than anything else.
 * Returns true if the submission has a .txt in its filename, false otherwise
 *
 */
public class TestVerify implements IVerification
{
    @Override
    public boolean VerifySubmission(File player_submission)
    {
        return (player_submission.getName().contains(".txt"));
    }
}
