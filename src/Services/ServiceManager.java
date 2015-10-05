package Services;

import Common.LogManager;
import Services.Logs.LogType;
import Services.Messages.Message;
import Services.Messages.TerminateMessage;

/**
 * Created by nsifniotis on 5/10/15.
 *
 * Base class for service managers.
 */
public class ServiceManager
{
    private Class wut_sort_of_class;
    private Service service;
    private boolean service_active;


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Constructor
     *
     * @param sc - the sort of service class that this service manager manages
     */
    public ServiceManager (Class sc)
    {
        this.wut_sort_of_class = sc;
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Starts the service safely.
     */
    public void StartService()
    {
        Service s;
        try
        {
            s = (Service) this.wut_sort_of_class.newInstance();
        }
        catch (Exception e)
        {
            LogManager.Log (LogType.ERROR, "ServiceManager StartService: Unable to create new instance of class " + this.wut_sort_of_class.getName());
            return;
        }

        this.service = s;
        this.service.start();
        this.service_active = true;
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Stop the service safely.
     */
    public void StopService ()
    {
        this.service_active = false;
        this.service.MessageQueue().add(new TerminateMessage());
        while (this.service.Alive())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                // nah, still sleeping.
            }
        }
        this.service = null;
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Enqueues the message if the service is active.
     *
     * @param msg - the message object to enqueue.
     */
    public void SendMessage (Message msg)
    {
        if (this.service_active)
            this.service.MessageQueue().add(msg);
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Simple getter functions.
     *
     * @return true if this service is still active.
     */
    public boolean Alive() { return this.service_active; }
}
