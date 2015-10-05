package Services;

import Services.Messages.Message;
import Services.Messages.TerminateMessage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * Your basic service class.
 *
 */
public abstract class Service extends Thread
{
    protected LogService log_service;
    private BlockingQueue<Message> message_queue;
    private boolean alive;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Constructor for this object. Create an empty message queue.
     */
    public Service(LogService logs)
    {
        this.log_service = logs;
        this.message_queue = new LinkedBlockingQueue<>();
        this.alive = false;

        if (logs != null)
        {
            // @TODO send a creation message.
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Accessor methods.
     *
     * @return the things
     */
    public BlockingQueue<Message> MessageQueue() { return this.message_queue; }
    public boolean Alive() { return this.alive; }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * This isn't the most interesting method in the class.
     *
     */
    @Override
    public void run()
    {
        alive = true;
        this.main_loop();
        alive = false;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * This is.
     *
     * Do the things. Terminate if need be. Palm off other messages
     * to the child services that implement this parent class.
     */
    private void main_loop()
    {
        boolean terminated = false;
        while (!terminated)
        {
            Message message = message_queue.peek();
            if (message != null)
            {
                // pop the message off the queue
                try
                {
                    message_queue.take();
                }
                catch (Exception e)
                { /* fuck off, I flat out have no idea what to do here. */ }

                if (message instanceof TerminateMessage)
                    terminated = true;
                else
                    handle_message(message);
            }

            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // yeah, the system doesn't interrupt threads in this way
                // if the thread has been interrupted, it's by the OS
                // or some other external source. anyway,

                terminated = true;
            }
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * This method needs to be overridden by services.
     * They will respond to their messages however they please.
     *
     * @param message - the message that they have to handle.
     */
    public abstract void handle_message (Message message);


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Do whatever this service does whenever it's active and nothing else is going on.
     */
    public abstract void do_service ();
}
