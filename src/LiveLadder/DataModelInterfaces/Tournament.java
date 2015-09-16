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

    public Tournament (Common.DataModel.Tournament item)
    {
        data_object = item;
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


    public void ScoreGame (int game_id, HashMap<Integer, TeamDetails> teams)
    {
        this.data_object.PointStructure().ScoreGame(game_id, teams);
    }
}
