package Common;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * System constants.
 *
 */
public class SystemState
{
    public static class Email
    {
        public static final String userName = "chasedaw";
        public static final String password = "b64094bf";
        public static final String host = "vmcp17.digitalpacific.com.au";
        public static final int port = 465;

        public static final String fromAddress = "u5809912@anu.edu.au";

        public static final String templates_folder = "email_templates/";

    }

    public static class Resources
    {
        public static ImageView server_start;
        public static ImageView server_stop;
        public static ImageView server_reset;
    }

    public static class Database
    {
        public static final String File = "tournaments.db";
    }


    public static class Folders
    {
        public static final String GameEngines = "game_engines/";
        public static final String PlayerInterfaces = "player_interfaces/";
        public static final String Submissions = "submissions/";
        public static final String Marshalling = "marshalling/";
        public static final String Pictures = "marshalling/pictures/";
        public static final String Database = "database/";

        public static class Logs
        {
            public static final String Root = "logs/";
            public static final String Error = "logs/system/errors/";
            public static final String SQL = "logs/system/sql/";
            public static final String Threads = "logs/system/threads/";
            public static final String Game = "logs/games/";
        }
    }

    public static final boolean DEBUG = true;

    public static final boolean SQL_LOGS = true;
    public static final boolean THREAD_LOGS = true;
    public static final boolean ERROR_LOGS = true;

    public static final int DEFAULT_TIMEOUT = 1800;     // half an hour is long enough


    public static void Initialise()
    {
        Resources.server_reset = new ImageView(new Image ("file:images/reset"));
        Resources.server_start = new ImageView(new Image ("file:images/server_start"));
        Resources.server_stop = new ImageView(new Image ("file:images/server_stop"));

        Resources.server_reset.setFitWidth(25);
        Resources.server_stop.setFitWidth(25);
        Resources.server_start.setFitWidth(25);

        Resources.server_start.setPreserveRatio(true);
        Resources.server_reset.setPreserveRatio(true);
        Resources.server_stop.setPreserveRatio(true);
    }
}
