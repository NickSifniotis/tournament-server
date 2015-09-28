package Common.DataModelObject;


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * The data model object for the different twitter account
 * credentials that are stored in the database.
 */
public class TwitterConfig extends Entity
{
    public String account_name;
    public String consumer_key;
    public String consumer_secret;
    public String access_token;
    public String access_token_secret;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * @param input - the database row that holds this data.
     */
    @Override
    public void LoadFromRecord(ResultSet input) throws SQLException
    {
        this.id = input.getInt ("id");
        this.account_name = input.getString ("account_name");
        this.consumer_key = input.getString ("consumer_key");
        this.consumer_secret = input.getString ("consumer_secret");
        this.access_token = input.getString ("access_token");
        this.access_token_secret = input.getString ("access_token_secret");
    }
}
