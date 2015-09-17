package Common.Email;

import Common.SystemState;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * An enumeration that holds the different types of emails that can be sent out by the system.
 */
public enum EmailTypes
{
    NO_RESUBMIT_ON, NO_RESUBMIT_OFF, DISQUALIFIED, NO_VALID_EMAIL,
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
        }

        return null;
    }
}
