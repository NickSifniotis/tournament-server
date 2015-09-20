package LiveLadder.DataModelInterfaces;

import java.util.HashMap;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * Tournament data object view for the LiveLadder systems.
 *
 * View is defined as follows
 *
 * id                       prikey
 * name                     String
 * pointstructure           PointStructure instance -- ??
 *
 *
 */
public class Tournament
{
    private Common.DataModel.Tournament data_object;
    private PointStructure point_structure;

    public Tournament (Common.DataModel.Tournament item)
    {
        data_object = item;
        point_structure = new PointStructure(item.PrimaryKey());
    }


    /**
     * Nick Sifniotis u5809912
     * 18/09/2015
     *
     * Construct a tournament by ID
     *
     * @param tournament_id - the tournament to construct
     */
    public Tournament (int tournament_id)
    {
        this(new Common.DataModel.Tournament(tournament_id));
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Accessor functions
     *
     * @return the stuff being accessed.
     */
    public int PrimaryKey() { return data_object.PrimaryKey(); }
    public String Name() { return data_object.Name(); }
    public boolean IsOn() { return data_object.GameOn(); }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Gets all the tournaments!
     *
     * @return and then returns them
     */
    public static Tournament[] LoadAll ()
    {
        Common.DataModel.Tournament[] all = Common.DataModel.Tournament.LoadAll();
        Tournament[] res = new Tournament[all.length];

        for (int i = 0; i < all.length; i ++)
            res[i] = new Tournament(all[i]);

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Returns a string representation of this tournament
     * for use inside the choice box on the GUI.
     *
     * @return I just typed out what this returns I am not doing it again
     */
    @Override
    public String toString ()
    {
        String status = (data_object.GameOn()) ? "ON" : "OFF";
        return data_object.Name() + " (" + status + ")";
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Scoring a game is tournament level stuff, I suppose.
     *
     * @param game - the game to score
     * @param teams - the team manifest for this tournament
     */
    public void ScoreGame (Game game, HashMap<Integer, TeamDetails> teams)
    {
        // handle the for/against stuff first.
        game.DistributeScores(teams);

        // handle the points distribution second
        if (!game.InProgress())
            point_structure.DistributePoints(teams, game.GetScores());
    }
}
