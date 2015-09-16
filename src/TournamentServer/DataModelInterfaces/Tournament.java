package TournamentServer.DataModelInterfaces;

import AcademicsInterface.IPlayer;

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
     * @return an array of all Tournaments that are currently active
     */
    public static Tournament[] LoadAll ()
    {
        Common.DataModel.Tournament[] all = Common.DataModel.Tournament.LoadAll(true);
        Tournament[] res = new Tournament[all.length];
        for (int i = 0; i < all.length; i ++)
            res[i] = new Tournament(all[i]);

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Various accessor functions
     * @return the requested data.
     */
    public int PrimaryKey () { return data_object.PrimaryKey(); }
    public IPlayer PlayerInterface() { return data_object.PlayerInterface(); }
    public int Timeout() { return data_object.Timeout(); }
    public int NumPlayers() { return data_object.NumPlayers(); }


    public void ResetTournament () { data_object.ResetTournament(); }
}
