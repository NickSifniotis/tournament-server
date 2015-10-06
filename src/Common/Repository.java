package Common;


import Common.DataModelObject.Entities;
import Common.DataModelObject.Entity;
import Common.DataModelObject.GameType;
import Common.DataModelObject.TwitterConfig;
import Services.LogService;
import Services.Logs.LogType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;


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
    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Load up from the database all things.
     */
    public static void Initialise()
    {
        for (Entities e: Entities.values())
            Entities.data_store[e.ordinal()] = load_from_table(e);
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
     * @param entity - what sort of entity we are building here
     * @return a hashmap of objects from the database.
     */
    private static HashMap<Integer, Entity> load_from_table(Entities entity)
    {
        String query = "SELECT * FROM " + entity.TableName();
        Connection connection = DBManager.connect();
        HashMap <Integer, Entity> holding = new HashMap<>();

        ResultSet results = DBManager.ExecuteQuery(query, connection);
        try
        {
            while (results.next())
            {
                Entity temp_entity = (Entity) entity.Class().newInstance();
                temp_entity.LoadFromRecord (results);
                holding.putIfAbsent (temp_entity.id, temp_entity);
            }
        }
        catch (Exception e)
        {
            String error = "Error while loading data from table " + entity.TableName() + ": " + e;
            DBManager.LogService(LogType.ERROR, error);
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
    public static TwitterConfig GetTwitterConfig (int id) { return (TwitterConfig) Entities.data_store[Entities.TWITTER_CONFIG.ordinal()].get(id); }
    public static GameType GetGameType (int id) { return (GameType) Entities.data_store[Entities.GAME_TYPE.ordinal()].get(id); }

    public static GameType[] GetGameTypes ()
    {
        Object[] holding = Entities.data_store[Entities.GAME_TYPE.ordinal()].values().toArray();
        GameType[] res = new GameType[holding.length];
        for (int i = 0; i < holding.length; i ++)
            res [i] = (GameType) holding[i];

        Arrays.sort(res);
        return res;
    }

    public static TwitterConfig[] GetTwitterConfigs()
    {
        Object[] holding = Entities.data_store[Entities.TWITTER_CONFIG.ordinal()].values().toArray();
        TwitterConfig[] res = new TwitterConfig[holding.length];
        for (int i = 0; i < holding.length; i ++)
            res [i] = (TwitterConfig) holding[i];

        Arrays.sort(res);
        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Methods for creating new instances of data model objects.
     * They are stored as blanks in the database; the new prikey is
     * immediately retrieved and used to store in the hashmap.
     *
     * @return a new instance of the type wanted.
     */
    public static Entity NewEntity (Entities type_of_thing)
    {
        Entity newb = new_entity(type_of_thing);
        Entities.data_store[type_of_thing.ordinal()].putIfAbsent(newb.id, newb);

        return newb;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Will create, save and return a new instance of whatever entity is.
     *
     * @param entity - the sort of new thing to create
     * @return the new thing
     */
    private static Entity new_entity (Entities entity)
    {
        String query = "INSERT INTO " + entity.TableName() + " DEFAULT VALUES;";
        int id = DBManager.ExecuteReturnKey(query);

        Entity newb = null;
        try
        {
            newb = (Entity) entity.Class().newInstance();
            newb.id = id;
        }
        catch (Exception e)
        {
            String error = "Error instantiating new " + entity.Class().getName() + ": " + e;
            DBManager.LogService(LogType.ERROR, error);
        }

        return newb;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Saves an entity into the database.
     *
     * @param id - the record to save
     */
    public static void SaveGameType (int id)
    {
        GameType game = GetGameType(id);
        if (game == null)
            return;         // should never happen

        String query = "UPDATE " + Entities.GAME_TYPE.TableName()
                + " SET name = " + DBManager.StringValue(game.name)
                + ", min_players = " + game.min_players
                + ", max_players = " + game.max_players
                + ", engine_class = " + DBManager.StringValue(game.engine_class)
                + ", viewer_class = " + DBManager.StringValue(game.viewer_class)
                + ", uses_viewer = " + DBManager.BoolValue(game.uses_viewer)
                + " WHERE id = " + game.id;

        DBManager.Execute(query);
    }

    public static void SaveTwitterConfig (int id)
    {
        TwitterConfig tc = GetTwitterConfig(id);
        if (tc == null)
            return;

        String query = "UPDATE " + Entities.TWITTER_CONFIG.TableName()
                + " SET account_name = " + DBManager.StringValue(tc.account_name)
                + ", access_token = " + DBManager.StringValue(tc.access_token)
                + ", access_token_secret = " + DBManager.StringValue(tc.access_token_secret)
                + ", consumer_key = " + DBManager.StringValue(tc.consumer_key)
                + ", consumer_secret = " + DBManager.StringValue(tc.consumer_secret)
                + " WHERE id = " + tc.id;

        DBManager.Execute(query);
    }
}
