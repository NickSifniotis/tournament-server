package Common.DataModel;

import Common.SystemState;
import LiveLadder.TeamDetails;

import java.util.HashMap;

/**
 * Created by nsifniotis on 12/09/15.
 */
public class PointStructure
{
    private int num_players;
    private int [] points;


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Create the ladder's point scoring system per the database records.
     * If no tournament ID is given, have a sook.
     *
     * @param tournament - the tournament who's point scoring structure to load.
     */
    public PointStructure (Tournament tournament)
    {
        this.num_players = tournament.NumPlayers();

        if (tournament.PrimaryKey() > 0)
        {
            String query = "SELECT * FROM point_structure WHERE tournament_id = " + tournament.PrimaryKey();


        }
        else
        {

        }
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Score this game. Compute points and pass them on to the TeamStructures that own them.
     * Add the ScoresFor and ScoreAgainst as well.
     *
     * @param g - the game whos results are being analysed
     * @param teams - a hashmap (key = submission prikey) of all the players in this tournament
     */
    public void ScoreGame (Game g, HashMap<Integer, TeamDetails> teams)
    {
        Scores game_scores;
        try
        {
            game_scores = new Scores(g.PrimaryKey(), num_players);
        }
        catch(Exception e)
        {
            String error = "PointStructure.ScoreGame - Error trying to score game " + g.PrimaryKey() + ": " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return;
        }

        PlayerSubmission[] players = g.GetPlayers();

        try
        {
            for (PlayerSubmission p : players)
            {
                TeamDetails deets = teams.get(p.PrimaryKey());
                deets.AddScores(game_scores.ScoreFor(p.PrimaryKey()), game_scores.ScoreAgainst(p.PrimaryKey()));
            }
        }
        catch (Exception e)
        {
            // a lot of these try/catch blocks are being used to catch impossible situations.
            // this is one of those.

            String error = "PointStructure.ScoreGame - Error trying to scorefor/against game " + g.PrimaryKey() + ": " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return;
        }

        // use the points system to calculate points and things.
    }
}
