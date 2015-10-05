package Services.Messages;

import Common.Email.EmailTypes;

/**
 * Created by nsifniotis on 5/10/15.
 *
 * The 'envelope' that contains the email to be mailed out.
 *
 */
public class EmailMessage extends Message
{
    private String destination;
    private EmailTypes type;
    private int tournament_id;
    private String attachment;


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * A couple of simple constructors.
     */
    public EmailMessage(EmailTypes type, String dest, int tourney_id)
    {
        this (type, dest, tourney_id, null);
    }

    public EmailMessage(EmailTypes type, String dest, int tournament_id, String attachment)
    {
        this.type = type;
        this.destination = dest;
        this.tournament_id = tournament_id;
        this.attachment = attachment;
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Accessor functions.
     *
     * @return various bits of data.
     */
    public String Destination() { return this.destination; }
    public int TournamentID() { return this.tournament_id; }
    public EmailTypes EmailType() { return this.type; }
    public String Attachment() { return this.attachment; }
}
