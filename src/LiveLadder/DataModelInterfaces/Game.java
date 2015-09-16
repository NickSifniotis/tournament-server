package LiveLadder.DataModelInterfaces;

import Common.DataModel.Score;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * The Game / Score DMO contract for the LiveLadder
 * The ladder is really only interested in scores.
 */
public class Game
{
    private Common.DataModel.Game source_game;
    private Common.DataModel.Score[] source_scores;


    public Game (Common.DataModel.Game item)
    {
        source_game = item;
        source_scores = item.GetScores();
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Returns a list of all games for the given tournament that are alive and aven't
     * been superceded by better players.
     *
     * @param tournament_id - the tournament to query
     * @return an array containing the Game objects.
     */

    public static Game[] LoadAll (int tournament_id)
    {
        int [] tourneys = { tournament_id };
        Common.DataModel.Game[] games = Common.DataModel.Game.LoadAll(tourneys, false);

        // filter out rubbish games
        List<Game> holding = new LinkedList<>();
        for (Common.DataModel.Game game: games)
            if (!game.Superceded() && game.Started())
                holding.add(new Game(game));

        Game[] res = new Game[holding.size()];
        return holding.toArray(res);
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Distribute the for and away scores to each of the teams that played this game.
     *
     * @param teams a hashmap of this tournaments teams, indexed by primary key
     */
    public void DistributeScores (HashMap<Integer, TeamDetails> teams)
    {
        int [] team_keys = new int[source_scores.length];
        for (int i = 0; i < source_scores.length; i++)
            team_keys[i] = source_scores[i].SubmissionKey();

        for (int i: team_keys)
            for (Score s: source_scores)
                if (s.SubmissionKey() == i)
                    teams.get(i).AddScores(s.Score(), 0);
                else
                    teams.get(i).AddScores(0, s.Score());
    }
}
