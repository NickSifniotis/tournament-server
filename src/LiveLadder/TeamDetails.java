package LiveLadder;

import Common.DataModel.PlayerSubmission;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

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

    private Label [] my_labels;


    public TeamDetails (PlayerSubmission p)
    {
        this.team_deets = p;
        this.my_labels = new Label[LadderColumnStructure.values().length];
        for (int i = 0; i < this.my_labels.length; i ++)
            this.my_labels[i] = new Label();

        this.my_labels[LadderColumnStructure.NAME.ordinal()].setText(this.team_deets.Name());
        this.my_labels[LadderColumnStructure.SCORE_AGAINST.ordinal()].setText(String.valueOf(this.score_against));
        this.my_labels[LadderColumnStructure.SCORE_FOR.ordinal()].setText(String.valueOf(this.score_for));
    //    this.my_labels[LadderColumnStructure.PIC.ordinal()].setText(this.team_deets.Avatar().getName());
        // @TODO: Gracefully handle nulls
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


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Hopefully, this method will add the team details to the main layout.
     * And remove any old instances of the team details that might be floating around somewhere.
     * This could be fuck ugly.
     *
     * @param grid - the main layout grid
     * @param position - which row to inject values into
     */
    public void AddToGrid (GridPane grid, int position)
    {
        this.my_labels[LadderColumnStructure.POSITION.ordinal()].setText(String.valueOf(position));
        for (int i = 0; i < LadderColumnStructure.values().length; i ++)
        {
            grid.getChildren().remove (this.my_labels[i]);
            grid.add (this.my_labels[i], i, position);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Adds the given scores (for a game) to this team's tally
     *
     * @param score_for - self evident to anyone who watches sports
     * @param score_against - ditto
     */
    public void AddScores (int score_for, int score_against)
    {
        this.score_against += score_against;
        this.score_for += score_for;

        this.my_labels[LadderColumnStructure.SCORE_AGAINST.ordinal()].setText(String.valueOf(this.score_against));
        this.my_labels[LadderColumnStructure.SCORE_FOR.ordinal()].setText(String.valueOf(this.score_for));
        this.my_labels[LadderColumnStructure.PERCENTAGE.ordinal()].setText(String.valueOf(this.Percentage()));
    }
}
