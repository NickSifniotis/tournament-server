package Common.Logs;

import Common.SystemState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nsifniotis on 13/09/15.
 *
 * A class to manage the generation of logs.
 * There are a lot of logs in this server .. a lot of logs.
 * The SQL logs alone are huge.
 *
 */
public class LogManager
{
    private static String [] logfiles = new String[LogType.values().length];


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Adds an entry to the current session's logs
     * If they don't exist, create them.
     *
     * @param type - which log the entry should be piped through to
     * @param entry - the entry to record.
     */
    public static void Log (LogType type, String entry)
    {
        // this test will also neatly return false if type == LogType.GAME
        if (!type.Logging())
            return;


        // if there's no log file for this session, create one.
        if (logfiles[type.ordinal()].equals(""))
            logfiles[type.ordinal()] = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(type.LogPath() + logfiles[type.ordinal()], true))))
        {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = new Date();
            String strDate = sdfDate.format(now);

            out.println (strDate + ": " + entry);
        }
        catch (IOException e)
        {
            System.out.println ("ERROR APPENDING TO LOG FILE.\nTHE ENTIRE PROGRAM IS FUCKED RUN FOR THE HILLS.");
        }

        if (type == LogType.ERROR && SystemState.DEBUG)
            System.out.println (entry);
    }


    public static void GameLog (int game_id, String entry)
    {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LogType.GAME.LogPath() + game_id + ".txt", true))))
        {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = new Date();
            String strDate = sdfDate.format(now);

            out.println (strDate + ": " + entry);
        }
        catch (IOException e)
        {
            System.out.println ("ERROR APPENDING TO GAME LOG FILE.\nTHE ENTIRE PROGRAM IS FUCKED RUN FOR THE HILLS.");
        }

    }
}
