package GameManager;

import AcademicsInterface.IGameEngine;

/**
 * Created by nsifniotis on 2/09/15.
 */
public class GameManager {

    public void run_game (IGameEngine game, GameManager.PlayerManager[] players)
    {
        // fuck yeah, let's RUN THIS GAME

        Object game_state = game.initialise(players.length);

        while (game.alive(game_state))
        {
            Object move = players[game.current_player(game_state)].
        }

    }
}
