package Common.DataModel;

import Common.Logs.LogManager;
import Common.Logs.LogType;
import LiveLadder.TeamDetails;

import java.util.HashMap;

/**
 * Created by nsifniotis on 12/09/15.
 *
 * This class holds the points model used by the tournament scoring system.
 * That is to say, how many points you get for coming first, or second, or third
 * or whatever. Also what to do in the event of a draw.
 *
 * It contains methods that will score a game and add the data directly into the TeamDetails
 * objects. This allows the LiveLadder to palm off the scoring task to this object.
 *
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
            LogManager.Log(LogType.ERROR, error);

            return;
        }

        PlayerSubmission[] players = g.GetPlayers();
        for (PlayerSubmission p : players)
        {
            int pri_key = p.PrimaryKey();
            TeamDetails deets = teams.get(pri_key);

            // by shifting the try/catch block inside this loop,
            // one missing score will not be enough to take down the entire game record.
            try
            {
                deets.AddScores(game_scores.ScoreFor(pri_key), game_scores.ScoreAgainst(pri_key));

                if (g.InProgress())
                    deets.SetPlayingNow();
            }
            catch (Exception e)
            {
                // a lot of these try/catch blocks are being used to catch impossible situations.
                // this is one of those.

                // 14/09/2015 - this 'impossible situation' was triggered a number of times last night
                // and I wasted two hours trying to debug it

                String error = "PointStructure.ScoreGame - Error trying to scorefor/against game " + g.PrimaryKey() + ": " + e;
                LogManager.Log(LogType.ERROR, error);
            }
        }


        // use the points system to calculate points and things.
    }
}
