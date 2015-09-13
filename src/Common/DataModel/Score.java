package Common.DataModel;

import Common.SystemState;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nsifniotis on 13/09/15.
 *
 * A single Score record from the database.
 *
 * Database schema for score objects
 *
 * id                   prikey
 * game_id              int
 * submission_id        int
 * score                int
 * no_score             boolean             Was the game terminated abnormally?
 * disqualified         boolean             Was this player the cause of abnormal termination?
 *
 */
public class Score
{
    private int id;
    private int submission_id;
    private int game_id;
    private int score;
    private boolean no_score;
    private boolean disqualified;


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Various constructors.
     *
     */
    public Score ()
    {
        load_state();
    }


    public Score (ResultSet input)
    {
        try
        {
            load_state(input);
        }
        catch (Exception e)
        {
            String error = "Score constructor (resultset) - SQL error retrieving score data. " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            load_state();
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Default new construction.
     *
     */
    private void load_state()
    {
        this.id = 0;
        this.submission_id = 0;
        this.game_id = 0;
        this.score = 0;
        this.no_score = false;
        this.disqualified = false;
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Loads the score data from the database.
     *
     * @param input - the recordset from the database
     * @throws SQLException - if something goes wrong
     */
    private void load_state (ResultSet input) throws SQLException
    {
        this.id = input.getInt("id");
        this.submission_id = input.getInt("submission_id");
        this.game_id = input.getInt("game_id");
        this.score = input.getInt("score");
        this.no_score = (input.getInt("no_score") == 1);
        this.disqualified = (input.getInt("disqualified") == 1);
    }
}
