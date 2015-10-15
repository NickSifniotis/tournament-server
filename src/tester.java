import Common.DBManager;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by nsifniotis on 15/10/15.
 */
public class tester
{
    public static void main(String[] args) {

        String query = "SELECT * FROM submission";

        Connection con = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, con);

        try
        {
            while (res.next())
            {
                System.out.println(res.getString("team_name") + ":" + res.getInt("playing") + ":" + res.getInt("disqualified"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        DBManager.disconnect(res);


        query = "SELECT * FROM game";

        con = DBManager.connect();
        res = DBManager.ExecuteQuery(query, con);

        try
        {
            while (res.next())
            {
                System.out.println(res.getInt("tournament_id") + ":" + res.getInt("round_number") + ":" + res.getInt("game_number"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        DBManager.disconnect(res);

    }
}
