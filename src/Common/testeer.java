package Common;

import Common.Email.EmailTypes;
import Common.Email.Emailer;

/**
 * Created by nsifniotis on 16/09/15.
 */
public class testeer
{
    public static void main(String[] args)
    {

        Emailer.SendEmail(EmailTypes.DISQUALIFIED, "u5809912@anu.edu.au");
    }
}
