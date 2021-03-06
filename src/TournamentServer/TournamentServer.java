package TournamentServer;

import Services.GameViewer.GameViewer;
import Services.Messages.TSMessage;
import Services.Messages.TSMessageType;
import Services.Messages.TSViewerMessage;

/**
 * Created by nsifniotis on 6/10/15.
 *
 * Service manager class for the Tournament Server service.
 *
 * This class can't use a ServiceManager to control the tournament server
 * because of the differences in the way the TS shuts itself down.
 *
 */
public class TournamentServer
{
    private static TournamentService service;


    public static void StartService()
    {
        service = new TournamentService();
        service.start();
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015

     * This is an interesting one. Look at the way it stops the service - it's nothing like the others.
     * This is because the tournament server might be halfway through running a game.
     * It has to wait until the game finishes before it can shut itself down.
     *
     */
    public static void StopService()
    {
        if (service == null)
            return;

        service.MessageQueue().add(new TSMessage(TSMessageType.END));
        while (service.Alive())
        {
            try
            {
                Thread.sleep(200);
            }
            catch (Exception e)
            {
                // do nothing, haha!
            }
        }
        service = null;
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
        if (service != null)
            service.MessageQueue().add(new TSMessage(TSMessageType.THREAD_POOL_RESIZE, new_size));
    }


    /**
     * Nick Sifniotis u5809912
     * 13/10/2015
     *
     * Sends a message to the server asking it to create a viewing window for the game.
     *
     * @param tournament_id - the tournament to open a window for
     */
    public static void OpenWindow(GameViewer v, int tournament_id)
    {
        if (service != null)
            service.MessageQueue().add(new TSViewerMessage(v, tournament_id));
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * @return true if ths service is active. Note that the service may be null.
     */
    public static boolean Alive() { return (service != null) && service.Alive(); }
}
