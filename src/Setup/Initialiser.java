package Setup;

import Common.DBManager;
import Common.SystemState;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nsifniotis on 22/09/15.
 *
 * Contains methods for creating the database tables that this system uses.
 *
 */
public class Initialiser
{
    /**
     * Nick Sifniotis u5809912
     * 22/09/2015
     *
     * Reboot the entire damn system.
     *
     * @param args - no args.
     */
    public static void main(String[] args)
    {
        CreateFileSystem();
        CreateTables();
    }


    /**
     * Nick Sifniotis u5809912
     * 22/09/2015
     *
     * Drop and reconstruct all the database tables that the system uses...
     */
    public static void CreateTables()
    {
        DBManager.Execute("DROP TABLE IF EXISTS game");
        DBManager.Execute("CREATE TABLE game (id integer primary key, tournament_id integer, "
                + "round_number integer, game_number integer, played boolean, "
                + "in_progress boolean)");

        DBManager.Execute("INSERT INTO game (tournament_id, round_number, game_number)" +
                        " VALUES (1, 1, 1)");
        DBManager.Execute("INSERT INTO game (tournament_id, round_number, game_number)" +
                        " VALUES (1, 2, 1)");

        DBManager.Execute("DROP TABLE IF EXISTS game_player");
        DBManager.Execute("CREATE TABLE game_player (id integer primary key, position integer, "
                + "game_id integer, fixture_slot_id integer)");

        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (1, 1, 1)");
        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (2, 1, 2)");
        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (3, 1, 3)");
        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (4, 1, 4)");
        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (4, 1, 1)");
        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (3, 1, 2)");
        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (2, 1, 3)");
        DBManager.Execute("INSERT INTO game_player (position, game_id, fixture_slot_id)" +
                " VALUES (1, 1, 4)");

        DBManager.Execute("DROP TABLE IF EXISTS fixture_slot");
        DBManager.Execute("CREATE TABLE fixture_slot (id integer primary key, tournament_id integer, "
                + "submission_id integer)");

        DBManager.Execute("INSERT INTO fixture_slot (tournament_id) VALUES (1)");
        DBManager.Execute("INSERT INTO fixture_slot (tournament_id) VALUES (1)");
        DBManager.Execute("INSERT INTO fixture_slot (tournament_id) VALUES (1)");
        DBManager.Execute("INSERT INTO fixture_slot (tournament_id) VALUES (1)");

        DBManager.Execute("DROP TABLE IF EXISTS tournament");
        DBManager.Execute("CREATE TABLE tournament (id integer primary key, game_id integer, name text, "
                + "player_interface_class text, verification_class text, num_players integer, "
                + "timeout integer, allow_resubmit boolean, use_null_moves boolean,"
                + "allow_submit boolean, game_on boolean, twitter_config_id integer)");

        DBManager.Execute("INSERT INTO tournament (game_id, name, player_interface_class, verification_class," +
                "num_players, timeout, allow_resubmit, use_null_moves, allow_submit, game_on, twitter_config_id)" +
                " VALUES (1, 'Test Tourney', 'BlokusPlayer', 'BlokusPlayer', 4, 10, 0, 1, 1, 0, 1)");

        DBManager.Execute("DROP TABLE IF EXISTS submission");
        DBManager.Execute("CREATE TABLE submission (id integer primary key, tournament_id integer, "
                + "team_name text, team_email text, team_avatar boolean, playing boolean, retired boolean, "
                + "disqualified boolean)");

        DBManager.Execute("DROP TABLE IF EXISTS game_type");
        DBManager.Execute("CREATE TABLE game_type (id integer primary key, name text, engine_class text, "
                + "viewer_class text, min_players integer, max_players integer, uses_viewer boolean)");

        DBManager.Execute("DROP TABLE IF EXISTS point_structure");
        DBManager.Execute("CREATE TABLE point_structure (id integer primary key, tournament_id integer, "
                + "position integer, points integer)");

        DBManager.Execute("INSERT INTO point_structure (tournament_id, position, points)" +
                " VALUES (1, 0, 6)");
        DBManager.Execute("INSERT INTO point_structure (tournament_id, position, points)" +
                " VALUES (1, 1, 4)");
        DBManager.Execute("INSERT INTO point_structure (tournament_id, position, points)" +
                " VALUES (1, 2, 2)");
        DBManager.Execute("INSERT INTO point_structure (tournament_id, position, points)" +
                " VALUES (1, 3, 0)");

        DBManager.Execute("DROP TABLE IF EXISTS score");
        DBManager.Execute("CREATE TABLE score (id integer primary key, submission_id integer, "
                + "game_id integer, score integer, no_score boolean, disqualified boolean)");

        DBManager.Execute("DROP TABLE IF EXISTS twitter_configuration");
        DBManager.Execute("CREATE TABLE twitter_configuration (id integer primary key, account_name string, "
                + "consumer_key string, consumer_secret string, access_token string, access_token_secret string)");
    }


    /**
     * Nick Sifniotis u5809912
     * 22/09/2015
     *
     * Creates the filesystem for use by the tournament.
     *
     * Also wipes the slate clean and deletes the contents of the system's directory structure.
     *
     * Between this and CreateTables() the entire system is completely reset.
     */
    public static void CreateFileSystem()
    {
        process_directory(SystemState.database_folder);
        process_directory(SystemState.engines_folder);
        process_directory(SystemState.interfaces_folder);
        process_directory(SystemState.marshalling_folder);
        process_directory(SystemState.pictures_folder);
        process_directory(SystemState.game_log_path);
        process_directory(SystemState.error_log_path);
        process_directory(SystemState.sql_log_path);
        process_directory(SystemState.tournament_log_path);
    }


    /**
     * Nick Sifniotis u5809912
     * 22/09/2015
     *
     * Processes a single directory within the system structure.
     * @param directory - the directory to process
     */
    private static void process_directory(String directory)
    {
        Path dir = Paths.get(directory);
        if (Files.exists(dir))
        {
            File folder = new File (directory);
            File[] listOfFiles = folder.listFiles();

            try
            {
                for (File f : listOfFiles)
                    if (!f.isDirectory())
                        Files.delete(Paths.get(f.getAbsolutePath()));
            }
            catch (Exception e)
            {
                System.out.println ("Unable to delete file: " + e);
            }
        }
        else
        {
            try
            {
                Files.createDirectory(dir);
            }
            catch (Exception e)
            {
                System.out.println ("Unable to create directory for the database: " + e);
            }
        }
    }
}
