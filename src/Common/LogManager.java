package Common;

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
    private static String game_log_path = "logs/games/";
    private static String sql_log_path = "logs/system/sql/";
    private static String error_log_path = "logs/system/errors/";
    private static String thread_log_path = "logs/system/threads";

    private static String sql_logfile;
    private static String error_logfile;
    private static String thread_logfile;


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Adds an entry to the current session's SQL logs
     * If they don't exist, create them.
     *
     * @param entry - the entry to record.
     */
    public static void DB_Log (String entry)
    {
        if (!SystemState.SQL_LOGS)
            return;


        // if there's no log file for this session, create one.
        if (sql_logfile.equals(""))
            sql_logfile = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sql_log_path + sql_logfile, true))))
        {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = new Date();
            String strDate = sdfDate.format(now);

            out.println (strDate + ": " + entry);
        }
        catch (IOException e)
        {
            System.out.println ("ERROR APPENDING TO SQL LOG FILE.\nTHE ENTIRE PROGRAM IS FUCKED RUN FOR THE HILLS.");
        }
    }

}
