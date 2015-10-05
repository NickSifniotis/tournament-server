package Services.Messages;


/**
 * Created by nsifniotis on 9/09/15.
 *
 * A messenger class used to carry messages between the console based interface
 * of the TournamentKey Server, and the TournamentKey running child thread.
 *
 * What sort of messages will these be? Who knows. It is in the hands of the gods.
 *
 */
public class TSMessage extends Message
{
    public TSMessageType message;
    public int payload;


    public TSMessage(TSMessageType t, int p)
    {
        this.message = t;
        this.payload = p;
    }


    public TSMessage(TSMessageType t)
    {
        this(t, 0);
    }
}
