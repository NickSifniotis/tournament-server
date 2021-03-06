package Common.DataModel;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IPlayer;
import AcademicsInterface.IViewer;
import Common.DBManager;
import Services.Logs.LogType;
import Common.SystemState;
import AcademicsInterface.IVerification;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by nsifniotis on 31/08/15.
 *
 * Data model for the tournaments themselves
 *
 */
public class Tournament extends Entity
{
    private String name;
    private boolean game_on;
    private int timeout;
    private boolean allow_resubmit;
    private boolean allow_submit;
    private boolean use_null_moves;
    private int game_type_id;
    private int num_players;
    private String player_interface_class;
    private String verification_class;
    private PointStructure points;


    @Override
    protected String table_name() {
        return "tournament";
    }

    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Empty constructor.
     *
     */
    public Tournament ()
    {
        load_state();
    }


    public Tournament (int id)
    {
        if (id > 0)
        {
            String query = "SELECT * FROM tournament WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                try
                {
                    res.next();
                    this.load_state(res);
                    DBManager.disconnect(res);          // disconnect by result
                }
                catch (Exception e)
                {
                    String error = "TournamentKey constructor (id) - SQL error retrieving tournament data. " + e;
                    DBManager.LogService(LogType.ERROR, error);
                    this.load_state();
                    DBManager.disconnect(connection);
                }
            }
            else
            {
                this.load_state();
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }
        else
            load_state();
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Construct this tournament object using the provided dataset.
     *
     * @param input - the record set from the database
     */
    public Tournament (ResultSet input)
    {
        try
        {
            this.load_state(input);
        }
        catch (Exception e)
        {
            String error = "TournamentKey constructor (resultset) - SQL error: " + e;
            DBManager.LogService(LogType.ERROR, error);

            load_state();
        }
    }

    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Reads all the tournaments from the database, and returns an array
     * of TournamentKey objects initialised to these values.
     *
     * @return all of the tournaments!
     */
    public static Tournament[] LoadAll ()
    {
        return LoadAll(false);
    }


    /**
     * Nick Sifniotis 5809912
     * 9/9/2015
     *
     * Returns all of the tournaments, or possibly a subset of the tournaments.
     *
     * @param active_only - whether or not to return only those tourneys that are GameOn()
     * @return all of the tournaments! Or only some
     */
    public static Tournament[] LoadAll (boolean active_only)
    {
        List<Tournament> temp = new ArrayList<>();

        String query = "SELECT * FROM tournament";
        if (active_only)
            query += " WHERE game_on = 1";

        Connection con = DBManager.connect();
        ResultSet results = DBManager.ExecuteQuery(query, con);
        try
        {
            while (results.next())
            {
                Tournament t = new Tournament(results);
                temp.add (t);
            }

            DBManager.disconnect(results);
        }
        catch (Exception e)
        {
            String error = "TournamentKey.LoadAll - Error executing SQL query: " + query + ": " + e;
            DBManager.LogService(LogType.ERROR, error);
        }

        DBManager.disconnect(con);

        int size = temp.size();
        Tournament [] res = new Tournament[size];

        return temp.toArray(res);
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Like the other objects in the data model, this one uses a specialised load state
     * and save state method to control access to the database.
     *
     * This function loads the current record from the provided dataset.
     *
     * @param input - the raw data
     */
    private void load_state (ResultSet input) throws SQLException
    {
        this.id = input.getInt("id");
        this.name = input.getString("name");
        this.allow_resubmit = (input.getInt("allow_resubmit") == 1);
        this.game_on = (input.getInt("game_on") == 1);
        this.verification_class = input.getString("verification_class");
        this.player_interface_class = input.getString("player_interface_class");
        this.timeout = input.getInt("timeout");
        this.num_players = input.getInt("num_players");
        this.game_type_id = input.getInt("game_id");
        this.allow_submit = (input.getInt("allow_submit") == 1);
        this.use_null_moves = (input.getInt("use_null_moves") == 1);
        this.points = null;
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Construct an empty tournament object.
     *
     */
    private void load_state ()
    {
        this.id = -1;
        this.name = "";
        this.allow_resubmit = false;
        this.allow_submit = false;
        this.use_null_moves = false;
        this.game_on = false;
        this.verification_class = "";
        this.player_interface_class = "";
        this.num_players = 0;
        this.timeout = 0;
        this.game_type_id = -1;           // anywhere in which game is used, check for null.

        this.points = null;
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Saves the current state of the object into the database.
     *
     */
    public void SaveState ()
    {
        // Is this already in the database?
        boolean exists = false;
        String query;

        if (this.id > 0)
        {
            query = "SELECT * FROM tournament WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                exists = true;
                DBManager.disconnect(res);          // disconnect by result
            }
            else
            {
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }

        if (exists)
        {
            query = "UPDATE tournament SET name = " + DBManager.StringValue(this.name)
                    + ", game_id = " + this.game_type_id
                    + ", player_interface_class = " + DBManager.StringValue(this.player_interface_class)
                    + ", verification_class = " + DBManager.StringValue(this.verification_class)
                    + ", allow_resubmit = " + DBManager.BoolValue(this.allow_resubmit)
                    + ", allow_submit = " + DBManager.BoolValue(this.allow_submit)
                    + ", game_on = " + DBManager.BoolValue(this.game_on)
                    + ", use_null_moves = " + DBManager.BoolValue(this.use_null_moves)
                    + ", num_players = " + this.num_players
                    + ", timeout = " + this.timeout
                    + " WHERE id = " + this.id;

            DBManager.Execute(query);
        }
        else
        {
            query = "INSERT INTO tournament (name, game_id, player_interface_class, verification_class,"
                    + " allow_resubmit, allow_submit, game_on, num_players, timeout, use_null_moves)"
                    + " VALUES ("
                    + DBManager.StringValue(this.name)
                    + ", " + this.game_type_id
                    + ", " + DBManager.StringValue(this.player_interface_class)
                    + ", " + DBManager.StringValue(this.verification_class)
                    + ", " + DBManager.BoolValue(this.allow_resubmit)
                    + ", " + DBManager.BoolValue(this.allow_submit)
                    + ", " + DBManager.BoolValue(this.game_on)
                    + ", " + this.num_players
                    + ", " + this.timeout
                    + ", " + DBManager.BoolValue(this.use_null_moves)
                    + ")";

            // we do want to know what the primary key of this new record is.
            this.id = DBManager.ExecuteReturnKey(query);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Accessor functions
     *
     * @return a whole bunch of different things
     */
    public String Name() { return this.name; }
    public int Timeout () { return this.timeout; }
    public boolean AllowResubmit () { return this.check_boolfield("allow_resubmit"); }
    public boolean AllowSubmit () { return this.check_boolfield("allow_submit"); }
    public boolean UsesNullMoves () { return this.check_boolfield("use_null_moves"); }
    public boolean GameOn () { return this.check_boolfield("game_on"); }
    public int NumPlayers () { return this.num_players; }
    public int NumSlots ()
    {
        int result = 0;

        String query = "SELECT COUNT(*) AS slot_count FROM fixture_slot WHERE tournament_id = " + id;
        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);

        if (res != null)
        {
            try
            {
                res.next();
                result = res.getInt("slot_count");
            }
            catch (Exception e)
            {
                String error = "Tournament.NumSlots - error in SQL query " + e;
                DBManager.LogService(LogType.ERROR, error);
                DBManager.disconnect(connection);   // disconnect by connection
            }

            DBManager.disconnect(res);          // disconnect by result
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
        }

        return result;
    }


    @Override
    public String toString () { return this.Name(); }


    /**
     * Nick Sifniotis u5809912
     * 7/9/2015
     *
     * Loads the game engine interface class from the Academics JAR file
     * Tests to ensure that it correctly implements the IGameEngine interface
     *
     * This functionality is now in the GameType data model! Where it belongs.
     *
     * @return an instance of the class itself.
     */
    public IGameEngine GameEngine()
    {
        if (this.game_type_id == -1)
            return null;

        return (new GameType(game_type_id)).GameEngine();
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Returns an instance of the IViewer implementation - if one exists - for
     * this game type.
     *
     * @return an instance of the view class, or null.
     */
    public IViewer Viewer()
    {
        if (this.game_type_id == -1)
            return null;

        return (new GameType(game_type_id)).Viewer();
    }


    /**
     * Nick Sifniotis u5809912
     * 12/10/2015
     *
     * @return true if this tournament uses an IViewer, false if it doesnt
     */
    public boolean UsesViewer()
    {
        if (this.game_type_id == -1)
            return false;

        return (new GameType(this.game_type_id).UsesViewer());
    }

    /**
     * Nick Sifniotis u5809912
     * 7/9/2015
     *
     * Loads the player submission interface class from the Academics JAR file
     * Tests to ensure that it correctly implements the IPlayer interface
     *
     * @return an instance of the class itself.
     */
    public IPlayer PlayerInterface()
    {
        if (this.player_interface_class.equals (""))
            return null;

        IPlayer res;
        String fullFileName = SystemState.Folders.PlayerInterfaces + this.id + ".jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            Class temp = playerClassLoader.loadClass(this.player_interface_class);

            if (!IPlayer.class.isAssignableFrom(temp))
                throw new ClassNotFoundException("The class does not correctly implement IPlayer");

            res = (IPlayer) temp.newInstance();
        }
        catch (Exception e)
        {
            String error = "TournamentKey.PlayerInterfaceClass - error creating class: " + e;
            DBManager.LogService(LogType.ERROR, error);

            return null;
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Return one instance of the verification class that the instructor has supplied.
     * It might return null so watch out for that.
     *
     * But dont worry, provided that you haven't fucked anything up it probably won't.
     *
     * @return an instance of the IVerification implementation
     */
    public IVerification Verification ()
    {
        if (this.verification_class.equals (""))
            return null;

        IVerification res;
        String fullFileName = SystemState.Folders.PlayerInterfaces + this.id + ".jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            Class temp = playerClassLoader.loadClass(this.verification_class);

            if (!IVerification.class.isAssignableFrom(temp))
                throw new ClassNotFoundException("The class does not correctly implement IVerification.");

            res = (IVerification) temp.newInstance();
        }
        catch (Exception e)
        {
            String error = "TournamentKey.Verification - error creating class: " + e;
            DBManager.LogService(LogType.ERROR, error);

            return null;
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Returns a number that represents an available position in the tournament fixture.
     * Throws a bitchy exception if there is no room left in the tournament.
     *
     * @return the prikey of the available slot, or throws an exception if none exist..
     */
    public int GetNextAvailableSlot () throws Exception
    {
        String query = "SELECT * FROM fixture_slot WHERE tournament_id = " + this.id + " AND submission_id = 0";
        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);
        int slot_key;

        if (res != null)
        {
            res.next();
            slot_key = res.getInt("id");
            DBManager.disconnect(res);          // disconnect by result
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
            throw new Exception ("No room left in tournament.");
        }

        if (slot_key == 0)
            throw new Exception ("No room left in tournament.");

        return slot_key;
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Adds this submission to the tournament.
     * slot_key is the value returned by GetNextAvailableSlot() above.
     *
     * @param slot_key - the slot key to assign the player to
     * @param player_id - the player to assign to that slot
     */
    public void AddPlayerToFixture (int slot_key, int player_id)
    {
        DBManager.LogService(LogType.TOURNAMENT, "Adding player " + player_id + " to fixture slot " + slot_key);

        String query = "UPDATE fixture_slot SET submission_id = " + player_id + " WHERE id = " + slot_key;
        DBManager.Execute(query);
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * Resets the tournament back to square one.
     * That is to say, marks all of these tournament's games as unplayed
     * and switches the tournament off.
     */
    public void ResetTournament()
    {
        this.StopTournament();

        int [] tourney_list = { this.id };
        Game [] games = Game.LoadAll(tourney_list, false, false);
        for (Game g: games)
            g.Reset();

        PlayerSubmission[] players = PlayerSubmission.LoadAll(this.id);
        for (PlayerSubmission p: players)
            p.EndingGame(false);
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * Start and stop this tournament.
     *
     */
    public void StartTournament()
    {
        this.game_on = true;
        SaveState();
    }

    public void StopTournament()
    {
        this.game_on = false;
        SaveState();
    }
}
