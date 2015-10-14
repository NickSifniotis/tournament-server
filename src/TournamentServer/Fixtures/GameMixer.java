package TournamentServer.Fixtures;

import Common.DBManager;

/**
 * Created by nsifniotis on 15/10/15.
 */
public class GameMixer
{

    public static void main(String[] args)
    {
        five_player();
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

        Reduce(full_games);
    }


    private static void Reduce (String [] full_games)
    {
        // do the main thing.
        int uniqueness_score = score(full_games);
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

                if (uniqueness_score == temp_score || uniqueness_score == 0)
                    done = true;
                else if (temp_score < uniqueness_score)
                    uniqueness_score = temp_score;
            }

            System.out.print("PASS: " + uniqueness_score + "  GAMES: ");
            for (int i = 0; i < full_games.length; i++)
                System.out.print(full_games[i] + ":");

            System.out.println();
        }


        GenerateSQL(full_games);
    }


    private static void GenerateSQL(String [] games)
    {
        int tournament_id = 2;         // SET THIS
        int num_players = 5;
        String query;
        int [] slot_priikeys = new int[num_players];

        query = "INSERT INTO tournament (game_id, name, player_interface_class, verification_class," +
                "num_players, timeout, allow_resubmit, use_null_moves, allow_submit, game_on, twitter_config_id)" +
                "VALUES (1, 'Blokus " + num_players +" player', 'BlokusPlayer', 'BlokusPlayer', " + num_players + ", 10, 0, 1, 1, 0, 1)";
        System.out.println (query);

        for (int i = 0; i < num_players; i ++)
        {
            query = "INSERT INTO fixture_slot (tournament_id, submission_id) VALUES (" + tournament_id + ", 0)";
           // slot_priikeys[i] = DBManager.ExecuteReturnKey(query);

            System.out.println ("Executing " + query);
        }

        int round_number = 1;
        for (String g: games)
        {
            query = "INSERT INTO game (tournament_id, round_number, game_number, played, in_progress)" +
                    " VALUES (" + tournament_id + ", " + round_number + ", 1, 0, 0)";
            System.out.println ("Executing " + query);

            for (int i = 0; i < g.length(); i ++)
            {

            }
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
