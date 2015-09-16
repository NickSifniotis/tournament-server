package TournamentServer.DataModelInterfaces;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * The Game entity contract for TournamentServer.
 * This is one half of the tricky concurrency issue - the other one is PlayerSubmission.
 * Get this right and the server will tick over nicely.
 * Get this wrong and you might have to abandon your coding ambitions.
 *
 *
 */
public class Game
{
    private Common.DataModel.Game data_object;


    public Game (Common.DataModel.Game item)
    {
        data_object = item;
    }

    public int PrimaryKey() { return data_object.PrimaryKey(); }
    public void StartGame() { data_object.StartGame(); }
    public void Terminate() { data_object.Terminate(); }
}
