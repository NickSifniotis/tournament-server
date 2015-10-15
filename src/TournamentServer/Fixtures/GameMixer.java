package TournamentServer.Fixtures;

import Common.DBManager;
import Common.SystemState;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nsifniotis on 15/10/15.
 *
 * BLOKUS DAY
 */
public class GameMixer
{

    public static void main(String[] args) {
        build(4);
    }
    /**
     * Nick Sifniotis u5809912
     * 15/10/2015
     *
     * BLOKUS DAY
     *
     * Unselect the number of players that there are available, and then execute this PSVM to create the fixture.
     * @param number - how many players are playing in this tournament
     */
    public static void build(int number)
    {
        switch(number)
        {
            case 4:
                four_player();
                break;
            case 5:
                five_player();
                break;
            case 6:
                six_player();
                break;
            case 7:
                seven_player();
                break;
            case 8:
                eight_player();
                break;
            case 9:
                nine_player();
                break;
            case 10:
                ten_player();
                break;
            default:
                System.out.println("Unable to build a tournament with " + number + " players. This will have to be done manually.");
                break;
        }
    }


    private static void eight_player()
    {
        String [] games = {
        "01234567",
        "01243567",
        "01253467",
        "01263457",
        "01273456",
        "01342567",
        "01352467",
        "01362457",
        "01372456",
        "01452367",
        "01462357",
        "01472356",
        "01562347",
        "01572346",
        "01672345",
        "02341567",
        "02351467",
        "02361457",
        "02371456",
        "02451367",
        "02461357",
        "02471356",
        "02561347",
        "02571346",
        "02671345",
        "03451267",
        "03461257",
        "03471256",
        "03561247",
        "03571246",
        "03671245",
        "04561237",
        "04571236",
        "04671235",
        "05671234" };

        // set up the array with the games that will be played.
        String [] full_games = new String [35];
        int counter = 0;
        for (int i = 0; i < 1; i ++)
            for (int j = 0; j < games.length; j ++)
            {
                full_games[counter] = games[j];
                counter ++;
            }

        full_games = Reduce2(full_games);

        GenerateSQL2(full_games, 8);
    }


    private static void seven_player()
    {
        String[] games = {
        "0123",
        "0124",
        "0125",
        "0126",
        "0134",
        "0135",
        "0136",
        "0145",
        "0146",
        "0156",
        "0234",
        "0235",
        "0236",
        "0245",
        "0246",
        "0256",
        "0345",
        "0346",
        "0356",
        "0456",
        "1234",
        "1235",
        "1236",
        "1245",
        "1246",
        "1256",
        "1345",
        "1346",
        "1356",
        "1456",
        "2345",
        "2346",
        "2356",
        "2456",
        "3456" };


        // set up the array with the games that will be played.
        String [] full_games = new String [70];
        int counter = 0;
        for (int i = 0; i < 2; i ++)
            for (int j = 0; j < games.length; j ++)
            {
                full_games[counter] = games[j];
                counter ++;
            }

        full_games = Reduce(full_games);

        GenerateSQL(full_games, 7);
    }


    private static void six_player()
    {
        String [] games = {
        "0123",
        "0124",
        "0125",
        "0134",
        "0135",
        "0145",
        "0234",
        "0235",
        "0245",
        "0345",
        "1234",
        "1235",
        "1245",
        "1345",
        "2345" };


        // set up the array with the games that will be played.
        String [] full_games = new String [60];
        int counter = 0;
        for (int i = 0; i < 4; i ++)
            for (int j = 0; j < games.length; j ++)
            {
                full_games[counter] = games[j];
                counter ++;
            }

        full_games = Reduce(full_games);

        GenerateSQL(full_games, 6);
    }

    private static void five_player()
    {
        String [] games = { "0124",
                "0123",
                "0134",
                "0234",
                "1234" };

        // set up the array with the games that will be played.
        String [] full_games = new String [60];
        int counter = 0;
        for (int i = 0; i < 12; i ++)
            for (int j = 0; j < games.length; j ++)
            {
                full_games[counter] = games[j];
                counter ++;
            }

        full_games = Reduce(full_games);

        GenerateSQL(full_games, 5);
    }

