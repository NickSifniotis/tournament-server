package TournamentServer;

import Common.DataModel.Game;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;

import java.util.Random;
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
    private GameManagerChild[] thread_pool;
    private int thread_pool_target;


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Default constructor, build with minimal thread pool size.
     */
    public TournamentThread ()
    {
        this(1);
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Constructor with specified thread pool target size.
     * The thread pool target size is the number of live threads that
     * the server will try to keep alive at any one point in time.
     *
     * In other words, how many games will the server attempt to play at once?
     *
     * @param thread_pool_target - how many games will be played simultaneously
     */
    public TournamentThread (int thread_pool_target)
    {
        this.winged_messenger = new LinkedBlockingQueue<>();
        this.thread_pool_target = thread_pool_target;
        this.thread_pool = new GameManagerChild[thread_pool_target];
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Full credit to stackExchange for giving me this idea.
     * The best way to pass commands from the console to the tournament server is
     * through the use of something called a BlockingQueue.
     *
     * @return our winged friend.
     */
    public BlockingQueue<Hermes> GetHermes()
    {
        return this.winged_messenger;
    }


    @Override
    public void run ()
    {
        Random randomiser = new Random();
        boolean finished = false;

        while (!finished)
        {
            // there's no point in trying to find new games if the thread pool is all full
            int running_games = 0;
            int available_spot = -1;

            for (int i = 0; i < thread_pool.length; i ++)
            {
                running_games += (thread_pool[i] == null) ? 0 : 1;
                if (thread_pool[i] == null)
                    available_spot = i;
            }

            if (running_games < thread_pool_target && available_spot != -1)
            {
                // load the current state of the tournaments, and all playable games connected to them.
                Tournament[] tournaments = Tournament.LoadAll(true);
                Game[] games = Game.LoadAll(tournaments, true);

                // those that are going - play some games.
                int game_to_play = randomiser.nextInt(games.length);

                launch_game(games[game_to_play], available_spot);
            }


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

                if (message.message.equals("THREAD_POOL"))
                {
                    int new_target = Integer.parseInt(message.payload);
                    if (new_target > 0)
                    {
                        // if it's less, don't worry about it. But if it's more, grow the array.
                        if (new_target > thread_pool_target)
                        {
                            GameManagerChild [] new_array = new GameManagerChild[new_target];
                            for (int i = 0; i < thread_pool.length; i ++)
                                new_array[i] = thread_pool[i];

                            thread_pool = new_array;
                        }
                        thread_pool_target = new_target;
                    }
                }
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


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Spawns a new game manager child thread and adds it to the thread pool.
     *
     * @param game - the game object to launch.
     * @param thread - which spot in the pool the new thread is to occupy.
     */
    private void launch_game (Game game, int thread)
    {
        PlayerManager[] players = null;
        thread_pool[thread] = new GameManagerChild(game, game.Tournament().GameEngine(), players);
    }
}
