package Common;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Development system variables
 *
 */
public class SystemState {

    public static final boolean DEBUG = true;
    public static final boolean LOGGING = true;

    private static final String log_file = "log.txt";

    public static final String marshalling_folder = "src/marshalling/";
    public static final String input_folder = "src/submissions/inputs/";
    public static final String sources_folder = "src/sources/";
    public static final String sources_classpath = "AcademicsInterface";


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Adds an entry to the program's log file.
     *
     * @param text - the entry to add.
     */
    public static void Log (String text)
    {
        if (!LOGGING)
            return;

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(log_file, true))))
        {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = new Date();
            String strDate = sdfDate.format(now);

            out.println (strDate + ": " + text);
        }
        catch (IOException e)
        {
            System.out.println ("ERROR APPENDING TO LOG FILE.\nTHE ENTIRE PROGRAM IS FUCKED RUN FOR THE HILLS.");
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Initialises the system.
     * Which means, deletes the log file.
     *
     */
    public static void initialise()
    {
        File logFile = new File (log_file);
        logFile.delete();
    }
}
