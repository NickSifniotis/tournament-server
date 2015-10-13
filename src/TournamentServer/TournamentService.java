package TournamentServer;

import AcademicsInterface.IViewer;
import Common.LogManager;
import Services.GameViewer.GameViewer;
import Services.Logs.LogType;
import Services.Messages.Message;
import Services.Messages.TSMessage;
import Services.Messages.TSViewerMessage;
import Services.Service;
import TournamentServer.DataModelInterfaces.*;
import javafx.stage.Stage;

import java.util.HashMap;


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
public class TournamentService extends Service
{
    private HashMap<Integer, GameViewer> game_windows;
    private GameManagerChild[] thread_pool;
    private int thread_pool_target;
    private int running_threads;
    private boolean user_signalled_shutdown;


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Default constructor, build with minimal thread pool size.
     */
    public TournamentService()
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
    public TournamentService(int thread_pool_target)
    {
        this.game_windows = new HashMap<>();
        this.thread_pool_target = thread_pool_target;
        this.thread_pool = new GameManagerChild[thread_pool_target];
        this.user_signalled_shutdown = false;
        this.running_threads = 0;
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Handle a multitude of different messages that the tournament server
     * needs to know how to handle.
     *
     * @param message - the message that they have to handle.
     */
    @Override
    public void handle_message(Message message)
    {
        if (!(message instanceof TSMessage))
            return;

        if (message instanceof TSViewerMessage)
        {
            TSViewerMessage msg = (TSViewerMessage) message;
            int tourney = msg.payload;
            GameViewer viewer = msg.the_stage;
            game_windows.put(tourney, viewer);
        }


        TSMessage msg = (TSMessage) message;

        switch (msg.message)
        {
            case END:
                user_signalled_shutdown = true;
                break;
            case KILL_GAME:
                this.abort_game(msg.payload);
                break;
            case KILL_TOURNAMENT:
                this.abort_tournament(msg.payload);
                break;
            case THREAD_POOL_RESIZE:
                this.thread_pool_target = msg.payload;
                if (this.thread_pool_target > this.thread_pool.length)
                {
                    GameManagerChild [] new_array = new GameManagerChild[this.thread_pool_target];
                    System.arraycopy(thread_pool, 0, new_array, 0, thread_pool.length);
                    thread_pool = new_array;
                }
                break;
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Finally, one of these that does something!
     */
    @Override
    public void do_service()
    {
        // clear out any games from the pool that have finished.
        this.clear_dead_threads();

        // start a new game, if there is room in the thread pool.
        this.launch_new_threads();

        if (this.user_signalled_shutdown && this.running_threads == 0)
            this.alive = false;
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
            int[] tournament_keys = Tournament.LoadKeys();
            Game[] games = Game.LoadAll(tournament_keys);
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
        Tournament tournament = new Tournament(game.TournamentKey());
        LogManager.Log(LogType.TOURNAMENT, "Attempting to launch game " + game.PrimaryKey() + " in tournament " + tournament.Name());
        PlayerSubmission[] players = game.Players();

        // games only launch one at a time. So it's fair to assume that all players that are ready
        // to play now will still be ready to play in a couple of milliseconds.
        boolean lets_play = true;
        for (PlayerSubmission player: players)
                lets_play &= player.ReadyToPlay();

        if (!lets_play || players.length != tournament.NumPlayers())
        {
            LogManager.Log(LogType.TOURNAMENT, "Failed to launch game - not enough players reporting ready: " + lets_play + ":" + players.length);
            return false;
        }


        PlayerManager[] player_managers = new PlayerManager[players.length];
        try
        {
            for (int i = 0; i < players.length; i++)
            {
                players[i].StartGame();
                player_managers[i] = new PlayerManager(tournament, players[i]);
            }
        }
        catch (Exception e)
        {
            String error = "Error launching game " + game.PrimaryKey() + ". " + e;
            LogManager.Log(LogType.ERROR, error);
            return false;
        }

        thread_pool[thread] = new GameManagerChild(game, tournament.GameEngine(), player_managers, tournament.UseNullMoves());

        // does this tournament use a viewer? Connect the game to it.
        if (tournament.UsesViewer())
        {
            GameViewer current_stage = this.game_windows.get(tournament.PrimaryKey());
            if (current_stage != null)
            {
                IViewer viewer = tournament.Viewer();
                current_stage.SetViewer(viewer);
            }
        }

        thread_pool[thread].start();

        LogManager.Log(LogType.TOURNAMENT, "Game started!");
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

        LogManager.Log(LogType.TOURNAMENT, "Attempting to end game " + game_thread.Game().PrimaryKey());

        try
        {
            for (int i = 0; i < game_players.length; i++)
                game_players[i].EndGame(game_scores.Disqualified(i));

            game_thread.Game().EndGame();
        }
        catch (Exception e)
        {
            String error = "Error terminating game + " + game_thread.Game().PrimaryKey() + ". " + e;
            LogManager.Log(LogType.TOURNAMENT, error);
        }

        thread_pool[thread] = null;

        LogManager.Log(LogType.TOURNAMENT, "Termination successful.");
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

        LogManager.Log(LogType.TOURNAMENT, "Reset of tournament " + tournament_id + " successful!");
    }
}
