package PlayerMarshall;


import Common.DBManager;
import Common.DataModel.Game;
import Common.DataModel.Tournament;
import Common.SystemState;

import java.sql.Connection;
import java.sql.ResultSet;


/**
 * Created by nsifniotis on 31/08/15.
 *
 * Just a PSVM for testing purposes
 */
public class tester {

    public static void main(String[] args)
    {
        String query = "UPDATE submission SET retired = 0, playing = 0, disqualified = 0";
        DBManager.Execute(query);
    }
}
