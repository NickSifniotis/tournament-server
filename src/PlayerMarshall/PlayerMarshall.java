package PlayerMarshall;

import PlayerMarshall.DataModel.Tournament;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
}
