package TournamentServer;

import Common.DataModel.Game;
import Common.DataModel.Tournament;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * This thread basically runs the tournaments.
 *
 */
public class TournamentThread extends Thread
{
    private BlockingQueue<Hermes> winged_messenger;

    public TournamentThread ()
    {
        winged_messenger = new LinkedBlockingQueue<>();
    }


    public BlockingQueue<Hermes> GetHermes()
    {
        return this.winged_messenger;
    }


    @Override
    public void run ()
    {
        boolean finished = false;
        while (!finished)
        {
            // load the current state of the tournaments, and all playable games connected to them.
            Tournament[] tournaments = Tournament.LoadAll(true);
            Game[] games = Game.LoadAll(tournaments, true);


            // those that are going - play some games.
            for (Game g: games)
                    System.out.println (g.PrimaryKey());


            // have we received any messages from the gods?
            Hermes message = winged_messenger.peek();
            if (message != null)
            {
                // pop the message off the queue
                try
                {
                    message = winged_messenger.take();
                }
                catch (Exception e)
                { /* fuck off, I flat out have no idea what to do here. */ }

                // deal with the message
                if (message.message.equals("Q"))
                    finished = true;
            }

            // go to sleep
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            { /* Ignore as we will try sleep again */}
        }
    }
}
