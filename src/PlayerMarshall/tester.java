package PlayerMarshall;

import PlayerMarshall.DataModel.Tournament;

import java.io.File;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Just a PSVM for testing purposes
 */
public class tester {

    public static void main(String[] args) {
        SystemState.initialise();
        SystemState.Log ("System restarted!");

        Tournament[] tourneys = Tournament.LoadAll();
        PlayerMarshall marshall = new PlayerMarshall();

        for (Tournament t: tourneys)
        {
            System.out.println ("Checking " + t.Name());
            String [] files = marshall.GetNewSubmissions(t);
            for (String s: files)
            {
                System.out.println("Found file " + s);

                System.out.println (t.VerifySubmission(new File(s)));
            }
        }
    }
}
