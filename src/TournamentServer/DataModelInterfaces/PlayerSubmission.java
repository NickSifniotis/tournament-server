package TournamentServer.DataModelInterfaces;


import java.io.File;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * PlayerSubmission data model view contract for the tournament server.
 * If I don't get this right, those infernal concurrency 'artifacts' that I have
 * been struggling to kill off for the last two days will be back, and in greater numbers.
 *
 * No pressure or anything.
 *
 * id                   prikey int autoinc
 * tournament_id        int             TS readable
 * team_name            string          TS readable
 * team_email           string          TS readable
 * team_avatar          string          TS readable
 * playing              boolean         TS writable    def 0
 * disqualified         boolean         TS writable    def 0
 * retired              boolean         TS readable    def 0
 */
public class PlayerSubmission
{
    private Common.DataModel.PlayerSubmission data_object;

    public PlayerSubmission (Common.DataModel.PlayerSubmission item)
    {
        data_object = item;
    }

    public int PrimaryKey() { return data_object.PrimaryKey(); }
    public String Name() { return data_object.Name(); }
    public String Email() { return data_object.Email(); }
    public File Picture() { return data_object.Avatar(); }
    public boolean Playing() { return data_object.Playing(); }
    public boolean Retired() { return data_object.Retired();}
    public boolean Disqualified() { return data_object.Disqualified(); }
    public boolean ReadyToPlay() { return data_object.ReadyToPlay(); }
    public String MarshalledSource() { return data_object.MarshalledSource(); }

    public void StartGame() { data_object.StartingGame(); }
    public void EndGame(boolean disqualified) throws Exception { data_object.EndingGame(disqualified); }
}
