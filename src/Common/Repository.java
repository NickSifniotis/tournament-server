package Common;


import Common.DataModelObject.Entities;
import Common.DataModelObject.Entity;
import Common.DataModelObject.GameType;
import Common.DataModelObject.TwitterConfig;
import Common.Logs.LogManager;
import Common.Logs.LogType;
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
    private static HashMap<Integer, Entity> twitter_configs;
    private static HashMap<Integer, Entity> game_types;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Load up from the database all things.
     */
    public static void Initialise()
    {
        game_types = load_from_table(Entities.GAME_TYPE);
        twitter_configs = load_from_table(Entities.TWITTER_CONFIG);
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
    public static GameType GetGameType (int id) { return (GameType) game_types.get(id); }

    public static GameType[] GetGameTypes ()
    {
        GameType[] res = (GameType[]) game_types.values().toArray();
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
    public static GameType NewGameType()
    {
        GameType newb = (GameType) new_entity(Entities.GAME_TYPE);
        game_types.putIfAbsent(newb.id, newb);

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
        String query = "INSERT INTO " + entity.TableName();
        int id = DBManager.ExecuteReturnKey(query);

        Entity newb = null;
        try
        {
            newb = (Entity) entity.Class().newInstance();
            newb.id = id;
        }
        catch (Exception e)
        {
            String error = "Error instantiating new " + entity.Class().getName() + ": " + e;;
            LogManager.Log(LogType.ERROR, error);
        }

        return newb;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Saves this game type.
     *
     * @param id - the game type record to save
     */
    public static void SaveGameType (int id)
    {
        GameType game = (GameType) game_types.get(id);
        if (game == null)
            return;         // should never happen

        String query = "UPDATE game_type SET name = '" + game.name
                + "', min_players = " + game.min_players
                + ", max_players = " + game.max_players
                + ", engine_class = '" + game.engine_class
                + "', viewer_class = '" + game.viewer_class
                + "', uses_viewer = " + DBManager.BoolValue(game.uses_viewer)
                + " WHERE id = " + game.id;

        DBManager.Execute(query);
    }
}
