package TournamentServer;


import java.util.Scanner;
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
        Scanner in = new Scanner (System.in);

        child.start();


        boolean finished = false;
        while (!finished)
        {
            System.out.println ("Command:");
            String command = in.nextLine();

            Hermes message = new Hermes ();
            message.message = command;

            messenger.add (message);

            if (command.equals("Q"))
                finished = true;
        }
    }

    public static void main(String[] args)
    {
        main_loop();
    }
}
