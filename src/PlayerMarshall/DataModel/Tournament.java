package PlayerMarshall.DataModel;

import PlayerMarshall.DBManager;
import PlayerMarshall.SystemState;

import java.io.File;
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

    }
}
