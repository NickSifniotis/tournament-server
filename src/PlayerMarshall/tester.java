package PlayerMarshall;


import AcademicsInterface.IVerification;
import Common.DataModel.Game;
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

        Game.LoadAll(tournaments[0], true);
    }
}
