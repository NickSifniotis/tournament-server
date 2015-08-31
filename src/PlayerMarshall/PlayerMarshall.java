package PlayerMarshall;

import PlayerMarshall.DataModel.PlayerSubmission;
import PlayerMarshall.DataModel.Tournament;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


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
     * If any new players have been uploaded, return the submissions as an array of files
     *
     * @param t - the tournament who's players we seek
     * @return - the much-sought-after players
     */
    public File [] GetNewSubmissions (Tournament t)
    {
        String full_path = input_folder + t.SubmissionsPath() + "/";
        SystemState.Log ("PlayerMarshall.GetNewSubmissions - checking directory " + full_path + " for tourney " + t.Name());

        File folder = new File (full_path);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null)
            SystemState.Log("PlayerMarshall.GetNewSubmissions - returning " + listOfFiles.length + " files found.");
        else
            SystemState.Log("PlayerMarshall.GetNewSubmissions - " + full_path + " is not a directory.");

        return listOfFiles;
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
            File [] files = GetNewSubmissions(t);
            for (File f: files)
            {
                if (t.VerifySubmission(f))
                {
                    // this submission is good, so lets move it to marshalling and get ready to rumble
                    // has this player been submitted before? We will know because submissions are identified
                    // by filenames.

                    String original = f.getName();
                    PlayerSubmission oldie = PlayerSubmission.GetActiveWithOriginalFilename(original, t);

                    if (oldie != null)
                    {
                        // @TODO: More retirement code here. The games and the logs!
                        oldie.Retire();
                    }

                    PlayerSubmission new_submission = new PlayerSubmission(true);
                    new_submission.setName("Default Name");
                    new_submission.setEmail("Default Email");
                    new_submission.setTournament(1);


                    // copy the submission over to the marshalling folder.
                    String destination = marshalling_folder + new_submission.PrimaryKey() + "." + original.split("\\.")[1];

                    try
                    {
                        Files.copy(f.toPath(), Paths.get(destination));
                        f.delete();
                    }
                    catch (Exception e)
                    {
                        String error = "PlayerMarshall.ProcessNewSubmissions - Error copying player file to marshalling: " + e;
                        SystemState.Log(error);

                        if (SystemState.DEBUG)
                            System.out.println (error);
                    }

                    // last but not least, go ahead and signal that this player is good to go
                    new_submission.ReadyToPlay();

                    // @TODO: Add the player to the games fixture
                }
                else
                {
                    // failed the verification test. Fuck. Now I have to send a dirty email
                    // @TODO: Figure out how to do emails

                    // erase the offending submission
                    try
                    {
                        SystemState.Log("PlayerMarshall.ProcessNewSubmissions - file " + f.getName() + " failed verification. Attempting to delete it.");

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
