package LiveLadder;

import Common.DataModel.PlayerSubmission;

/**
 * Created by nsifniotis on 11/09/15.
 *
 * Objects of class TeamDetails contain all the information pertaining to a
 * team submission with respect to the LiveLadder.
 *
 */
public class TeamDetails implements Comparable<TeamDetails>
{
    private PlayerSubmission team_deets;

    private int points;
    private int score_for;
    private int score_against;


    public TeamDetails (PlayerSubmission p)
    {
        this.team_deets = p;
    }


    /**
     * Nick Sifniotis u5809912
     * 11/09/2015
     *
     * Various accessor functions
     *
     * @return various bits of data
     *
     */
    public int Points () { return this.points; }
    public double Percentage ()
    {
        if (score_against == 0)
            return 0;

        return (double) score_for / score_against;
    }


    /**
     * Nick Sifniotis u5809912
     * 11/09/2015
     *
     * Implement Comparable so that arrays of these things can sort themselves without my help.
     *
     * @param o - the other object
     * @return - actually I'm not sure what I'm supposed to return.
     *
     */
    @Override
    public int compareTo(TeamDetails o)
    {
        if (this.points > o.points)
            return -1;

        if (this.points < o.points)
            return 1;

        return Double.compare(o.Percentage(), this.Percentage());   // craftily swapping them around to maintain reverse ordering.
    }
}
