package Common.DataModelObject;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * GameType data model object
 */
public class GameType extends Entity
{
    public String name;
    public String engine_class;
    public String viewer_class;
    public boolean uses_viewer;
    public int min_players;
    public int max_players;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Loads the data from the database.
     *
     * @param input - the database row that holds this data.
     * @throws SQLException
     */
    @Override
    public void LoadFromRecord(ResultSet input) throws SQLException
    {
        this.id = input.getInt ("id");
        this.name = input.getString ("name");
        this.engine_class = input.getString ("engine_class");
        this.viewer_class = input.getString ("viewer_class");
        this.uses_viewer = (input.getInt ("uses_viewer") == 1);
        this.min_players = (input.getInt ("min_players"));
        this.max_players = (input.getInt ("max_players"));
    }
}
