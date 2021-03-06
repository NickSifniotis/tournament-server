package LiveLadder.DataModelInterfaces;

import Common.DataModel.PlayerSubmission;
import Common.LogManager;
import Services.LogService;
import Services.Logs.LogType;
import LiveLadder.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
        this.my_labels = new Label[LadderColumnStructure.values().length];
        for (int i = 0; i < this.my_labels.length; i++)
        {
            this.my_labels[i] = new Label();
            this.my_labels[i].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            this.my_labels[i].getStyleClass().add("player_row");
        }

        // how's this for gracefully handling a no name situation
        if (p.Name() != null && !p.Name().equals(""))
            this.my_labels[LadderColumnStructure.NAME.ordinal()].setText(p.Name());
        else
            this.my_labels[LadderColumnStructure.NAME.ordinal()].setText("Unnamed Team #" + p.PrimaryKey());

        if (p.Playing())
            this.my_labels[LadderColumnStructure.STATUS.ordinal()].setGraphic(new ImageView(Resources.play_image));

        if (p.Disqualified())
            this.my_labels[LadderColumnStructure.STATUS.ordinal()].setGraphic(new ImageView(Resources.disq_image));


        // add the team pictures to the ladder
        try
        {
            ImageView pic_view = new ImageView((p.UsesAvatar()) ? new Image("file:" + p.Avatar()) : Resources.nopic_image);
            pic_view.setFitHeight(50);
            pic_view.setPreserveRatio(true);
            pic_view.setSmooth(true);
            this.my_labels[LadderColumnStructure.PIC.ordinal()].setGraphic(pic_view);
        }
        catch (Exception e)
        {
            String error = "Picture error. UsesAv = " + p.UsesAvatar() + " and path is " + p.Avatar() + ": " + e;
            LogManager.Log(LogType.ERROR, error);
        }

        AddScores(0, 0);
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

    private double percentage ()
    {
        if (score_against == 0)
            return 0;

        return (double) score_for / score_against * 100;
    }

    private int differential ()
    {
        return this.score_for - this.score_against;
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

        return Integer.compare(o.score_for, this.score_for);   // craftily swapping them around to maintain reverse ordering.
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Hopefully, this method will add the team details to the main layout.
     *
     * @param grid - the main layout grid
     * @param position - which row to inject values into
     * @param style  - the hex colour this row needs to be
     */
    public void AddToGrid (GridPane grid, int position, String style)
    {
        this.my_labels[LadderColumnStructure.POSITION.ordinal()].setText(String.valueOf(position));
        for (int i = 0; i < LadderColumnStructure.values().length; i ++)
        {
            this.my_labels[i].setStyle("-fx-background-color: " + style + ";");
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
        this.my_labels[LadderColumnStructure.PERCENTAGE.ordinal()].setText(String.format("%.1f", this.percentage()));
        this.my_labels[LadderColumnStructure.DIFFERENTIAL.ordinal()].setText(String.valueOf(this.differential()));
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
