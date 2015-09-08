package Common.DataModel;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IPlayer;
import AcademicsInterface.IViewer;
import Common.DBManager;
import Common.SystemState;
import AcademicsInterface.IVerification;

import java.io.File;
import java.lang.reflect.Method;
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
public class Tournament {

    private String submission_folder;

;
    private String sources_jarfile;
    private String game_engine_class;

    private String submission_class;
    private String submission_method;

    // the new data model. Wean the system off every entry above this line.
    private int id;
    private String name;
    private boolean game_on;
    private int timeout;
    private boolean allow_resubmit;
    private boolean allow_resubmit_on;
    private GameType game;
    private int num_players;
    private String player_interface_class;
    private boolean uses_verification;
    private String verification_class;


    public Tournament ()
    {

    }


    public Tournament (int id)
    {


    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Construct this tournament object using the provided dataset.
     *
     * @param input
     */
    public Tournament (ResultSet input)
    {
        try
        {
            this.load_state(input);
        }
        catch (Exception e)
        {
            String error = "Tournament constructor (resultset) - SQL error: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }
    }

    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Reads all the tournaments from the database, and returns an array
     * of Tournament objects initialised to these values.
     *
     * @return
     */
    public static Tournament[] LoadAll () {
        SystemState.Log("Tournament.LoadAll - attempting to load all");
        List<Tournament> temp = new ArrayList<>();

        String query = "SELECT * FROM tournament";
        Connection con = DBManager.connect();
        ResultSet results = DBManager.ExecuteQuery(query, con);
        try
        {
            while (results.next())
            {
                Tournament t = new Tournament();

                // the old stuff
            //    t.submission_folder = results.getString("submissions_folder");
              //  t.game_engine_class = results.getString ("game_engine_class");
              //  t.sources_jarfile = results.getString ("sources_jar");
              //  t.submission_class = results.getString("submission_class");
              //  t.submission_method = results.getString ("submission_method");

                // the new stuff
                t.id = results.getInt("id");
                t.name = results.getString("name");
                t.uses_verification = (results.getInt("uses_verification") == 1);
                t.allow_resubmit = (results.getInt("allow_resubmit") == 1);
                t.allow_resubmit_on = (results.getInt("allow_resubmit_on") == 1);
                t.game_on = (results.getInt("game_on") == 1);
                t.verification_class = results.getString("verification_class");
                t.player_interface_class = results.getString ("player_interface_class");
                t.timeout = results.getInt("timeout");
                t.num_players = results.getInt("num_players");
                t.game = new GameType(results.getInt ("game_id"));

                temp.add (t);
            }

            DBManager.disconnect(results);
        }
        catch (Exception e)
        {
            String error = "Tournament.LoadAll - Error executing SQL query: " + query + ": " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }

        int size = temp.size();
        Tournament [] res = new Tournament[size];
        SystemState.Log("Tournament.LoadAll - returning " + size + " tournaments.");

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
        this.uses_verification = (input.getInt("uses_verification") == 1);
        this.allow_resubmit = (input.getInt("allow_resubmit") == 1);
        this.allow_resubmit_on = (input.getInt("allow_resubmit_on") == 1);
        this.game_on = (input.getInt("game_on") == 1);
        this.verification_class = input.getString("verification_class");
        this.player_interface_class = input.getString ("player_interface_class");
        this.timeout = input.getInt("timeout");
        this.num_players = input.getInt("num_players");
        this.game = new GameType(input.getInt ("game_id"));
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
        this.uses_verification = false;
        this.allow_resubmit = false;
        this.allow_resubmit_on = false;
        this.game_on = false;
        this.verification_class = "";
        this.player_interface_class = "";
        this.num_players = 0;
        this.timeout = 0;
        this.game = null;           // anywhere in which game is used, check for null.
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Accessor functions
     *
     * @return
     */
    public String SubmissionsPath()
    {
        return this.submission_folder;
    }

    public String Name() { return this.name; }
    public int PrimaryKey () { return this.id; }
    public int Timeout () { return this.timeout; }
    public boolean AllowResubmitOff () { return this.allow_resubmit; }
    public boolean AllowResubmitOn () { return this.allow_resubmit_on; }
    public boolean GameOn () { return this.game_on; }


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
        if (this.game == null)
            return null;

        return this.game.GameEngine();
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
        if (this.game == null)
            return null;
        
        return this.game.Viewer();
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
        String fullFileName = SystemState.interfaces_folder + this.id + ".jar";

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
            String error = "Tournament.PlayerInterfaceClass - error creating class: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

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
     * @return
     */
    public IVerification Verification ()
    {
        if (this.verification_class.equals (""))
            return null;

        IVerification res;
        String fullFileName = SystemState.interfaces_folder + this.id + ".jar";

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
            String error = "Tournament.Verification - error creating class: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return null;
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Accessor functions.
     *
     * @return various values
     */
    public String SubmissionClassName ()
    {
        return this.submission_class;
    }
    public String SubmissionMethod () { return this.submission_method; }
}
