package LiveLadder.DataModelInterfaces;

import Common.DataModel.PlayerSubmission;
import LiveLadder.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;


/**
 * Created by nsifniotis on 11/09/15.
 *
 * Objects of class TeamDetails contain all the information pertaining to a
 * team submission with respect to the LiveLadder.
 *
 * Implementation of the PlayerSubmission contract with respect to LiveLadder
 *
 */
public class TeamDetails implements Comparable<TeamDetails>
{
    private int id;
    private int points;
    private int score_for;
    private int score_against;
    private boolean playing_now;
    private boolean retired;
    private boolean disqualified;

    private Label [] my_labels;


    /**
     * Nick Sifniotis u5809912
     * 14/09/2015
     *
     * Constructor for the team details object
     *
     * @param p - the PlayerSubmission for whom we are collecting team details.
     */
    public TeamDetails (PlayerSubmission p)
    {
        this.id = p.PrimaryKey();
        this.retired = p.Retired();
        this.playing_now = p.Playing();
        this.my_labels = new Label[LadderColumnStructure.values().length];
        for (int i = 0; i < this.my_labels.length; i++)
        {
            this.my_labels[i] = new Label();
            this.my_labels[i].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        // how's this for gracefully handling a no name situation
        if (p.Name() != null && !p.Name().equals(""))
            this.my_labels[LadderColumnStructure.NAME.ordinal()].setText(p.Name());
        else
            this.my_labels[LadderColumnStructure.NAME.ordinal()].setText("Unnamed Team #" + p.PrimaryKey());

        if (this.playing_now)
            this.my_labels[LadderColumnStructure.STATUS.ordinal()].setGraphic(new ImageView(Resources.play_image));

        if (p.Disqualified())
            this.my_labels[LadderColumnStructure.STATUS.ordinal()].setGraphic(new ImageView(Resources.disq_image));


        if (p.Avatar() != null)
        {
            // @TODO: Display the picture
        }
        else
        {
            //@TODO: Display no picture
            //this.my_labels[LadderColumnStructure.PIC.ordinal()]
        }
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
    public int PrimaryKey() { return this.id; }
    public int Points () { return this.points; }
    public double Percentage ()
    {
        if (score_against == 0)
            return 0;

        return (double) score_for / score_against * 100;
    }
    public int Differential () {return this.score_for - this.score_against; }
    public boolean Retired() { return this.retired; }


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

        return Integer.compare(o.Differential(), this.Differential());   // craftily swapping them around to maintain reverse ordering.
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Hopefully, this method will add the team details to the main layout.
     *
     * @param grid - the main layout grid
     * @param position - which row to inject values into
     */
    public void AddToGrid (GridPane grid, int position)
    {
        this.my_labels[LadderColumnStructure.POSITION.ordinal()].setText(String.valueOf(position));
        for (int i = 0; i < LadderColumnStructure.values().length; i ++)
        {
            this.my_labels[i].getStyleClass().clear();
            if (position % 2 == 0)
                this.my_labels[i].getStyleClass().add ("player_row2");
            else
                this.my_labels[i].getStyleClass().add ("player_row1");

            if (LadderColumnStructure.Enabled(i))
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
        this.my_labels[LadderColumnStructure.PERCENTAGE.ordinal()].setText(String.format("%.1f", this.Percentage()));
        this.my_labels[LadderColumnStructure.DIFFERENTIAL.ordinal()].setText(String.valueOf(this.Differential()));

        if (this.playing_now)
            this.my_labels[LadderColumnStructure.STATUS.ordinal()].setText("Playing");
        else
            this.my_labels[LadderColumnStructure.STATUS.ordinal()].setText("");
    }


    /**
     * Nick Sifniotis u5809912
     * 17/09/2015
     *
     * Adds the given points divident to this player's running total.
     *
     * @param points - the number of points to add.
     */
    public void AddPoints (int points)
    {
        this.points += points;

        this.my_labels[LadderColumnStructure.POINTS.ordinal()].setText(String.valueOf(this.points));
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Returns all current players for the tournament.
     *
     * @param tournament_id - the tournament that is being loaded
     * @return teamdetails objects for every live player.
     */
    public static TeamDetails[] LoadAll (int tournament_id)
    {
        Common.DataModel.PlayerSubmission[] players = Common.DataModel.PlayerSubmission.LoadAll(tournament_id, true);
        TeamDetails [] res = new TeamDetails[players.length];

        for (int i = 0; i < players.length; i++)
            res [i] = new TeamDetails(players[i]);

        return res;
    }
}
