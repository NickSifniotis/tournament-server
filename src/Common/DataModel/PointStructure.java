package Common.DataModel;

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
     * @param tournament_id - the tournament who's point scoring structure to load.
     */
    public PointStructure (int tournament_id)
    {
        if (tournament_id > 0)
        {
            String query = "SELECT * FROM point_structure WHERE tournament_id = " + tournament_id;


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

    }
}