    private static void four_player ()
    {
        String [] games = { "0123" };

        // set up the array with the games that will be played.
        String [] full_games = new String [24];
        int counter = 0;
        for (int i = 0; i < 24; i ++)
            for (int j = 0; j < games.length; j ++)
            {
                full_games[counter] = games[j];
                counter ++;
            }

        full_games = Reduce(full_games);

        GenerateSQL(full_games, 4);
    }

    private static void nine_player ()
    {
        String [] full_games = { "01234567", "04782356", "02681357", "03461258", "03581247", "02451678", "01562348", "02371468", "05781346", "13482567", "01453678", "01372458", "01263578", "01682347", "12783456", "04671235", "14572368", "04581267", "02473568", "01372468", "02561348", "03462578", "02381567", "06781245", "01583467", "03571246", "01234578", "01482356", "02471568", "03452678",
        };


        full_games = Reduce2(full_games);

        GenerateSQL2(full_games, 9);
    }


    private static void ten_player ()
    {
        String [] full_games = {"01234567", "12793468", "02681359", "06792458", "03491578", "01482357", "01563789", "05891246", "03472569", "13682479", "02781459", "02351678", "01473689", "24893567", "05691234", "12694578", "01582367", "02461389", "01793456", "03492578", "02381467", "12354689", "03672459", "05681279", "01453789", "12683457", "02591348", "04783569", "02671589", "01234679",
        };

        full_games = Reduce2(full_games);
        GenerateSQL2(full_games, 10);
    }

    private static String [] Reduce2(String [] full_games)
    {
        // do the main thing.
        int uniqueness_score = score2(full_games);
        int last_uniq_score = 0;
        boolean done = false;

        while (!done) {
            for (int i = 0; i < full_games.length; i++) {
                String temp = full_games[i];
                String first = temp.substring(0, 4);
                String second = temp.substring(4, 8);

                int permutation = 0;
                int temp_score = uniqueness_score;

                // try thr first game
                while (permutation < 24 && uniqueness_score <= temp_score) {
                    full_games[i] = permute(permutation, first) + second;
                    temp_score = score2(full_games);
                    permutation++;
                }

                // ok, try the second game
                if (permutation == 24 && uniqueness_score <= temp_score)
                {
                    permutation = 0;
                    while (permutation < 24 && uniqueness_score <= temp_score) {
                        full_games[i] = first + permute(permutation, second);
                        temp_score = score2(full_games);
                        permutation++;
                    }
                }

                if (uniqueness_score < temp_score)
                    full_games[i] = temp;

                if (temp_score < uniqueness_score)
                    uniqueness_score = temp_score;

            }

            if (last_uniq_score == uniqueness_score)
                done = true;

            last_uniq_score = uniqueness_score;

            System.out.print("PASS: " + uniqueness_score + ":" + last_uniq_score + "  GAMES: ");
            for (int i = 0; i < full_games.length; i++)
                System.out.print(full_games[i] + ":");

            System.out.println();
        }

        return full_games;
    }

    private static String [] Reduce (String [] full_games)
    {
        // do the main thing.
        int uniqueness_score = score(full_games);
        int last_uniq_score = 0;
        boolean done = false;

        while (!done) {
            for (int i = 0; i < full_games.length; i++) {
                String temp = full_games[i];

                int permutation = 0;
                int temp_score = uniqueness_score;
                while (permutation < 24 && uniqueness_score <= temp_score) {
                    full_games[i] = permute(permutation, temp);
                    temp_score = score(full_games);
                    permutation++;
                }

                if (uniqueness_score < temp_score)
                    full_games[i] = temp;

                if (temp_score < uniqueness_score)
                    uniqueness_score = temp_score;

            }

            if (last_uniq_score == uniqueness_score)
                done = true;

            last_uniq_score = uniqueness_score;

            System.out.print("PASS: " + uniqueness_score + ":" + last_uniq_score + "  GAMES: ");
            for (int i = 0; i < full_games.length; i++)
                System.out.print(full_games[i] + ":");

            System.out.println();
        }

        return full_games;
    }

