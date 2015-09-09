package TournamentServer;

import java.util.concurrent.BlockingQueue;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * The main application in this suite - at long last, it's written!
 */
public class TournamentServer
{

    private static void main_loop ()
    {
        // begin by firing up the child process that manages all the game work.
        TournamentThread child = new TournamentThread();
        BlockingQueue <Hermes> messenger = child.GetHermes();

        child.run();



    }

    public static void main(String[] args)
    {
        main_loop();
    }
}
