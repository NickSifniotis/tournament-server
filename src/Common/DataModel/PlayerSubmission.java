package Common.DataModel;

import Common.DBManager;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import Common.SystemState;
import AcademicsInterface.SubmissionMetadata;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * This class represents one player submission for a tournament.
 * As well as holding the data, this class interfaces with the database layer
 * to load and save its own state (as directed ..)
 *
 * Database schema
 * id                   prikey int                                      auto_inc
 * tournament_id        int             PM writable, TS LL readable
 * team_name            varchar         PM writable, TS LL readable
 * team_email           varchar         PM writable, TS LL readable
 * team_avatar          boolean         PM writable, TS LL readable
 * playing              boolean         TS writable, LL readable        def 0
 * disqualified         boolean         TS writable, LL readable        def 0
 * retired              boolean         PM writable, TS LL readable     def 0
 *
 */
public class PlayerSubmission extends Entity
{
    private int tournament_id;

    private String name;
    private String email;
    private boolean uses_avatar;
    private boolean playing;
    private boolean disqualified;
    private boolean retired;


    @Override
    protected String table_name() {
        return "submission";
    }

    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Default blank constructor.
     *
     */
    public PlayerSubmission()
    {
        this.loadState();
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Constructor - the flag specifies whether to create a blank entry in the DB and
     * extract the new prikey, or, whether to simply create a blank object.
     */
    public PlayerSubmission (boolean create_database)
    {
        this();

        if (create_database)
            saveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Construct using the current record in the provided database recordset.
     * This is the way that most of these objects will be created.
     *
     * @param input - the DB recordset that points to the object that we want to create
     * @throws SQLException - because what the fuck
     */
    public PlayerSubmission (ResultSet input) throws SQLException
    {
        this.loadState(input);
    }


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * Construct the player object by retrieving from the database the record
     * that corresponds to the player_id that has been provided.
     *
     * If the player_id corresponds to no record, then construct the default new player.
     *
     * @param player_id - the record to retrieve.
     */
    public PlayerSubmission (int player_id)
    {
        String query;

        if (player_id != 0) {
            query = "SELECT * FROM submission WHERE id = " + player_id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                try
                {
                    res.next();
                    this.loadState(res);
                    DBManager.disconnect(res);          // disconnect by result
                }
                catch (Exception e)
                {
                    String error = "PlayerSubmission constructor (player_id) - SQL error retrieving player data. " + e;
                    LogManager.Log(LogType.ERROR, error);

                    this.loadState();
                    DBManager.disconnect(connection);
                }
            }
            else
            {
                this.loadState();
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }

    }


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * Load this players details from the database recordset provided.
     *
     * @param input - the recordset from the database.
     * @throws SQLException if something goes wrong with the query.
     */
    private void loadState (ResultSet input) throws SQLException
    {
        this.id = input.getInt ("id");
        this.name = input.getString("team_name");
        this.email = input.getString("team_email");
        this.uses_avatar = (input.getInt("team_avatar") == 1);
        this.tournament_id = input.getInt ("tournament_id");
        this.retired = (input.getInt("retired") == 1);
        this.disqualified = (input.getInt("disqualified") == 1);
        this.playing = (input.getInt ("playing") == 1);
    }


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * Loads the default / blank / new player. It should be in a constructor
     * but you know what java is so bitchy about calling constructors from other
     * constructors ON THE FIRST LINE that I have to do it this way.
     *
     */
    private void loadState()
    {
        id = 0;
        tournament_id = 0;
        name = "";
        email = "";
        uses_avatar = false;
        playing = false;
        disqualified = false;
        retired = false;
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Dump the current state into the database.
     * The exact method used will depend on whether or not the record already exists
     * in the database ..
     */
    public void saveState ()
    {
        // is this submission already in the database?
        boolean exists = false;
        String query;

        if (this.id > 0)
        {
            query = "SELECT * FROM submission WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null) {
                exists = true;
                DBManager.disconnect(res);          // disconnect by result
            } else {
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }

        if (exists)
        {
            query = "UPDATE submission SET team_name = '" + this.name
                    + "', team_email = '" + this.email
                    + "', team_avatar = " + DBManager.BoolValue(this.uses_avatar)
                    + ", tournament_id = " + this.tournament_id
                    + " WHERE id = " + this.id;

            DBManager.Execute(query);
        }
        else
        {
            query = "INSERT INTO submission (team_name, team_email, team_avatar"
                    + ", tournament_id) VALUES ("
                    + "'" + this.name + "'"
                    + ", '" + this.email + "'"
                    + ", " + DBManager.BoolValue(this.uses_avatar)
                    + ", " + this.tournament_id + ")";

            // we do want to know what the primary key of this new record is.
            this.id = DBManager.ExecuteReturnKey(query);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 11/09/2015
     *
     * Returns an array of PlayerSubmission objects
     *
     * @param t_id - the tournament to return submissions for. Will return all submissions if t is 0
     * @return - the playersubmission set
     */
    public static PlayerSubmission [] LoadAll (int t_id)
    {
        return LoadAll(t_id, false);
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * Returns an array of PlayerSubmission objets from the database.
     *
     * @param t_id - the tournament to return submissions for. Returns from all if this is 0.
     * @param active_only - whether or not to return active players only, or all of them.
     *
     * @return - the PlayerSubmission[] array
     */
    public static PlayerSubmission [] LoadAll (int t_id, boolean active_only)
    {
        String query = "SELECT * FROM submission WHERE 1";
        query += (t_id == 0) ? "" : " AND tournament_id = " + t_id;
        query += (active_only) ? " AND retired = 0" : "";

        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);
        List<PlayerSubmission> temp_list = new LinkedList<>();

        if (res != null)
        {
            try
            {
                while (res.next())
                    temp_list.add(new PlayerSubmission(res));
            }
            catch (Exception e)
            {
                String error = "PlayerSubmission.LoadAll - Error executing SQL query: "
                        + query + ": " + e;
                LogManager.Log(LogType.ERROR, error);
            }
            DBManager.disconnect(res);          // disconnect by result
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
        }

        PlayerSubmission[] res_array = new PlayerSubmission[temp_list.size()];
        return temp_list.toArray(res_array);
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Retires this player submission.
     * A retirement occurs when a student uploads a newer version of their player.
     * The old version is disabled, the game logs archived, and any future games involving this
     * player are removed from the list.
     *
     * This object is only responsible for the first task - retiring itself.
     *
     */
    public void Retire()
    {
        String query = "UPDATE submission SET retired = 1 WHERE id = " + this.id;
        this.retired = true;
        DBManager.Execute(query);
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Searches player submissions for any that were submitted using this filename
     * Since that is how they will be differentiated - by UID - and for the same
     * tournament, for what I hope are obvious reasons.
     *
     * This method returns only the one unretired player, not the complete set of all submissions
     *
     * @param original - the filename that has been detected in the input folder.
     * @param t_id - the tournament in question. Clearly it is desirable to support multiple
     *          tournaments and submissions with common filenames thereof
     *          What a shocker of a sentence that was.
     * @return a player submission object
     */
    public static PlayerSubmission GetActiveWithTeamName(String original, int t_id)
    {
        PlayerSubmission res = null;

        String query = "SELECT * FROM submission WHERE team_name = '"
                + original + "' AND retired = 0 AND tournament_id = " + t_id;

        try
        {
            Connection con = DBManager.connect();
            ResultSet ressie = DBManager.ExecuteQuery(query, con);
            if (ressie.next())
                res = new PlayerSubmission(ressie);

            DBManager.disconnect(ressie);
        }
        catch (Exception e)
        {
            String error = "PlayerSubmission.GetActiveWithTeamName - Error executing SQL query: "
                    + query + ": " + e;
            LogManager.Log(LogType.ERROR, error);
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Accessor / getter functions.
     *
     * @return various interesting bits of data.
     */
    public String Name () { return this.name; }
    public String Email () { return this.email; }
    public boolean UsesAvatar () { return this.uses_avatar; }
    public String Avatar() { return SystemState.pictures_folder + this.id + ".pic"; }
    public int TournamentKey() { return this.tournament_id; }
    public boolean LivePlaying() { return this.check_boolfield("playing"); }
    public boolean LiveRetired() { return this.check_boolfield("retired"); }
    public boolean LiveDisqualified() { return this.check_boolfield("disqualified"); }
    public boolean Retired() { return this.retired; }
    public boolean Disqualified() { return this.disqualified; }
    public boolean Playing() { return this.playing; }
    public boolean ReadyToPlay () { return !(Playing() | Retired() | Disqualified()); }
    public String MarshalledSource () { return SystemState.marshalling_folder + this.id + ".sub"; }
    public int FixtureSlotAllocation ()
    {
        String query = "SELECT fs.* FROM fixture_slot fs, submission s WHERE"
                + " fs.tournament_id = s.tournament_id AND fs.submission_id = s.id"
                + " AND s.id = " + this.id;

        int fixture_position = 0;
        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);
        if (res != null)
        {
            try
            {
                while (res.next())
                {
                    fixture_position = res.getInt("id");
                }
            }
            catch (Exception e)
            {
                String er = "Game.ResetAll - SQL error retrieving player data. " + e;
                LogManager.Log(LogType.ERROR, er);
                DBManager.disconnect(connection);
            }

            DBManager.disconnect(res);          // disconnect by result
        }
        else
        {
            String er = "Game.ResetAll - No data error retrieving player data.";
            LogManager.Log(LogType.ERROR, er);
            DBManager.disconnect(connection);   // disconnect by connection
        }
        LogManager.Log(LogType.TOURNAMENT, "PlayerSubmission.FixtureSlotAllocation: Returning " + fixture_position + " for submission " + this.id);
        return fixture_position;
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Setter functions.
     *
     * @param s - the data to set.
     */
    public void setName (String s)
    {
        String query = "UPDATE submission SET team_name = '" + s + "' WHERE id = " + this.id;
        DBManager.Execute(query);
    }

    public void setEmail (String s)
    {
        String query = "UPDATE submission SET team_email = '" + s + "' WHERE id = " + this.id;
        DBManager.Execute(query);
    }

    public void setAvatar (boolean s)
    {
        String query = "UPDATE submission SET team_avatar = '" + DBManager.BoolValue(s) + "' WHERE id = " + this.id;
        DBManager.Execute(query);
    }

    public void setTournamentKey(int fk)
    {
        String query = "UPDATE submission SET tournament_id = " + fk + " WHERE id = " + this.id;
        DBManager.Execute(query);
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Returns a count of the number of players who have been assigned slots in a tournament.
     * If the parameter tourney is null, get a count of registrations for all tournaments.
     *
     * @param tourney_id - the tournament to query, or zero for all.
     * @return a count of the number of registered players.
     */
    public static int CountRegisteredPlayers (int tourney_id)
    {
        String query;

        if (tourney_id == 0)
            query = "SELECT COUNT(*) AS count FROM fixture_slot"
                + " WHERE submission_id <> 0";
        else
            query = "SELECT COUNT(*) AS count FROM fixture_slot"
                + " WHERE submission_id <> 0 AND tournament_id = " + tourney_id;

        int res = 0;

        Connection connection = DBManager.connect();
        ResultSet r = DBManager.ExecuteQuery(query, connection);

        if (r != null)
        {
            try
            {
                r.next();
                res = r.getInt("count");
                DBManager.disconnect(r);          // disconnect by result
            }
            catch (Exception e)
            {
                String error = "PlayerSubmission.CountRegisteredPlayers - SQL error retrieving player data. " + e;
                LogManager.Log(LogType.ERROR, error);
                DBManager.disconnect(connection);
            }
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Records the fact that this player is currently playing a game.
     */
    public void StartingGame()
    {
        String query = "UPDATE submission SET playing = " + DBManager.BoolValue(true) + " WHERE id = " + this.id;
        DBManager.Execute(query);
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Signals that a player has finished playing a game.
     *
     * @param disqualified - true if this player was disqualified
     * @throws Exception - chucks a hissy fit if the player doesnt seem to be playing any games to end.
     */
    public void EndingGame (boolean disqualified) throws Exception
    {
        if (!LivePlaying())
            throw new Exception ("This player does not seem to be playing any game.");

        String query = (disqualified) ? "UPDATE submission SET playing = " + DBManager.BoolValue(false)
                                            + ", disqualified = " + DBManager.BoolValue(true)
                                        : "UPDATE submission SET playing = " + DBManager.BoolValue(false);
        query += " WHERE id = " + this.id;
        DBManager.Execute(query);
    }
}

