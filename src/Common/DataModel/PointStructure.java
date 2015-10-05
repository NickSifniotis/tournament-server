package Common.DataModel;

import Common.DBManager;
import Services.LogService;
import Services.Logs.LogType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 17/09/15.
 *
 * The data model object for the table 'point structure'
 *
 *
 * id               prikey autoinc          W TM, R LL
 * tournament_id    int                     W TM, R LL
 * position         int                     W TM, R LL
 * points           int                     W TM, R LL
 */
public class PointStructure extends Entity implements Comparable<PointStructure>
{
    private int tournament_id;
    private int position;
    private int points;


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Create a new blank record and save it immediately.
     *
     */
    public PointStructure()
    {
        load_state();
        save_state();
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Constructor for this object, build from database record
     *
     * @param input - the database record.
     */
    public PointStructure(ResultSet input)
    {
        try
        {
            this.load_state(input);
        }
        catch (Exception e)
        {
            String error = "PointStructure.constructor (resultset) - SQL error encountered: " + e;
            LogService.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Get them all from the database!
     *
     * @param tournament_id - which tournament to load for
     * @return all of the point items
     */
    public static PointStructure[] LoadAll (int tournament_id)
    {
        List<PointStructure> res = new LinkedList<>();
        String query = "SELECT * FROM point_structure WHERE tournament_id = " + tournament_id;
        Connection connection = DBManager.connect();
        ResultSet records = DBManager.ExecuteQuery(query, connection);

        if (records != null)
        {
            try
            {
                while (records.next())
                {
                    res.add(new PointStructure(records));
                }

                DBManager.disconnect(records);          // disconnect by result
            }
            catch (Exception e)
            {
                String error = "PointStructure.LoadAll (t_id) - SQL error retrieving game data. " + e;
                LogService.Log(LogType.ERROR, error);
                DBManager.disconnect(connection);
            }
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
        }

        PointStructure[] temp = new PointStructure[res.size()];
        return res.toArray(temp);
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Default blank state.
     */
    private void load_state ()
    {
        this.id = 0;
        this.tournament_id = 0;
        this.position = 0;
        this.points = 0;
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Loads this records data from the resultset provided.
     *
     * @param input - the database record
     * @throws SQLException if something goes wrong
     */
    private void load_state (ResultSet input) throws SQLException
    {
        this.id = input.getInt("id");
        this.tournament_id = input.getInt("tournament_id");
        this.position = input.getInt("position");
        this.points = input.getInt("points");
    }


    private void save_state ()
    {
        //@TODO since table_name and entity exist it would be worth refactoring the fuck
        //@TODO out of load and save states. Put the engine in entity and use overridden fnctions
        //@TODO to assign values to variables and create the SQL queries.
        // is this already in the database?
        boolean exists = false;
        String query;

        if (this.id > 0)
        {
            query = "SELECT * FROM " + table_name() + " WHERE id = " + id;
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
            query = "UPDATE " + table_name() + " SET tournament_id = " + this.tournament_id
                    + ", position = " + this.position
                    + ", points = " + this.points
                    + " WHERE id = " + this.id;

            DBManager.Execute(query);
        }
        else
        {
            query = "INSERT INTO " + table_name() + " (tournament_id, position, points)"
                    + " VALUES (" + this.tournament_id
                    + ", " + this.position
                    + ", " + this.points
                    + ")";

            // we do want to know what the primary key of this new record is.
            this.id = DBManager.ExecuteReturnKey(query);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Accessor functions.
     *
     * @return data
     */
    public int TournamentKey() { return this.tournament_id; }
    public int Position() { return this.position; }
    public int Points() { return this.points; }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Setter functions for this object.
     *
     * @param t_id various data
     */
    public void SetTournamentKey(int t_id) { this.tournament_id = t_id; this.save_state(); }
    public void SetPosition(int i) { this.position = i; this.save_state(); }
    public void SetPoints(int i) { this.points = i; this.save_state(); }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * @return the database table name for objects of this class.
     */
    @Override
    protected String table_name()
    {
        return "point_structure";
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * I'd rather do this than write a sorting algorithm. Even for an array with fewer than ten elements. Go me!
     *
     * @param o - the other item.
     * @return the results of the comparison
     */
    @Override
    public int compareTo(PointStructure o)
    {
        return Integer.compare(this.position, o.position);
    }
}
