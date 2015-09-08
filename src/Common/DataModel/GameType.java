package Common.DataModel;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IViewer;
import Common.DBManager;
import Common.SystemState;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsifniotis on 8/09/15.
 *
 * Data model object for records of the table 'game_type'
 * Database schema is as follows
 * id               int primary key
 * name             varchar (30)
 * engine_class     varchar (30)
 * viewer_class     varchar (30)
 * uses_viewer      boolean
 * min_players      int
 * max_players      int
 *
 */
public class GameType
{
    private int id;
    private String name;
    private String engine_class;
    private String viewer_class;
    private boolean uses_viewer;
    private int min_players;
    private int max_players;


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Default blank constructor.
     *
     */
    public GameType ()
    {
        loadState();
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Constructor - load by primary key.
     * @param id
     */
    public GameType (int id)
    {
        String query;

        if (id != 0)
        {
            query =  "SELECT * FROM game_type WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                try
                {
                    res.next();
                    this.loadState(res);
                    DBManager.disconnect(res);          // disconnect by result
                }
                catch (Exception e)
                {
                    String error = "GameType constructor (game_id) - SQL error retrieving player data. " + e;
                    SystemState.Log(error);

                    if (SystemState.DEBUG)
                        System.out.println (error);

                    this.loadState();
                }
            }
            else
            {
                this.loadState();
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Constructor - load by recordset.
     *
     * @param input - the database recordset to load from
     */
    public GameType (ResultSet input)
    {
        try
        {
            this.loadState(input);
        }
        catch (Exception e)
        {
            String error = "GameType constructor (recordset input) - SQL error retrieving player data. " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

        }
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Returns an array of game_type objects corresponding to every entry in the database.
     *
     * @return
     */
    public static GameType[] LoadAll()
    {
        SystemState.Log("GameType.LoadAll - attempting to load all");
        List<GameType> temp = new ArrayList<>();

        String query = "SELECT * FROM game_type";
        Connection con = DBManager.connect();
        ResultSet results = DBManager.ExecuteQuery(query, con);
        try
        {
            while (results.next())
            {
                GameType t = new GameType(results);
                temp.add (t);
            }

            DBManager.disconnect(results);
        }
        catch (Exception e)
        {
            String error = "GameType.LoadAll - Error executing SQL query: " + query + ": " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }

        int size = temp.size();
        GameType [] res = new GameType[size];
        SystemState.Log("GameType.LoadAll - returning " + size + " game types.");

        return temp.toArray(res);
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Pull the data from the database record that has been provided.
     *
     * @param input - a recordset pointing to the data we want to exctract.
     * @throws SQLException - the data may not exist, or some such.
     */
    private void loadState (ResultSet input) throws SQLException
    {
        this.id = input.getInt("id");
        this.name = input.getString("name");
        this.engine_class = input.getString("engine_class");
        this.viewer_class = input.getString("viewer_class");
        this.uses_viewer = (input.getInt("uses_viewer") == 1);
        this.min_players = input.getInt("min_players");
        this.max_players = input.getInt("max_players");
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * The default constructor, so to speak.
     *
     */
    private void loadState ()
    {
        this.id = 0;
        this.name = "";
        this.engine_class = "";
        this.viewer_class = "";
        this.uses_viewer = false;
        this.min_players = 0;
        this.max_players = 0;
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Accessor functions, including two interesting functions that return instances of interfaces.
     *
     * @return
     */
    public String Name () { return this.name; }
    public int MinPlayers () { return this.min_players; }
    public int MaxPlayers () { return this.max_players; }
    public boolean UsesViewer () { return this.uses_viewer; }
    public String GameEngineClass () { return this.engine_class; }
    public String ViewerClass () { return this.viewer_class; }

    public IGameEngine GameEngine () {
        if (this.engine_class.equals(""))
            return null;

        IGameEngine res;
        String fullFileName = SystemState.engines_folder + this.id + ".jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            Class source_class = playerClassLoader.loadClass(this.engine_class);

            if (!IGameEngine.class.isAssignableFrom(source_class))
                throw new ClassNotFoundException("The class does not correctly implement IGameEngine");

            res = (IGameEngine) source_class.newInstance();

        } catch (Exception e) {
            String error = "GameType.GameEngine - error creating class: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println(error);

            return null;
        }

        return res;
    }

    public IViewer Viewer ()
    {
        if (this.viewer_class.equals("") || !this.uses_viewer)
            return null;

        IViewer res;
        String fullFileName = SystemState.engines_folder + this.id + ".jar";

        try
        {
            URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            Class source_class = playerClassLoader.loadClass(this.viewer_class);

            if (!IViewer.class.isAssignableFrom(source_class))
                throw new ClassNotFoundException("The class does not correctly implement IViewer");

            res = (IViewer) source_class.newInstance();

        } catch (Exception e) {
            String error = "GameType.Viewer - error creating class: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println(error);

            return null;
        }

        return res;
    }


    @Override
    public String toString ()
    {
        return this.Name();
    }
}
