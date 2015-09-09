package PlayerMarshall;


import AcademicsInterface.IVerification;
import Common.DataModel.Tournament;
import Common.SystemState;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Just a PSVM for testing purposes
 */
public class tester {

    public static void main(String[] args) {
        SystemState.initialise();
        SystemState.Log("System restarted!");

        Tournament[] tournaments = Tournament.LoadAll();
        String full_filename = SystemState.input_folder + tournaments[0].PrimaryKey() + "/player.jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + full_filename + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath);
            Class temp = playerClassLoader.loadClass("comp1140.ass2.Metadata");

            System.out.println ("got here!");

            System.out.println (temp.getField("team_name").get(temp.newInstance()));
        }
        catch (Exception e)
        {
            String error = "Tournament.Verification - error creating class: " + e;
            System.out.println (error);
        }
    }
}