    private static void GenerateSQL2(String [] games, int num_players)
    {
        String query;
        int [] slot_priikeys = new int[num_players];

        query = "INSERT INTO tournament (game_id, name, player_interface_class, verification_class," +
                "num_players, timeout, allow_resubmit, use_null_moves, allow_submit, game_on, twitter_config_id)" +
                "VALUES (1, 'Blokus " + num_players +" player', 'BlokusPlayer', 'BlokusPlayer', " + num_players + ", 10, 0, 1, 1, 0, 1)";
        System.out.println (query);
        int tournament_id = DBManager.ExecuteReturnKey(query);

        try {
            Files.createDirectory(Paths.get(SystemState.Folders.Submissions + tournament_id + "/"));
        }
        catch (Exception e)
        {
            System.out.println("Error creating dir: " + e);
        }

        for (int i = 0; i < num_players; i ++)
        {
            query = "INSERT INTO fixture_slot (tournament_id, submission_id) VALUES (" + tournament_id + ", 0)";
            slot_priikeys[i] = DBManager.ExecuteReturnKey(query);

            System.out.println ("Executing " + query);
        }

        int round_number = 1;
        for (String g: games)
        {
            query = "INSERT INTO game (tournament_id, round_number, game_number, played, in_progress)" +
                    " VALUES (" + tournament_id + ", " + round_number + ", 1, 0, 0)";
            System.out.println ("Executing " + query);
            int game_id = DBManager.ExecuteReturnKey(query);

            for (int i = 0; i < 4; i ++)
            {
                query = "INSERT INTO game_player (position, game_id, fixture_slot_id) VALUES "
                        + "(" + i + ", " + game_id + ", " + slot_priikeys[Integer.parseInt(g.substring(i, i + 1))] + ")";
                System.out.println (query);
                DBManager.Execute(query);
            }

            query = "INSERT INTO game (tournament_id, round_number, game_number, played, in_progress)" +
                    " VALUES (" + tournament_id + ", " + round_number + ", 2, 0, 0)";
            System.out.println ("Executing " + query);
            game_id = DBManager.ExecuteReturnKey(query);

            for (int i = 4; i < 8; i ++)
            {
                query = "INSERT INTO game_player (position, game_id, fixture_slot_id) VALUES "
                        + "(" + i + ", " + game_id + ", " + slot_priikeys[Integer.parseInt(g.substring(i, i + 1))] + ")";
                System.out.println (query);
                DBManager.Execute(query);
            }

            round_number++;
        }
    }


    private static void GenerateSQL(String [] games, int num_players)
    {
        String query;
        int [] slot_priikeys = new int[num_players];

        query = "INSERT INTO tournament (game_id, name, player_interface_class, verification_class," +
                "num_players, timeout, allow_resubmit, use_null_moves, allow_submit, game_on, twitter_config_id)" +
                "VALUES (1, 'Blokus " + num_players +" player', 'BlokusPlayer', 'BlokusPlayer', " + num_players + ", 10, 0, 1, 1, 0, 1)";
        System.out.println (query);
        int tournament_id = DBManager.ExecuteReturnKey(query);

        try {
            Files.createDirectory(Paths.get(SystemState.Folders.Submissions + tournament_id + "/"));
        }
        catch (Exception e)
        {
            System.out.println("Error creating dir: " + e);
        }

        for (int i = 0; i < num_players; i ++)
        {
            query = "INSERT INTO fixture_slot (tournament_id, submission_id) VALUES (" + tournament_id + ", 0)";
            slot_priikeys[i] = DBManager.ExecuteReturnKey(query);

            System.out.println ("Executing " + query);
        }

        int round_number = 1;
        for (String g: games)
        {
            query = "INSERT INTO game (tournament_id, round_number, game_number, played, in_progress)" +
                    " VALUES (" + tournament_id + ", " + round_number + ", 1, 0, 0)";
            System.out.println ("Executing " + query);
            int game_id = DBManager.ExecuteReturnKey(query);

            for (int i = 0; i < g.length(); i ++)
            {
                query = "INSERT INTO game_player (position, game_id, fixture_slot_id) VALUES "
                        + "(" + i + ", " + game_id + ", " + slot_priikeys[Integer.parseInt(g.substring(i, i + 1))] + ")";
                System.out.println (query);
                DBManager.Execute(query);
            }

            round_number++;
        }
    }


