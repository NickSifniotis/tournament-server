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
    NO_RESUBMIT_ON, NO_RESUBMIT_OFF, DISQUALIFIED, NO_VALID_EMAIL, ABNORMAL,
    FAILED_VALIDATION, NO_SUBMIT_ON, NO_SUBMIT_OFF, NO_SLOTS_AVAILABLE, NO_METADATA;


    public Path Template()
    {
        switch (this)
        {
            case NO_VALID_EMAIL:
                return Paths.get(SystemState.Email.templates_folder + "no_valid_email.html");
            case NO_METADATA:
                return Paths.get(SystemState.Email.templates_folder + "no_metadata.html");
            case NO_RESUBMIT_OFF:
                return Paths.get(SystemState.Email.templates_folder + "no_resubmit_off.html");
            case NO_RESUBMIT_ON:
                return Paths.get(SystemState.Email.templates_folder + "no_resubmit_on.html");
            case NO_SUBMIT_OFF:
                return Paths.get(SystemState.Email.templates_folder + "no_submit_off.html");
            case NO_SUBMIT_ON:
                return Paths.get(SystemState.Email.templates_folder + "no_submit_on.html");
            case NO_SLOTS_AVAILABLE:
                return Paths.get(SystemState.Email.templates_folder + "no_metadata.html");
            case FAILED_VALIDATION:
                return Paths.get(SystemState.Email.templates_folder + "failed_valid.html");
            case DISQUALIFIED:
                return Paths.get(SystemState.Email.templates_folder + "disqualification.html");
            case ABNORMAL:
                return Paths.get(SystemState.Email.templates_folder + "abnormal.html");
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
            case NO_RESUBMIT_OFF:
                return "Submission rejected";
            case NO_RESUBMIT_ON:
                return "Submission rejected";
            case NO_SUBMIT_OFF:
                return "Submission rejected";
            case NO_SUBMIT_ON:
                return "Submission rejected";
            case NO_SLOTS_AVAILABLE:
                return "No room left in tournament";
            case FAILED_VALIDATION:
                return "Submission failed verification testing";
            case DISQUALIFIED:
                return "You've been disqualified.";
            case ABNORMAL:
                return "Game terminated abnormally.";
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
