package TournamentServer;

import Services.Messages.TSMessage;
import Services.Messages.TSMessageType;
import Services.ServiceManager;

/**
 * Created by nsifniotis on 6/10/15.
 *
 * Service manager class for the Tournament Server service.
 */
public class TournamentServer
{
    private static ServiceManager service = new ServiceManager(TournamentService.class);


    public static void StartService()
    {
        service.StartService();
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * This is an interesting one. Look at the way it stops the service - it's nothing like the others.
     * This is because the tournament server might be halfway through running a game.
     * It has to wait until the game finishes before it can shut itself down.
     *
     */
    public static void StopService()
    {
        service.SendMessage(new TSMessage(TSMessageType.END));
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Changes the number of games that the tournament server can run concurrently.
     *
     * @param new_size - the maximum number of games to run at the one time.
     */
    public void ResizeThreadPool(int new_size)
    {
        service.SendMessage(new TSMessage(TSMessageType.THREAD_POOL_RESIZE, new_size));
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * @return true if ths service is active.
     */
    public static boolean Alive() { return service.Alive(); }
}
