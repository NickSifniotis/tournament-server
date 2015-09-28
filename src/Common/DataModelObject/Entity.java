package Common.DataModelObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * The base class used for all data model objects.
 */
public abstract class Entity implements Comparable<Entity>
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


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Truly lazy programming.
     *
     * Fuck ever having to sort anything myself ever again.
     *
     * @param o - the other entity to compare to
     * @return the result of the comparison
     */
    @Override
    public int compareTo(Entity o)
    {
        return Integer.compare(this.id, o.id);
    }
}
