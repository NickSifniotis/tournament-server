package Common.DataModel;

import Common.DBManager;
import Services.LogService;
import Services.Logs.LogType;
import Services.Messages.LogMessage;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * Base class for Data Model objects. Contains useful methods.
 */
public abstract class Entity
{
    protected int id;               // the universal primary key, haha


    protected abstract String table_name();

    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Returns the primary key of this object.
     *
     * @return an int that is the primary key.
     */
    public int PrimaryKey()
    {
        return this.id;
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * The boolfields retired and ready were causing major issues when they were being stored in this
     * data object. The underlying data model would be updated but the changes would never filter through
     * to the class instances. This had the effect of making some players unretireable sometimes.
     *
     * So it was decided to keep these booleans within the database and only extract them on an as-needed basis.
     *
     * @param field_name - the database field we want the value of
     * @return the value from the database.
     */
    protected boolean check_boolfield (String field_name)
    {
        String query = "SELECT " + field_name + " FROM " + table_name() + " WHERE id = " + this.id;

        boolean res = false;

        Connection connection = DBManager.connect();
        ResultSet r = DBManager.ExecuteQuery(query, connection);

        if (r != null)
        {
            try
            {
                r.next();
                res = (r.getInt(field_name) == 1);
                DBManager.disconnect(r);          // disconnect by result
            }
            catch (Exception e)
            {
                String error = "PlayerSubmission.check_boolfield - SQL error retrieving field value " + field_name + ". " + e;
                DBManager.LogService(LogType.ERROR, error);
                DBManager.disconnect(connection);
            }
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
        }

        return res;
    }
}
