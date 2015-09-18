package Common.DataModel;

import Common.DBManager;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import java.sql.Connection;
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
public class Score extends Entity implements Comparable<Score>
{
    private int submission_id;
    private int game_id;
    private int score;
    private boolean no_score;
    private boolean disqualified;


    @Override
    protected String table_name() {
        return "score";
    }

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
        SaveState();
    }


    public Score (int game_id, int submission_id)
    {
        this.load_state();
        this.game_id = game_id;
        this.submission_id = submission_id;
        this.SaveState();
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
            LogManager.Log(LogType.ERROR, error);

            load_state();
        }
    }


    public Score (int id)
    {
        String query;

        if (id > 0)
        {
            query =  "SELECT * FROM game WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                try
                {
                    res.next();
                    this.load_state(res);
                    DBManager.disconnect(res);          // disconnect by result
                }
                catch (Exception e)
                {
                    String error = "Score constructor (score_id) - SQL error retrieving player data. " + e;
                    LogManager.Log(LogType.ERROR, error);

                    this.load_state();
                    DBManager.disconnect(connection);
                }
            }
            else
            {
                this.load_state();
                DBManager.disconnect(connection);   // disconnect by connection
            }
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


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Save the current state of the scores.
     */
    public void SaveState ()
    {
        // is this score already in the database?
        boolean exists = false;
        String query;

        if (this.id > 0)
        {
            query = "SELECT * FROM score WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                exists = true;
                DBManager.disconnect(res);          // disconnect by result
            }
            else
            {
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }

        if (exists)
        {
            query = "UPDATE score SET score = " + this.score
                    + ", game_id = " + this.game_id
                    + ", submission_id = " + this.submission_id
                    + ", no_score = " + DBManager.BoolValue(this.no_score)
                    + ", disqualified = " + DBManager.BoolValue(this.disqualified)
                    + " WHERE id = " + this.id;

            DBManager.Execute(query);
        }
        else
        {
            query = "INSERT INTO score (score, game_id, submission_id, no_score, disqualified)"
                    + " VALUES (" + this.score
                    + ", " + this.game_id
                    + ", " + this.submission_id
                    + ", " + DBManager.BoolValue(this.no_score)
                    + ", " + DBManager.BoolValue(this.disqualified)
                    + ")";

            // we do want to know what the primary key of this new record is.
            this.id = DBManager.ExecuteReturnKey(query);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Updates this record with a new score.
     *
     * @param new_score - the new score.
     */
    public void Update (int new_score)
    {
        this.score = new_score;
        SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Indicates that the game terminated abnormally.
     *
     * @param my_fault - True if it was this record's player that caused it.
     */
    public void AbnormalTermination (boolean my_fault)
    {
        this.no_score = true;
        this.disqualified = my_fault;
        SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Various accessor functions.
     *
     * @return nice data.
     */
    public boolean Disqualified() { return this.disqualified; }
    public boolean NoScore() { return this.no_score; }
    public int Score() { return this.score; }
    public int SubmissionKey () { return this.submission_id; }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Lazy sorting is the best sort of sorting.
     *
     * @param o - the other score to compare against
     * @return the result of the comparison
     */
    @Override
    public int compareTo(Score o)
    {
        return Integer.compare(o.score, this.score);
    }
}
