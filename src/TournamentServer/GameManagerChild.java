package TournamentServer;

import AcademicsInterface.IGameEngine;
import Common.DataModel.Game;
import Common.DataModel.Scores;
import Common.Logs.LogManager;
import Common.Logs.LogType;
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
    public GameManagerChild (Game game, IGameEngine engine, PlayerManager[] players)
    {
        this.game = game;
        this.engine = engine;
        this.players = players;

        this.game_scores = new Scores(game, players);

        try
        {
            this.game.StartGame();
        }
        catch (Exception e)
        {
            String error = "GameManagerChild.constructor - unable to start game " + this.game.PrimaryKey();
            LogManager.Log(LogType.ERROR, error);
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
            Object move;
            try
            {
                move = players[engine.GetCurrentPlayer(game_state)].nextMove(game_state);
            }
            catch (PlayerMoveException e)
            {
                // these exceptions indicate that the player failed to return a move for some reason.
                // they have nothing to say about the validity of the move that was returned. That
                // is a separate test...
//@TODO: Add 'null move' rule option to tournament, implement. The null move function belongs in IPlayer

                // log what the fuck has happened as well.
                LogManager.Log (LogType.ERROR, "GameManagerChild.run game - something went wrong with a player move: " + e);
                LogManager.Log (LogType.TOURNAMENT, "Disqualifying below player for throwing a PlayerMoveException.");

                game_scores.Disqualify(engine.GetCurrentPlayer(game_state));

                finished = true;
                return;
            }

            // why would move be null? It's only null if nextMove chucks an exception
            // who knows. If there's a way to screw up, you can be sure Java will find it
            if (move == null)
            {
                LogManager.Log (LogType.TOURNAMENT, "Disqualifying below player for returning a move that is null.");
                game_scores.Disqualify(engine.GetCurrentPlayer(game_state));

                finished = true;
                return;
            }

            // this is more like it
            // fuck you cheats
            if (!engine.IsLegitimateMove(game_state, move))
            {
                LogManager.Log (LogType.TOURNAMENT, "Disqualifying below player for returning a move that is not legit.");
                game_scores.Disqualify(engine.GetCurrentPlayer(game_state));

                finished = true;
                return;
            }


            // the move passed the threefold barriers
            // progress the engine.
            //@TODO: Log the move into the game logs.
            game_state = engine.MakeMove(game_state, move);
            game_scores.Update (engine.ScoreGame(game_state));
        }
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
