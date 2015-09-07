package Common.DataModel;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IPlayer;
import Common.DBManager;
import Common.SystemState;
import AcademicsInterface.IVerification;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Data model for the tournaments themselves
 *
 */
public class Tournament {
    private int id;
    private String name;
    private String submission_folder;

    private boolean uses_verification;
    private String verification_package;
    private String sources_jarfile;
    private String game_engine_class;
    private String player_interface_class;
    private String submission_class;
    private String submission_method;


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
                t.name = results.getString("name");
                t.submission_folder = results.getString("submissions_folder");
                t.id = results.getInt("id");
                t.uses_verification = (results.getInt("uses_verification") == 1);
                t.verification_package = results.getString("verification_package");
                t.game_engine_class = results.getString ("game_engine_class");
                t.player_interface_class = results.getString ("player_interface_class");
                t.sources_jarfile = results.getString ("sources_jar");
                t.submission_class = results.getString ("submission_class");
                t.submission_method = results.getString ("submission_method");
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

    public String Name()
    {
        return this.name;
    }

    public int PrimaryKey () { return this.id; }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Yeah, I can't believe I put this here either. Tourneys don't verify things.
     *
     * @param filename - the submission to verify
     * @return
     */
    public boolean VerifySubmission (File filename)
    {
        if (!uses_verification)
            return true;

        boolean result = false;
        SystemState.Log ("Tournament.VerifySubmission - attempting to load verification package " + verification_package);
        String fullFileName = SystemState.sources_folder + this.sources_jarfile + ".jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            Class verifier = playerClassLoader.loadClass(this.verification_package);

            if (!IVerification.class.isAssignableFrom(verifier))
                throw new ClassNotFoundException("The class does not correctly implement IVerification");

            SystemState.Log ("Tournament.VerifySubmission - class loaded and implements IVerification.");

            Method verification_method = verifier.getMethod("VerifySubmission", File.class);
            Object res = verification_method.invoke(verifier.newInstance(), filename);
            result = (boolean) res;
        }
        catch (Exception e)
        {
            String error = "Tournament.VerifySubmission - Error processing verification class: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }

        return result;
    }


    /**
     * Nick Sifniotis u5809912
     * 7/9/2015
     *
     * Loads the game engine interface class from the Academics JAR file
     * Tests to ensure that it correctly implements the IGameEngine interface
     *
     * @return the class itself - not an instance of it.
     */
    public Class GameEngineClass()
    {
        if (this.game_engine_class.equals (""))
            return null;

        Class res;
        String fullFileName = SystemState.sources_folder + this.sources_jarfile + ".jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            res = playerClassLoader.loadClass(this.game_engine_class);

            if (!IGameEngine.class.isAssignableFrom(res))
                throw new ClassNotFoundException("The class does not correctly implement IGameEngine");
        }
        catch (Exception e)
        {
            String error = "Tournament.GameEngineClass - error creating class: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return null;
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 7/9/2015
     *
     * Loads the player submission interface class from the Academics JAR file
     * Tests to ensure that it correctly implements the IPlayer interface
     *
     * @return the class itself - not an instance of it.
     */
    public Class PlayerInterfaceClass()
    {
        if (this.player_interface_class.equals (""))
            return null;

        Class res;
        String fullFileName = SystemState.sources_folder + this.sources_jarfile + ".jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            res = playerClassLoader.loadClass(this.player_interface_class);

            if (!IPlayer.class.isAssignableFrom(res))
                throw new ClassNotFoundException("The class does not correctly implement IPlayer");
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
