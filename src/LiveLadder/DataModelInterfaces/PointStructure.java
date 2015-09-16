package LiveLadder.DataModelInterfaces;

import Common.DataModel.Score;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 17/09/15.
 *
 * LiveLadder data model entity view contract for the PointStructure DMO.
 * This class holds an array of PointStructure objects, corresponding to
 * a tournament's entire structure.
 *
 * Long story short, the entire table is read only to the LiveLadder.
 */
public class PointStructure
{
    private Common.DataModel.PointStructure[] structure;


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Simple constructor.
     *
     * @param tournament_id - the tournament that we are scoring against
     */
    public PointStructure (int tournament_id)
    {
        structure = Common.DataModel.PointStructure.LoadAll(tournament_id);
        Arrays.sort(structure);
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Distribute points to the different players based on how they ranked in a game.
     *
     * @param teams - all the teams that competed in this tournament
     * @param scores - the results of the game
     */
    public void DistributePoints (HashMap<Integer, TeamDetails> teams, Common.DataModel.Score[] scores)
    {
        if (scores.length == 0)
            return;

        Arrays.sort(scores);
        if (scores[0].NoScore())
        {
            // no score mode. Sum the total points available and distribute them evenly to everybody
            // except the player responsible for taking the game down.
            int sum = 0;
            for (Common.DataModel.PointStructure p: structure)
                sum += p.Points();

            int dividend = sum / (scores.length - 1);    // rounded down because there should be no advantage here

            for (Score s: scores)
                if (!s.Disqualified())
                    teams.get(s.PrimaryKey()).AddPoints(dividend);
        }
        else
        {
            List<Integer> player_holding = new LinkedList<>();
            int cumulative = 0;
            int last_item_score = scores[0].Score();
            int counter = 0;

            for (Score s: scores)
            {
                if (s.Score() != last_item_score)
                {
                    // disburse the points
                    int dividend = cumulative / player_holding.size();
                    for (Integer i: player_holding)
                        teams.get(i).AddPoints(dividend);

                    cumulative = 0;
                    player_holding.clear();
                }

                // add the next item to the list
                last_item_score = s.Score();
                cumulative += structure[counter].Points();
                player_holding.add (s.SubmissionKey());
                counter ++;
            }

            // and finally disburse the last set of points
            int dividend = cumulative / player_holding.size();
            for (Integer i: player_holding)
                teams.get(i).AddPoints(dividend);
        }
    }
}
