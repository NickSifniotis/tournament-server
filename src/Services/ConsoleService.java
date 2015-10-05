package Services;

import Services.Messages.LogMessage;
import Services.Messages.Message;

/**
 * Created by nsifniotis on 5/10/15.
 *
 * Provides a quick and dirty console log dump.
 */
public class ConsoleService extends Service
{
    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Dump the received message to the console.
     * If it's not the right sort of message, just ignore it.
     *
     * @param message - the message that they have to handle.
     */
    @Override
    public void handle_message(Message message)
    {
        if (!(message instanceof LogMessage))
            return;

        String msg = ((LogMessage) message).Message();
        System.out.println (msg);
        System.out.flush();
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Don't have to do a thing.
     */
    @Override
    public void do_service()
    {
        // fuck all
    }
}