    private static String permute(int permutation, String input)
    {
        String res = null;
        char[] chars = input.toCharArray();

        switch (permutation)
        {
            case 0:
                res = buildString(chars[0], chars[1], chars[2], chars[3]);
                break;
            case 1:
                res = buildString(chars[0], chars[1], chars[3], chars[2]);
                break;
            case 2:
                res = buildString(chars[0], chars[2], chars[1], chars[3]);
                break;
            case 3:
                res = buildString(chars[0], chars[2], chars[3], chars[1]);
                break;
            case 4:
                res = buildString(chars[0], chars[3], chars[2], chars[1]);
                break;
            case 5:
                res = buildString(chars[0], chars[3], chars[1], chars[2]);
                break;
            case 6:
                res = buildString(chars[1], chars[0], chars[2], chars[3]);
                break;
            case 7:
                res = buildString(chars[1], chars[0], chars[3], chars[2]);
                break;
            case 8:
                res = buildString(chars[1], chars[2], chars[0], chars[3]);
                break;
            case 9:
                res = buildString(chars[1], chars[2], chars[3], chars[0]);
                break;
            case 10:
                res = buildString(chars[1], chars[3], chars[2], chars[0]);
                break;
            case 11:
                res = buildString(chars[1], chars[3], chars[0], chars[2]);
                break;
            case 12:
                res = buildString(chars[2], chars[1], chars[0], chars[3]);
                break;
            case 13:
                res = buildString(chars[2], chars[1], chars[3], chars[0]);
                break;
            case 14:
                res = buildString(chars[2], chars[0], chars[1], chars[3]);
                break;
            case 15:
                res = buildString(chars[2], chars[0], chars[3], chars[1]);
                break;
            case 16:
                res = buildString(chars[2], chars[3], chars[0], chars[1]);
                break;
            case 17:
                res = buildString(chars[2], chars[3], chars[1], chars[0]);
                break;
            case 18:
                res = buildString(chars[3], chars[1], chars[2], chars[0]);
                break;
            case 19:
                res = buildString(chars[3], chars[1], chars[0], chars[2]);
                break;
            case 20:
                res = buildString(chars[3], chars[2], chars[1], chars[0]);
                break;
            case 21:
                res = buildString(chars[3], chars[2], chars[0], chars[1]);
                break;
            case 22:
                res = buildString(chars[3], chars[0], chars[2], chars[1]);
                break;
            case 23:
                res = buildString(chars[3], chars[0], chars[1], chars[2]);
                break;
        }

        return res;
    }

    private static String buildString (char a, char b, char c, char d)
    {
        char[] res = { a, b, c, d };
        return new String (res);
    }


    private static int score2 (String [] games)
    {
        int res = 0;

        int [][] counts = new int [20][8];
        for (int i = 0; i < counts.length; i ++)
            for (int j = 0; j < 4; j ++)
                counts[i][j] = 0;

        for (int i = 0; i < games.length; i ++) {
            for (int j = 0; j < 4; j++)
                counts[Integer.parseInt(games[i].substring(j, j + 1))][j]++;
            for (int j = 4; j < 8; j++)
                counts[Integer.parseInt(games[i].substring(j, j + 1))][j]++;
        }

        for (int i = 0; i < counts.length; i ++)
        {
            for (int j = 0; j < 4; j++)
            {
                for (int k = 0; k < 4; k ++)
                    res += Math.abs(counts[i][j] - counts[i][k]);
            }
        }

        return res;
    }


    private static int score (String [] games)
    {
        int res = 0;

        int [][] counts = new int [20][4];
        for (int i = 0; i < counts.length; i ++)
            for (int j = 0; j < 4; j ++)
                counts[i][j] = 0;

        for (int i = 0; i < games.length; i ++)
            for (int j = 0; j < games[i].length(); j ++)
                counts[Integer.parseInt(games[i].substring(j, j+1))][j] ++;

        for (int i = 0; i < counts.length; i ++)
        {
            for (int j = 0; j < 4; j++)
            {
                for (int k = 0; k < 4; k ++)
                    res += Math.abs(counts[i][j] - counts[i][k]);
            }
        }

        return res;
    }
}
