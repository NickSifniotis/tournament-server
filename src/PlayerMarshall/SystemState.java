package PlayerMarshall;

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
}
