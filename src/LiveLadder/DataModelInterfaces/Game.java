package LiveLadder.DataModelInterfaces;

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
        source_scores = item.
    }

}
