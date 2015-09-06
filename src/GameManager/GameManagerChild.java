package GameManager;

import AcademicsInterface.IGameEngine;
import Common.DataModel.Scores;
import GameManager.Exceptions.PlayerMoveException;

/**
 * Created by nsifniotis on 2/09/15.
 *
 * Child thread for executing one instance of a game.
 *
 * Requires one IGameEngine implementation and an array of
 * PlayerManager objects that correspond to the players of this game.
 *
 */
public class GameManagerChild extends Thread {

    public boolean finished;

    private int game_id;
    private IGameEngine game;
    private PlayerManager[] players;


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * The constructor.
     *
     * The parameters being fed through are not correct. We should be receiving some sort
     * of Game object record from the data model, not an already instantiated IGameEngine class
     * @TODO fix that
     * @param game
     * @param players
     */
    public GameManagerChild (int game_id, IGameEngine game, PlayerManager[] players)
    {
        this.game_id = game_id;
        this.game = game;
        this.players = players;
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
        finished = false;
        run_game();
        finished = true;
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

        Object game_state = game.InitialiseGame(players.length);
        Scores game_scores = new Scores (game_id, this.players);

        while (game.AreYouStillAlive(game_state) && game_scores.GameOn())
        {
            Object move = null;
            try
            {
                move = players[game.GetCurrentPlayer(game_state)].nextMove(game_state);
            }
            catch (PlayerMoveException e)
            {
                // these exceptions indicate that the player failed to return a move for some reason.
                // they have nothing to say about the validity of the move that was returned. That
                // is a separate test...

                game_scores.Disqualify(game.GetCurrentPlayer(game_state));

                finished = true;
                return;
            }

            // why would move be null? It's only null if nextMove chucks an exception
            // who knows. If there's a way to screw up, you can be sure Java will find it
            if (move == null)
            {
                game_scores.Disqualify(game.GetCurrentPlayer(game_state));

                finished = true;
                return;
            }

            // this is more like it
            // fuck you cheats
            if (!game.IsLegitimateMove(game_state, move))
            {
                game_scores.Disqualify(game.GetCurrentPlayer(game_state));

                finished = true;
                return;
            }


            // the move passed the threefold barriers
            // progress the game.
            game_state = game.MakeMove(game_state, move);
            game_scores.Update (game.ScoreGame(game_state));
        }

    }
}
