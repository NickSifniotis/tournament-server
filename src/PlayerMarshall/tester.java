package PlayerMarshall;

import PlayerMarshall.DataModel.Tournament;

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
                System.out.println (s);
        }
    }
}
