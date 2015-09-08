package TournamentServer;

import AcademicsInterface.IGameEngine;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;

/**
 * Created by nsifniotis on 7/09/15.
 *
 * Just a testing class. Go away warnings.
 *
 */
public class tester {

    public static void main(String[] args)
    {

        // get the tournament data
        Tournament [] tourneys = Tournament.LoadAll();

        if (tourneys.length == 0)
            return;

        // create a game engine
        IGameEngine engine = tourneys[0].GameEngine();

        if (engine == null)
            return;


        // create some players
        PlayerSubmission player = new PlayerSubmission(15);
        PlayerManager [] game_players = new PlayerManager[4];
        for (int i = 0; i < 4; i ++)
            game_players[i] = new PlayerManager(tourneys[0], player);

        GameManagerChild child = new GameManagerChild(1, engine, game_players);

        child.run();
    }
}
