package Common.DataModel;

import Common.DBManager;
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
 * id                   prikey int
 * tournament_id        int
 * team_name            varchar
 * team_email           varchar
 * team_motto           varchar
 * team_avatar          varchar
 * ready                boolean
 * disqualified         int
 * retired              boolean
 * original_filename    varchar
 *
 */
public class PlayerSubmission
{
    private int id;
    private int tournament_id;

    private String name;
    private String email;
    private String motto;
    private String orig_file;
    private String avatar_file;

    private boolean retired;
    private int disqualified_count;
    private boolean ready;


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
        SystemState.Log("PlayerSubmission constructor (player_id) - attempting to load player " + player_id);
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
                    SystemState.Log(error);

                    if (SystemState.DEBUG)
                        System.out.println (error);

                    this.loadState();
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
        this.motto = input.getString("team_motto");
        this.avatar_file = input.getString("team_avatar");
        this.orig_file = input.getString("original_filename");
        this.retired = (input.getInt("retired") == 1);
        this.ready = (input.getInt("ready") == 1);
        this.disqualified_count = input.getInt("disqualified");
        this.tournament_id = input.getInt ("tournament_id");
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
        motto = "";
        avatar_file = "";
        orig_file = "";
        retired = false;
        disqualified_count = 0;
        ready = false;
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Sets the metadata of this submission.
     *
     * @param data - the data extracted using IVerification.ExtractMetaData
     */
    public void SetMetaData (SubmissionMetadata data)
    {
        this.setName(data.team_name);
        this.setAvatar(data.team_picture);
        this.setEmail(data.team_email);
        this.setMotto(data.team_motto);
        this.setSubmissionKey(data.team_name);      //@TODO: Is this really necessary?
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
        SystemState.Log("Saving state for player submission " + this.id);


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
                    + "', team_avatar = '" + this.avatar_file
                    + "', team_motto = '" + this.motto
                    + "', original_filename = '" + this.orig_file
                    + "', retired = " + DBManager.BoolValue(this.retired)
                    + ", disqualified = " + this.disqualified_count
                    + ", ready = " + DBManager.BoolValue(this.ready)
                    + ", tournament_id = " + this.tournament_id
                    + " WHERE id = " + this.id;

            if (SystemState.DEBUG) System.out.println (query); else SystemState.Log(query);

            DBManager.Execute(query);
        }
        else
        {
            query = "INSERT INTO submission (team_name, team_email, team_motto, team_avatar"
                    + ", original_filename, retired, disqualified, ready, tournament_id) VALUES ("
                    + "'" + this.name + "'"
                    + ", '" + this.email + "'"
                    + ", '" + this.motto + "'"
                    + ", '" + this.avatar_file + "'"
                    + ", '" + this.orig_file + "'"
                    + ", " + DBManager.BoolValue(this.retired)
                    + ", " + this.disqualified_count
                    + ", " + DBManager.BoolValue(this.ready)
                    + ", " + this.tournament_id + ")";

            if (SystemState.DEBUG) System.out.println (query); else SystemState.Log(query);

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
     * @param t - the tournament to return submissions for. Will return all submissions if t is null
     * @return - the playersubmission set
     */
    public static PlayerSubmission [] LoadAll (Tournament t)
    {
        String query;
        if (t == null)
            query = "SELECT * FROM submission";
        else
            query = "SELECT * FROM submission WHERE tournament_id = " + t.PrimaryKey();

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
                SystemState.Log(error);

                if (SystemState.DEBUG)
                    System.out.println (error);
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
        SystemState.Log("PlayerSubmission.Retire - attempting to retire player with PK " + this.id);
        this.retired = true;
        saveState();
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
     * @param t - the tournament in question. Clearly it is desirable to support multiple
     *          tournaments and submissions with common filenames thereof
     *          What a shocker of a sentence that was.
     * @return a player submission object
     */
    public static PlayerSubmission GetActiveWithOriginalFilename (String original, Tournament t)
    {
        PlayerSubmission res = null;

        String query = "SELECT * FROM submission WHERE original_filename = '"
                + original + "' AND retired = 0 AND tournament_id = " + t.PrimaryKey();
        SystemState.Log("PlayerSubmission.GetActiveWithOriginalFilename - attempting to query database: " + query);

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
            String error = "PlayerSubmission.GetActiveWithOriginalFilename - Error executing SQL query: "
                    + query + ": " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
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
    public int PrimaryKey () { return this.id; }
    public String Name () { return this.name; }
    public String Email () { return this.email; }
    public String Motto () { return this.motto; }
    public File Avatar () { return new File (this.avatar_file); } // @TODO: Avatar path
    public String SubmissionKey () { return this.orig_file; }
    public int TournamentKey() { return this.tournament_id; }
    public boolean ReadyToPlay () { return this.ready & !this.retired & (this.disqualified_count == 0); }
    public boolean Active () { return !this.retired; }
    public String MarshalledSource () { return SystemState.marshalling_folder + this.id + ".sub"; }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Setter functions.
     *
     * @param s - the data to set.
     */
    public void setName (String s) { this.name = s; }
    public void setEmail (String s) { this.email = s; }
    public void setAvatar (String s) { this.avatar_file = s; }
    public void setMotto (String s) { this.motto = s; }
    public void setSubmissionKey (String s) { this.orig_file = s; }
    public void setTournamentKey(int fk) { this.tournament_id = fk; }
    public boolean IsReady () { return this.ready && !this.retired && (this.disqualified_count == 0); }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Resets the player submission so that it can play games again.
     *
     */
    public void GetReady()
    {
        this.ready = true;
        this.retired = false;
        this.disqualified_count = 0;

        saveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Returns a count of the number of players who have been assigned slots in a tournament.
     * If the parameter tourney is null, get a count of registrations for all tournaments.
     *
     * @param tourney - the tournament to query, or null for all.
     * @return a count of the number of registered players.
     */
    public static int CountRegisteredPlayers (Tournament tourney)
    {
        String query;

        if (tourney == null)
            query = "SELECT COUNT(*) AS count FROM fixture_slot"
                + " WHERE submission_id <> 0";
        else
            query = "SELECT COUNT(*) AS count FROM fixture_slot"
                + " WHERE submission_id <> 0 AND tournament_id = " + tourney.PrimaryKey();

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
                SystemState.Log(error);

                if (SystemState.DEBUG)
                    System.out.println (error);
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
     *
     * @throws Exception - if the player is not in the correct state to be starting games.
     */
    public void StartingGame() throws Exception
    {
        if (!this.ready || this.retired || this.disqualified_count > 0)
            throw new Exception ("This player is not able to play.");

        this.ready = false;
        this.saveState();
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
        if (this.ready)
            throw new Exception ("This player does not seem to be playing any game.");

        if (disqualified)
            this.disqualified_count++;
        this.ready = true;

        this.saveState();
    }
}

