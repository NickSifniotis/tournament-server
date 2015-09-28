package Common.DataModelObject;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IViewer;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import Common.SystemState;

import java.net.URL;
import java.net.URLClassLoader;
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


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Accessor functions.
     *
     * @return the things
     */
    public String SourceFilename () { return SystemState.engines_folder + this.id + ".jar"; }

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

        }
        catch (Exception e)
        {
            String error = "GameType.GameEngine - error creating class: " + e;
            LogManager.Log(LogType.ERROR, error);

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

        }
        catch (Exception e)
        {
            String error = "GameType.Viewer - error creating class: " + e;
            LogManager.Log(LogType.ERROR, error);

            return null;
        }

        return res;
    }
}
