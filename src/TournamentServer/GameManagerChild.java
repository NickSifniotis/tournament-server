package TournamentServer;

import AcademicsInterface.IGameEngine;
import Common.Email.EmailTypes;
import Common.Email.Emailer;
import Services.LogService;
import TournamentServer.DataModelInterfaces.Game;
import TournamentServer.DataModelInterfaces.Scores;
import Services.Logs.LogType;
import TournamentServer.Exceptions.PlayerMoveException;

/**
 * Created by nsifniotis on 2/09/15.
 *
 * Child thread for executing one instance of a engine.
 *
 * Requires one IGameEngine implementation and an array of
 * PlayerManager objects that correspond to the players of this engine.
 *
 */
public class GameManagerChild extends Thread
{
    private boolean finished;
    private boolean aborted;

    private Scores game_scores;
    private Game game;
    private IGameEngine engine;
    private boolean use_nulls;
    private PlayerManager[] players;


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * The constructor.
     *
     * @param game - the game that is being played
     * @param engine - the game engine that will be used to drive it
     * @param players - the players who will be competing in this game.
     */
    public GameManagerChild (Game game, IGameEngine engine, PlayerManager[] players, boolean use_nulls)
    {
        this.game = game;
        this.engine = engine;
        this.players = players;
        this.use_nulls = use_nulls;

        this.game_scores = new Scores(game.PrimaryKey(), players);

        try
        {
            this.game.StartGame();

            for (int i = 0; i < players.length; i ++)
                LogService.GameLog(game.PrimaryKey(), "Player " + i + ": " + players[i].Name());
        }
        catch (Exception e)
        {
            String error = "GameManagerChild.constructor - unable to start game " + this.game.PrimaryKey();
            LogService.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Override the Thread run method
     *
     * 10/10 useless comment
     *
     */
    @Override
    public void run ()
    {
        this.finished = false;
        this.aborted = false;

        this.run_game();

        this.finished = true;
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Ok, this is the more interesting run method.
     *
     */
    private void run_game ()
    {
        // fuck yeah, let's RUN THIS GAME

        Object game_state = engine.InitialiseGame(players.length);

        while (engine.AreYouStillAlive(game_state) && game_scores.GameOn() && !this.aborted)
        {
            int current_player = engine.GetCurrentPlayer(game_state);
            Object move;
            try
            {
                move = players[current_player].nextMove(game_state);
            }
            catch (PlayerMoveException e)
            {
                // these exceptions indicate that the player failed to return a move for some reason.
                // they have nothing to say about the validity of the move that was returned. That
                // is a separate test...
                if (this.use_nulls)
                {
                    move = players[current_player].NullMove();
                    LogService.GameLog(game.PrimaryKey(), "Player " + current_player + " threw a PlayerMoveException. Skipping turn.");
                }
                else
                {
                    // log what the fuck has happened as well.
                    LogService.Log(LogType.ERROR, "GameManagerChild.run game - something went wrong with player " + current_player + "'s move: " + e);
                    LogService.Log(LogType.TOURNAMENT, "Disqualifying player " + current_player + " for throwing a PlayerMoveException.");
                    LogService.GameLog(game.PrimaryKey(), "Player " + current_player + " disqualified - failed to return a move.");

                    game_scores.Disqualify(current_player);

                    finished = true;
                    return;
                }
            }

            // why would move be null? It's only null if nextMove chucks an exception
            // who knows. If there's a way to screw up, you can be sure Java will find it
            if (move == null)
            {
                if (this.use_nulls)
                {
                    move = players[current_player].NullMove();
                    LogService.GameLog(game.PrimaryKey(), "Player " + current_player + " returned a null move. Skipping turn.");
                }
                else
                {
                    LogService.Log(LogType.TOURNAMENT, "Disqualifying player " + current_player + " for returning a move that is null.");
                    LogService.GameLog(game.PrimaryKey(), "Player " + current_player + " disqualified - returned a null move.");
                    game_scores.Disqualify(current_player);

                    finished = true;
                    return;
                }

            }

            // this is more like it
            // fuck you cheats
            if (!engine.IsLegitimateMove(game_state, move))
            {
                if (this.use_nulls)
                {
                    move = players[current_player].NullMove();
                    LogService.GameLog(game.PrimaryKey(), "Player " + current_player + " returned an illegal move: " + move + ". Skipping turn.");
                }
                else
                {
                    LogService.Log(LogType.TOURNAMENT, "Disqualifying player " + current_player + " for returning a move that is not legit.");
                    LogService.GameLog(game.PrimaryKey(), "Player " + current_player + " disqualified - returned a bad move: " + move);
                    Disqualify(current_player);

                    finished = true;
                    return;
                }
            }


            // the move passed the threefold barriers
            // progress the engine.
            LogService.GameLog(game.PrimaryKey(), engine.LogEntry(game_state, move));
            game_state = engine.MakeMove(game_state, move);
            game_scores.Update (engine.ScoreGame(game_state));
        }

        // and send out EMAILS ABOUT IT
        for (PlayerManager p: players)
            Emailer.SendEmail(EmailTypes.GAME_OVER, p.Email(),
                this.game.TournamentKey(), LogService.GameLogFilename(this.game.PrimaryKey()));
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Disqualify a player.
     *
     * @param player_id - the player whom to disqualify
     */
    private void Disqualify (int player_id)
    {
        for (int i = 0; i < players.length; i ++)
            Emailer.SendEmail(((i == player_id) ? EmailTypes.DISQUALIFIED : EmailTypes.ABNORMAL),
                    players[player_id].Email(),
                    this.game.TournamentKey(),
                    LogService.GameLogFilename(this.game.PrimaryKey()));

        game_scores.Disqualify(player_id);
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Various accessor functions.
     *
     * @return whatever data was requested.
     */
    public Game Game() { return this.game; }
    public Scores Scores() { return this.game_scores; }
    public PlayerManager[] Players () { return this.players; }
    public boolean Finished () { return this.finished; }
    public void Abort () { this.aborted = true; this.Game().Terminate(); }
}
