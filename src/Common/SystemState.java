package Common;


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
        public static final String userName = "blokus_tournament@hotmail.com";;
        public static final String password = "b64094bf";
        public static final String host = "smtp-mail.outlook.com";
        public static final int port = 587;

        public static final String templates_folder = "email_templates/";

    }

    public static final boolean DEBUG = true;

    public static final boolean SQL_LOGS = false;
    public static final boolean THREAD_LOGS = true;
    public static final boolean ERROR_LOGS = true;


    // new directory structure
    public static final String engines_folder = "game_engines/";
    public static final String interfaces_folder = "player_interfaces/";
    public static final String input_folder = "submissions/";
    public static final String marshalling_folder = "marshalling/";
    public static final String pictures_folder = "marshalling/pictures/";

    // locations of log files
    public static final String error_log_path = "logs/system/errors/";
    public static final String sql_log_path = "logs/system/sql/";
    public static final String tournament_log_path = "logs/system/threads/";
    public static final String game_log_path = "logs/games/";

    public static final int DEFAULT_TIMEOUT = 1800;     // half an hour is long enough
}
