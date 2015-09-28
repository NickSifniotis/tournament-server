package Common.DataModelObject;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * The base class used for all data model objects.
 */
public abstract class Entity
{
    public int id;

    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Loads this object's data from its database row.
     *
     * @param input - the database row that holds this data.
     */
    public abstract void LoadFromRecord(ResultSet input) throws SQLException;
}
