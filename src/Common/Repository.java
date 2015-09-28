package Common;


import Common.DataModelObject.Entity;
import Common.DataModelObject.TwitterConfig;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import twitter4j.Twitter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * So, this could be a new way to hold the data from the database.
 * The entirety of the database is stored in main memory.
 * The objects are only created once. Everyone shares them.
 *
 * Concurrency is going to destroy this model. Even with views and
 * only allowing one service to write to each field, there could be
 * collisions. What the fuck.
 */
public class Repository
{
    private static HashMap<Integer, Entity> twitter_configs;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Load up from the database all things.
     */
    public static void Initialise()
    {
        twitter_configs = load_from_table("twitter_config", TwitterConfig.class);
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * If this method works, it's going to be awesome.
     * The children of the Entity type know how to load themselves from a DB
     * This class simply loads all the records in the given table, and
     * turns them all into objects of the given class type. The only restriction
     * is that the class type must be a descendant of entity.
     *
     * @param table - the table in the DB to load.
     * @param type - the type of class to be constructing.
     * @return a hashmap of objects from the database.
     */
    private static HashMap<Integer, Entity> load_from_table(String table, Class type)
    {
        String query = "SELECT * FROM " + table;
        Connection connection = DBManager.connect();
        HashMap <Integer, Entity> holding = new HashMap<>();

        ResultSet results = DBManager.ExecuteQuery(query, connection);
        try
        {
            while (results.next())
            {
                Entity temp_entity = (Entity)type.newInstance();
                temp_entity.LoadFromRecord (results);
                holding.putIfAbsent (temp_entity.id, temp_entity);
            }
        }
        catch (Exception e)
        {
            String error = "Error while loading data from table " + table + ": " + e;
            LogManager.Log (LogType.ERROR, error);
        }
        finally
        {
            DBManager.disconnect(results);
        }

        return holding;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Accessor functions for the various data objects.
     *
     * @param id - the ID of the object to return.
     * @return the object.
     */
    public static TwitterConfig GetTwitterConfig (int id) { return (TwitterConfig) twitter_configs.get(id); }
}
