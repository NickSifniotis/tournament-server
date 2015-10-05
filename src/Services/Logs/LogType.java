package Services.Logs;

import Common.SystemState;

/**
 * Created by nsifniotis on 13/09/15.
 *
 * An enumeration that holds the data needed for the LogService to run.
 *
 * Good lord this server is unnecessarily featurific.
 *
 */
public enum LogType
{
    ERROR, TOURNAMENT, SQL, GAME;


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Returns the paths that logs are stored in.
     *
     * @return a path within a string.
     */
    public String LogPath()
    {
        String res = "";
        switch (this)
        {
            case ERROR:
                res = SystemState.error_log_path;
                break;
            case TOURNAMENT:
                res = SystemState.tournament_log_path;
                break;
            case SQL:
                res = SystemState.sql_log_path;
                break;
            case GAME:
                res = SystemState.game_log_path;
                break;
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 13/9/2015
     *
     * Returns true if logging is enabled for the type of log that is being enquired about.
     *
     * @return - true if logs are being recorded, false otherwise.
     */
    public boolean Logging()
    {
        boolean res = false;

        switch (this)
        {
            case ERROR:
                res = SystemState.ERROR_LOGS;
                break;
            case TOURNAMENT:
                res = SystemState.THREAD_LOGS;
                break;
            case SQL:
                res = SystemState.SQL_LOGS;
                break;
            case GAME:
                res = false;                // Game logs live in a seperate universe.
                break;
        }

        return res;
    }
}
