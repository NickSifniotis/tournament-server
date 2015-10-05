package PlayerMarshall;

import Services.ServiceManager;


/**
 * Created by nsifniotis on 5/10/15.
 *
 * Manager class for the PlayerMarshall service.
 *
 */
public class PlayerMarshallManager
{
    private static ServiceManager service;


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * StartService and EndService are controlled by the TournamentManager.
     */
    public static void StartService ()
    {
        service = new ServiceManager(PlayerMarshallService.class);
    }

    public static void EndService ()
    {
        service.StopService();
    }

    public static boolean Alive() { return (service != null) && service.Alive(); }
}
