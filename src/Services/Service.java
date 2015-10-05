package Services;

import Common.LogManager;
import Services.Logs.LogType;
import Services.Messages.LogMessage;
import Services.Messages.Message;
import Services.Messages.TerminateMessage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * Your basic service class.
 *
 * These things all record start and stop times in the system logs, at least.
 * Interestingly, the logging service is not able to record either its own start
 * or its own end.
 *
 */
public abstract class Service extends Thread
{
    private BlockingQueue<Message> message_queue;
    protected boolean alive;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Constructor for this object. Create an empty message queue.
     */
    public Service()
    {
        this.message_queue = new LinkedBlockingQueue<>();
        this.alive = false;

        String creation_message = "Service " + this.getClass().getName() + " created!";
        LogManager.Log(LogType.TOURNAMENT, creation_message);
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

        String shutdown_message = "Service " + this.getClass().getName() + " shutting down.";
        LogManager.Log(LogType.TOURNAMENT, shutdown_message);

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
            do_service();

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
                Thread.sleep(500);
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
