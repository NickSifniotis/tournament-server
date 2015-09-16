package TournamentServer;

import Common.DataModel.Game;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Scores;
import Common.DataModel.Tournament;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import Common.SystemState;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * This thread basically runs the tournaments.
 *
 * It's trickier than it seems to write this code. Games are spawned off as child
 * threads, so this thread cannot terminate (in the event of a user shutdown) until
 * all child threads have terminated - until all games have finished, in other words.
 *
 */
public class TournamentThread extends Thread
{
    private BlockingQueue<Hermes> winged_messenger;
    private GameManagerChild[] thread_pool;
    private int thread_pool_target;
    private int running_threads;
    private boolean user_signalled_shutdown;
    private boolean finished;


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
        this.user_signalled_shutdown = false;
        this.running_threads = 0;
        this.finished = false;
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


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Concurrency is a major pain in my arse.
     *
     */
    @Override
    public void run ()
    {
        finished = false;

        while (!this.user_signalled_shutdown || this.running_threads > 0)
        {
            // clear out any games from the pool that have finished.
            this.clear_dead_threads();

            // start a new game, if there is room in the thread pool.
            this.launch_new_threads();

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

                switch (message.message)
                {
                    case END:
                        user_signalled_shutdown = true;
                        break;
                    case KILL_GAME:
                        this.abort_game(message.payload);
                        break;
                    case KILL_TOURNAMENT:
                        this.abort_tournament(message.payload);
                        break;
                    case INC_THREAD_POOL:
                        this.thread_pool_target ++;
                        if (this.thread_pool_target > this.thread_pool.length)
                        {
                            GameManagerChild [] new_array = new GameManagerChild[this.thread_pool_target];
                            System.arraycopy(thread_pool, 0, new_array, 0, thread_pool.length);
                            thread_pool = new_array;
                        }
                        break;
                    case DEC_THREAD_POOL:
                        if (this.thread_pool_target > 1)
                            this.thread_pool_target--;
                        break;
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

        finished = true;
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Searches through the pool of game manager threads, and clears out the dead ones.
     *
     */
    private void clear_dead_threads()
    {
        for (int i = 0; i < thread_pool.length; i ++)
            if (thread_pool[i] != null)
                if (thread_pool[i].Finished())
                {
                    this.end_game(i);
                }
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * If there is room in the thread pool, attempt to launch another game.
     *
     */
    private void launch_new_threads ()
    {
        // count how many child threads are still running.
        this.running_threads = 0;
        int available_spot = -1;

        for (int i = 0; i < thread_pool.length; i ++)
        {
            this.running_threads += (thread_pool[i] == null) ? 0 : 1;
            if (thread_pool[i] == null)
                available_spot = i;
        }

        // do not spawn any new games if the user has indicated that they want a shutdown
        if (this.user_signalled_shutdown)
            return;

        // there's no point in trying to find new games if the thread pool is all full
        if (this.running_threads < thread_pool_target && available_spot != -1)
        {
            // load the current state of the tournaments, and all playable games connected to them.
            Tournament[] tournaments = Tournament.LoadAll(true);
            int[] keys = new int[tournaments.length];
            for (int i = 0; i < tournaments.length; i++)
                keys[i] = tournaments[i].PrimaryKey();

            Game[] games = Game.LoadAll(keys, true);
            int game_counter = 0;

            boolean success = false;
            while (!success && game_counter < games.length)
            {
                // let's play a game
                success = launch_game(games[game_counter], available_spot);
                game_counter ++;
            }
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
     * @return - returns true if the launch was successful, false otherwise
     */
    private boolean launch_game (Game game, int thread)
    {
        LogManager.Log (LogType.TOURNAMENT, "Attempting to launch game " + game.PrimaryKey() + " in tournament " + game.Tournament().Name());

        PlayerSubmission[] players = game.GetPlayers();

        // games only launch one at a time. So it's fair to assume that all players that are ready
        // to play now will still be ready to play in a couple of milliseconds.
        boolean lets_play = true;
        for (PlayerSubmission player: players)
                lets_play &= player.ReadyToPlay();

        if (!lets_play || players.length != game.Tournament().NumPlayers())
        {
            LogManager.Log (LogType.TOURNAMENT, "Failed to launch game - not enough players reporting ready.");
            return false;
        }


        PlayerManager[] player_managers = new PlayerManager[players.length];
        try
        {
            for (int i = 0; i < players.length; i++)
            {
                players[i].StartingGame();
                player_managers[i] = new PlayerManager(game.Tournament(), players[i]);
            }
        }
        catch (Exception e)
        {
            String error = "Error launching game " + game.PrimaryKey() + ". " + e;
            LogManager.Log(LogType.ERROR, error);
            return false;
        }

        thread_pool[thread] = new GameManagerChild(game, game.Tournament().GameEngine(), player_managers);
        thread_pool[thread].start();

        LogManager.Log (LogType.TOURNAMENT, "Game started!");
        return true;
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * The game has ended, so release the player submission and game records.
     * If a player has been disqualified, make sure that the playersub record reflects that.
     *
     * @param thread - which thread in the thread pool has terminated.
     */
    private void end_game (int thread)
    {
        GameManagerChild game_thread = thread_pool[thread];
        PlayerManager[] game_players = game_thread.Players();
        Scores game_scores = game_thread.Scores();

        LogManager.Log (LogType.TOURNAMENT, "Attempting to end game " + game_thread.Game().PrimaryKey());

        try
        {
            for (int i = 0; i < game_players.length; i++)
                game_players[i].EndingGame(game_scores.Disqualified(i));

            game_thread.Game().EndGame();
        }
        catch (Exception e)
        {
            String error = "Error terminating game + " + game_thread.Game().PrimaryKey() + ". " + e;
            LogManager.Log (LogType.TOURNAMENT, error);
        }

        thread_pool[thread] = null;

        LogManager.Log (LogType.TOURNAMENT, "Termination successful.");
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * Sends a signal to abort the game.
     *
     */
    private void abort_game(int game_id)
    {
        LogManager.Log(LogType.TOURNAMENT, "Attempting to abort game " + game_id);

        for (GameManagerChild gm: thread_pool)
            if (gm.Game().PrimaryKey() == game_id)
                gm.Abort();

        LogManager.Log(LogType.TOURNAMENT, "Abortion of game " + game_id + " finished.");
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * Shuts down a tournament, and seeks out any active games being played
     * for that tournament and kills them off too.
     *
     * @param tournament_id - the tournament to shut down
     */
    private void abort_tournament (int tournament_id)
    {
        LogManager.Log(LogType.TOURNAMENT, "Attempting to reset tournament " + tournament_id);

        Tournament t = new Tournament(tournament_id);
        t.ResetTournament();

        LogManager.Log(LogType.TOURNAMENT, "Supercede of tournament " + tournament_id + " successful!");
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Accessor functions
     *
     * @return the data that the user wants.
     */
    public boolean Finished () { return this.finished; }
}
