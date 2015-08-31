package PlayerMarshall;

import PlayerMarshall.DataModel.Tournament;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Created by nsifniotis on 31/08/15.
 */
public class PlayerMarshall {

    private static final String marshalling_folder = "src/marshalling/";
    private static final String input_folder = "src/submissions/inputs/";


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Checks the player submissions input folder for the given tournament.
     * If any new players have been uploaded, return the fully qualified file names of the
     * new submissions as an array (or List? we'll see) of Strings.
     * @TODO: Verify the way that this thing will work.
     * @param t - the tournament who's players we seek
     * @return - the much-sought-after players
     */
    public String [] GetNewSubmissions (Tournament t)
    {
        String full_path = input_folder + t.SubmissionsPath() + "/";
        SystemState.Log ("PlayerMarshall.GetNewSubmissions - checking directory " + full_path + " for tourney " + t.Name());

        File folder = new File (full_path);
        File[] listOfFiles = folder.listFiles();

        List<String> res = new ArrayList<>();
        for (File f: listOfFiles)
            res.add (full_path + f.getName());

        int size = res.size();
        SystemState.Log ("PlayerMarshall.GetNewSubmissions - returning " + size + " files found.");

        String [] resres = new String[size];
        return res.toArray(resres);
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Searches the input folders for new submissions, verifies
     * and processes them.
     *
     * Called every few seconds by the main program loop.
     *
     */
    public void ProcessNewSubmissions ()
    {
        Tournament[] tourneys = Tournament.LoadAll();

        for (Tournament t: tourneys)
        {
            System.out.println ("Checking " + t.Name());
            String [] files = GetNewSubmissions(t);
            for (String s: files)
            {
                if (t.VerifySubmission(new File(s)))
                {
                    // this submission is good, so lets move it to marshalling and get ready to rumble

                }
                else
                {
                    // failed the verification test. Fuck. Now I have to send a dirty email
                    // @TODO: Figure out how to do emails

                    // erase the offending submission
                    try
                    {
                        SystemState.Log("PlayerMarshall.ProcessNewSubmissions - file " + s + " failed verification. Attempting to delete it.");

                        File f = new File (s);
                        f.delete();

                        SystemState.Log("PlayerMarshall.ProcessNewSubmissions - delete successful.");
                    }
                    catch (Exception e)
                    {
                        String error = "PlayerMarshall.ProcessNewSubmissions - Error deleting file: " + e;
                        SystemState.Log(error);

                        if (SystemState.DEBUG)
                            System.out.println (error);
                    }
                }
            }
        }
    }
}
