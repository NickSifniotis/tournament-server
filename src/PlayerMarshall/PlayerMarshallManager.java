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
    private static ServiceManager service = new ServiceManager(PlayerMarshallService.class);


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * StartService and StopService are controlled by the TournamentManager.
     */
    public static void StartService ()
    {
        service.StartService();
    }

    public static void StopService()
    {
        service.StopService();
    }

    public static boolean Alive() { return service.Alive(); }
}
