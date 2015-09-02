package PlayerMarshall;


import Common.SystemState;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Just a PSVM for testing purposes
 */
public class tester {

    public static void main(String[] args) {
        SystemState.initialise();
        SystemState.Log("System restarted!");

        PlayerMarshall marshall = new PlayerMarshall();

        marshall.ProcessNewSubmissions();
    }
}
