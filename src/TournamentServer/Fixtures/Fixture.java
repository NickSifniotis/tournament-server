package TournamentServer.Fixtures;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 10/09/15.
 *
 */
public class Fixture
{
    public List<short[]> rounds;
    private short[][] met_before;
    private static final int NUM_PLAYERS = 10;

    public Fixture()
    {
        rounds = new LinkedList<>();
        met_before = new short[NUM_PLAYERS][NUM_PLAYERS];
    }

    public boolean perfect()
    {
        short[] counts = new short[10];

        for (int i = 0; i < NUM_PLAYERS; i++)
            for (int j = 0; j < NUM_PLAYERS; j++)
                counts[met_before[i][j]]++;

        counts[0] -= NUM_PLAYERS;       // coz they dont play themselves

        int zero_count = 0;
        for (short c: counts)
            zero_count += (c == 0) ? 1 : 0;

        return (zero_count == NUM_PLAYERS - 1);
    }

    public int range ()
    {
        int min = 15;
        int max = -1;

        for (int i = 0; i < NUM_PLAYERS; i++)
            for (int j = 0; j < NUM_PLAYERS; j++)
            {
                if (i != j) {
                    if (min > met_before[i][j])
                        min = met_before[i][j];

                    if (max < met_before[i][j])
                        max = met_before[i][j];
                }
            }

        return max - min;
    }



    public String hash()
    {
        short[] counts = new short[10];

        for (int i = 0; i < NUM_PLAYERS; i++)
            for (int j = 0; j < NUM_PLAYERS; j++)
                counts[met_before[i][j]]++;

        counts[0] -= NUM_PLAYERS;       // coz they dont play themselves

        String res = "";
        for (short s : counts)
            res += s + ":";

        return res;
    }


    public Fixture AddRound(short[] round)
    {
        // need to create a deep copy of this object.

        Fixture res = new Fixture();
        for (int i = 0; i < NUM_PLAYERS; i++)
            for (int j = 0; j < NUM_PLAYERS; j++)
                res.met_before[i][j] = this.met_before[i][j];

        for (short[] r : this.rounds)
            res.rounds.add(r.clone());

        res.rounds.add(round);

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (i != j)
                    res.met_before[round[i]][round[j]]++;

        for (int i = 4; i < 8; i++)
            for (int j = 4; j < 8; j++)
                if (i != j)
                    res.met_before[round[i]][round[j]]++;

        return res;
    }
}
