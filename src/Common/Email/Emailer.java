package Common.Email;

import Common.DataModel.Tournament;
import Services.LogService;
import Services.Logs.LogType;
import Common.SystemState;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * Created by nsifniotis on 9/09/15.
 *
 * Emailer class that sends standard emails out to students regarding their submissions.
 *
 * This class is intentionally not concurrent - though it bloody well could be, given how long it takes
 * to send one of those damned emails out. But it would be impossible to send out as attachments the very
 * same submission files that the marshall is lining up to delete ..
 *
 */
public class Emailer
{
    public static void SendEmail (EmailTypes type, String destination_address, int tournament_id)
    {
        SendEmail(type, destination_address, tournament_id, null);
    }


    public static void SendEmail (EmailTypes type, String destination_address, int tournament_id, String attachment)
    {
        if (destination_address == null || destination_address.equals(""))
        {
            destination_address = "u5809912@anu.edu.au";
            type = EmailTypes.NO_VALID_EMAIL;
        }

        Tournament tourney = new Tournament(tournament_id);
        String tourney_name = tourney.Name();
        String tourney_slots = String.valueOf(tourney.NumSlots());
        String body = "";

        try
        {
            List<String> lines = Files.readAllLines(type.Template());
            for (String l: lines)
            {
                l = l.replace("{TOURNAMENT.NAME}", tourney_name);
                l = l.replace("{TOURNAMENT.SLOTS}", tourney_slots);
                body += l + "\n";
            }
        }
        catch (Exception e)
        {
            // this should never happen!
            String error = "Emailer.SendEmail - error when attemption to load email template " + type.Template() + ": " + e;
         //   LogService.Log(LogType.ERROR, error);

            return;
        }


        Properties properties = GetSMTPProperties();

        try
        {
            Session mailSession = Session.getDefaultInstance(properties);
            Transport transport = mailSession.getTransport();

            // creates a new e-mail message
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(SystemState.Email.fromAddress));
            InternetAddress[] toAddresses = {new InternetAddress(destination_address)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(type.Subject());
            msg.setSentDate(new Date());

            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html; charset=utf-8");

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (attachment != null)
            {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(attachment);
                multipart.addBodyPart(messageBodyPart);
            }

            // sets the multi-part as e-mail's content
            msg.setContent(multipart);

            // sends the e-mail
            transport.connect(SystemState.Email.host, SystemState.Email.port,
                    SystemState.Email.userName, SystemState.Email.password);
            transport.sendMessage(msg,
                    msg.getRecipients(Message.RecipientType.TO));
            transport.close();
        }
        catch (Exception e)
        {
            System.out.println (e.toString());
            e.printStackTrace();
        }


    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * @return a structure containing the properties needed to send an email
     */
    private static Properties GetSMTPProperties()
    {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.host", SystemState.Email.host);
        properties.put("mail.smtps.port", SystemState.Email.port);
        properties.put("mail.smtps.auth", "true");
        properties.put("mail.smtps.starttls.enable", "true");
        properties.put("mail.smtps.socketFactory.fallback", "true");
        properties.put("mail.user", SystemState.Email.userName);
        properties.put("mail.password", SystemState.Email.password);

        return properties;
    }
}
