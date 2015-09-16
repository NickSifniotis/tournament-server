package Common.Email;

import microsoft.exchange.webservices.data.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.net.URI;
import java.util.Date;
import java.util.Properties;


/**
 * Created by nsifniotis on 9/09/15.
 *
 * Emailer class that sends standard emails out to students regarding their submissions.
 *
 */
public class Emailer
{
    //@TODO: Implement this shit
    //@TODO: I am certain that more parameters will be needed in that list of arguments
    public static void SendEmail (EmailTypes type, String destination_address)
    {
        String subject = "test email";
        String host = "smtp-mail.outlook.com";
        int port = 587;
        String userName = "blokus_tournament@hotmail.com";
        String password = "b64094bf";
        String body = "Your player has been successfully registered for the 2015 Blokus tournament. WOW";


        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.socketFactory.fallback", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);


        try
        {

            Session mailSession = Session.getDefaultInstance(properties);
            mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();

            // creates a new e-mail message
            Message msg = new MimeMessage(mailSession);


            msg.setFrom(new InternetAddress(userName));
            InternetAddress[] toAddresses = {new InternetAddress(destination_address)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html");

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // sets the multi-part as e-mail's content
            msg.setContent(multipart);

            // sends the e-mail
            transport.connect(host, port, userName, password);

            transport.sendMessage(msg,
                    msg.getRecipients(Message.RecipientType.TO));
            transport.close();
        }
        catch (Exception e)
        {
            System.out.println (e.toString());
            e.printStackTrace();
        }


        return;
    }
}
