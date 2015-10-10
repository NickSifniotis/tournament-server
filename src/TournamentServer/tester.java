package TournamentServer;

import Common.DBManager;
import Common.LogManager;
import Common.TwitterManager;
import Services.Twitter.Data.TwitterConfig;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by nsifniotis on 7/09/15.
 *
 * Just a testing class. Go away warnings.
 *
 */
public class tester {

    private static final short NUM_PLAYERS = 10;


    /**
     * Sorts the game array so that the lowest players come first. This
     * makes comparing two rounds easy since two identical rounds will
     * sort the same way.
     * @param original the unsorted round
     */
    public static short [] sort_round(short[] original)
    {
        short [] res = new short[8];

        short [] game1 = new short[4];
        short [] game2 = new short[4];

        System.arraycopy(original, 0, game1, 0, 4);
        System.arraycopy(original, 4, game2, 0, 4);

        game1 = sort_array(game1);
        game2 = sort_array(game2);

        if (game1[0] < game2[0])
        {
            System.arraycopy(game1, 0, res, 0, 4);
            System.arraycopy(game2, 0, res, 4, 4);
        }
        else
        {
            System.arraycopy(game2, 0, res, 0, 4);
            System.arraycopy(game1, 0, res, 4, 4);
        }

        return res;
    }


    public static short [] sort_array (short [] array)
    {
        for (int i = 0; i < array.length; i ++)
            for (int j = 0; j < array.length - 1; j ++)
                if (array[j] > array[j + 1])
                {
                    short temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }

        return array;
    }


    public static String hash_round (short [] original)
    {
        String res = "";

        for (short s: original)
            res += s;

        return res;
    }

    public static void main(String[] args)
    {
        // this is an interesting one. Experimenting with fixturing.

        // the hypothetical is a ten player tournament with four players per game
        // this means that there will be two games and two byes per round.

        // the endgame is to create an algorithm that will generate fixtures for tournaments
        // with an arbitary number of competitors, with an arbitary number of players per game

        // one round is two 4player games. There is no need to represent the teams in the bye
        // in this data structure. Therefore, an 8 element ushort array should do the job.
/*
        short [] round;
        short [] players = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int num_games = 0;

        HashMap <String, short []> unique_games = new HashMap<>();

        boolean finished = false;
        while (!finished)
        {
            // Find the largest index k such that a[k] < a[k + 1].
            // If no such index exists, the permutation is the last permutation.

            int k = -1;
            for (int i = 0; i < players.length - 1; i ++)
                if (players[i] < players[i + 1])
                    k = i;

            if (k == -1)
                finished = true;

            // the rest of the algorithm goes here.
            if (!finished)
            {
                // Find the largest index l greater than k such that a[k] < a[l].
                int l = -1;
                for (int i = k; i < players.length; i ++)
                    if (players[k] < players[i])
                        l = i;

                //Swap the value of a[k] with that of a[l].
                short temp = players[k];
                players[k] = players[l];
                players[l] = temp;

                // Reverse the sequence from a[k + 1] up to and including the final element a[n].
                short [] newarray = players.clone();

                for (int i = k + 1; i < players.length; i ++)
                    newarray[players.length - i + k] = players[i];

                players = newarray;


                round = new short[8];
                System.arraycopy(players, 0, round, 0, round.length);
                round = sort_round(round);

                if (unique_games.putIfAbsent(hash_round(round), round) == null)
                {
                    System.out.println(hash_round(round));
                    num_games++;
                }
            }
        }


        System.out.println ("Finished! Total unique combinations: " + num_games);

        Fixture start = new Fixture();

        HashMap<String, Fixture> fixtures = new HashMap<>();
        HashMap<String, Fixture> new_fixtures;
        Fixture temp;
        int count;
        int best_range;

        temp = start.AddRound(unique_games.get("01234567"));
        fixtures.putIfAbsent(temp.hash(), temp);


        for (int zzz = 0; zzz < 20; zzz ++)
        {
            count = 0;
            best_range = 50; // impossible
            new_fixtures = new HashMap<>();

            for (short[] r : unique_games.values())
                for (Fixture f : fixtures.values())
                {
                    temp = f.AddRound(r);

                    if (temp.perfect())
                    {
                        System.out.println ("Perfect fixture found!!");
                        System.out.println (temp.hash());
                        return;

                    }

                    if (temp.range() <= best_range)
                    {
                        // filters out a lot of fat before it's even inserted into the hashmap
                        if (new_fixtures.putIfAbsent(temp.hash(), temp) == null)
                        {
                           // System.out.println("Adding hash " + temp.hash() + " range " + temp.range());
                            count++;
                            best_range = temp.range();
                        }
                    }
                }

            fixtures = new_fixtures;

            System.out.println("Total adds: " + count);
            count = 0;

            // filter out fixtures that have blown out their ranges too far.
            new_fixtures = new HashMap<>();
            for (Fixture f: fixtures.values())
                if (f.range() == best_range)
                    new_fixtures.putIfAbsent(f.hash(), f);
                else
                    count ++;

            System.out.println ("Total fails: " + count);
            fixtures = new_fixtures;
        }


        System.out.println("Results!!\n");

        for (Fixture f: fixtures.values())
        {
            System.out.print(f.hash() + ": ");
            for (short [] s: f.rounds)
                System.out.print (Arrays.toString(s) + ", ");

            System.out.println();
        }*/

        LogManager.StartService();

        Common.Repository.Initialise();

        String query = "SELECT * FROM submission";
        Connection con = DBManager.connect();
        try
        {
            ResultSet res = DBManager.ExecuteQuery(query, con);
            while (res.next())
            {
                System.out.println (res.getInt("id") + ": " + res.getString("team_name") + ":" + res.getString("playing"));
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            DBManager.disconnect(con);
        }

        LogManager.StopService();
    }

}
