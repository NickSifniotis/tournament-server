package Common.Email;

import Common.SystemState;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * An enumeration that holds the different types of emails that can be sent out by the system.
 */
public enum EmailTypes
{
    NO_RESUBMIT, DISQUALIFIED, NO_VALID_EMAIL, ABNORMAL, GAME_OVER,
    FAILED_VALIDATION, NO_SUBMIT, NO_SLOTS_AVAILABLE, NO_METADATA;


    public Path Template()
    {
        switch (this)
        {
            case NO_VALID_EMAIL:
                return Paths.get(SystemState.Email.templates_folder + "no_valid_email.html");
            case NO_METADATA:
                return Paths.get(SystemState.Email.templates_folder + "no_metadata.html");
            case NO_RESUBMIT:
                return Paths.get(SystemState.Email.templates_folder + "no_resubmit.html");
            case NO_SUBMIT:
                return Paths.get(SystemState.Email.templates_folder + "no_submit.html");
            case NO_SLOTS_AVAILABLE:
                return Paths.get(SystemState.Email.templates_folder + "no_slots.html");
            case FAILED_VALIDATION:
                return Paths.get(SystemState.Email.templates_folder + "failed_valid.html");
            case DISQUALIFIED:
                return Paths.get(SystemState.Email.templates_folder + "disqualification.html");
            case ABNORMAL:
                return Paths.get(SystemState.Email.templates_folder + "abnormal.html");
            case GAME_OVER:
                return Paths.get(SystemState.Email.templates_folder + "game_over.html");
        }

        return null;
    }


    public String Subject()
    {
        switch (this)
        {
            case NO_VALID_EMAIL:
                return "Invalid Email on Submission";
            case NO_METADATA:
                return "Submission rejected";
            case NO_RESUBMIT:
                return "Submission rejected";
            case NO_SUBMIT:
                return "Submission rejected";
            case NO_SLOTS_AVAILABLE:
                return "No room left in tournament";
            case FAILED_VALIDATION:
                return "Submission failed verification testing";
            case DISQUALIFIED:
                return "You've been disqualified.";
            case ABNORMAL:
                return "Game terminated abnormally.";
            case GAME_OVER:
                return "Game results";
        }

        return "";
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * I'm looking that these methods I'm writing, and I'm thinking 'new database entity ..'
     *
     * @return true if this email template requires the attachment of a student submission
     */
    public boolean AttachSubmission()
    {
        switch (this)
        {
            case NO_VALID_EMAIL:
            case NO_METADATA:
            case FAILED_VALIDATION:
                return true;
        }

        return false;
    }
}
