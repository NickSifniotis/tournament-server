package Common;


import Common.Email.EmailTypes;
import Services.EmailService;
import Services.Messages.EmailMessage;
import Services.ServiceManager;

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
    private static ServiceManager service = new ServiceManager(EmailService.class);


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * StartService and StopService are controlled by the TournamentManager.
     */
    public static void StartService() { service.StartService(); }

    public static void StopService()
    {
        service.StopService();
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Creates the email message and sends it to the service queue.
     *
     */
    public static void SendEmail (EmailTypes type, String destination_address, int tournament_id)
    {
        SendEmail(type, destination_address, tournament_id, null);
    }

    public static void SendEmail (EmailTypes type, String destination_address, int tournament_id, String attachment)
    {
        service.SendMessage(new EmailMessage(type, destination_address, tournament_id, attachment));
    }
}
