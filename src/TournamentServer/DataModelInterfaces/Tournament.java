package TournamentServer.DataModelInterfaces;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IPlayer;
import AcademicsInterface.IViewer;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * The data object view contract thing for the Tournament entity for the TS
 *
 * id                       prikey autoinc
 * PlayerInterface          instance of IPlayer
 * timeout                  int
 *
 */
public class Tournament
{
    private Common.DataModel.Tournament data_object;

    public Tournament (Common.DataModel.Tournament item)
    {
        data_object = item;
    }


    public Tournament (int tournament_id)
    {
        data_object = new Common.DataModel.Tournament(tournament_id);
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Wrapper method for the LoadAll method
     *
     * @param active_only - TRUE if we only want active tournaments, FALSE if we want them all.
     * @return an array of all Tournaments
     */
    public static Tournament[] LoadAll (boolean active_only)
    {
        Common.DataModel.Tournament[] all = Common.DataModel.Tournament.LoadAll(active_only);
        Tournament[] res = new Tournament[all.length];
        for (int i = 0; i < all.length; i ++)
            res[i] = new Tournament(all[i]);

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * We only want the keys! The fucking keys!!
     *
     * @return an array of primary keys.
     */
    public static int[] LoadKeys ()
    {
        Tournament[] all = LoadAll(true);
        int[] keys = new int[all.length];
        for (int i = 0; i < all.length; i ++)
            keys[i] = all[i].PrimaryKey();

        return keys;
    }

    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Various accessor functions
     * @return the requested data.
     */
    public int PrimaryKey () { return data_object.PrimaryKey(); }
    public String Name() { return data_object.Name(); }
    public IPlayer PlayerInterface() { return data_object.PlayerInterface(); }
    public IGameEngine GameEngine() { return data_object.GameEngine(); }
    public IViewer Viewer() { return data_object.Viewer(); }
    public boolean UsesViewer() { return data_object.UsesViewer(); }
    public int Timeout() { return data_object.Timeout(); }
    public int NumPlayers() { return data_object.NumPlayers(); }
    public boolean UseNullMoves() { return data_object.UsesNullMoves(); }


    public void ResetTournament () { data_object.ResetTournament(); }
    public void StartTournament () { data_object.StartTournament(); }
    public void StopTournament () { data_object.StopTournament(); }


    @Override
    public String toString()
    {
        return data_object.Name();
    }
}
