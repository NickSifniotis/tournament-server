package Common;


import Common.Email.EmailTypes;
import Services.EmailService;
import Services.Messages.EmailMessage;
import Services.Messages.TerminateMessage;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * Emailer class that sends standard emails out to students regarding their submissions.
 *
 * @TODO there may be concurrency issues with deleted attachments.
 *
 */
public class Emailer
{
    private static EmailService service;
    private static boolean service_active = false;

    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * StartService and EndService are controlled by the TournamentManager.
     */
    public static void StartService ()
    {
        service = new EmailService();
        service.start();
        service_active = true;
    }

    public static void EndService ()
    {
        service_active = false;
        service.MessageQueue().add(new TerminateMessage());
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Creates the email message and sends it to the service queue.
     * If the service is inactive, do nothing.
     *
     */
    public static void SendEmail (EmailTypes type, String destination_address, int tournament_id)
    {
        SendEmail(type, destination_address, tournament_id, null);
    }

    public static void SendEmail (EmailTypes type, String destination_address, int tournament_id, String attachment)
    {
        if (service_active)
            service.MessageQueue().add(new EmailMessage(type, destination_address, tournament_id, attachment));
    }
}
