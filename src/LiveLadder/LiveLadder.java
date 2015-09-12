package LiveLadder;


/**
 * Created by nsifniotis on 11/09/15.
 *
 * Live ladder - watch your tournament live!
 *
 */

import Common.DataModel.Game;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.PointStructure;
import Common.DataModel.Tournament;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;

public class LiveLadder extends Application {

    private TeamDetails [] teams;
    private HashMap<Integer, TeamDetails> teams_indexed;
    private TournamentSelector selector_widget;
    private Tournament tournament;
    private BorderPane main_layout;


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage)
    {
        main_layout = new BorderPane();
        HBox top_row = new HBox ();
        selector_widget = new TournamentSelector(this, top_row);

        main_layout.setBottom(top_row);             // @TODO: This would work better as a solo button that opens popup
        main_layout.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene (main_layout, 600, 550);
        primaryStage.setScene(scene);
        primaryStage.setTitle("LIVE LADDER");
        primaryStage.show();


        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(500),
                ae -> this.refresh_main_grid()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * This crafty bit of code creates a completely new GridPane object, populates it
     * and only when the data transfer is complete, chucks out the old one and
     * inserts the new one in its stead.
     *
     */
    private void refresh_main_grid ()
    {
        GridPane new_grid = new GridPane();
        LadderColumnStructure.SetupHeaders(new_grid);

        this.refresh_scores(new_grid);
        main_layout.setCenter(new_grid);
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Shows the details of this new tournament.
     *
     * @param t - which tournament to display
     */
    public void set_tournament (Tournament t)
    {
        this.tournament = t;
        PlayerSubmission[] players = PlayerSubmission.LoadAll(t);
        this.teams = new TeamDetails[players.length];
        this.teams_indexed = new HashMap<>();

        for (int i = 0; i < players.length; i ++)
        {
            this.teams[i] = new TeamDetails(players[i]);
            this.teams_indexed.putIfAbsent(players[i].PrimaryKey(), teams[i]);
        }

        this.refresh_main_grid();
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Populate the team data structure with information about scores and things.
     *
     * @param grid - the GridPane object to dump the data into
     */
    private void refresh_scores(GridPane grid)
    {
        if (this.tournament == null)
            return;


        // players start from zero
        for (TeamDetails t: teams)
                t.Reset();


        // get the games for this tournament.
        Tournament [] tournaments = { this.tournament };
        Game[] games = Game.LoadAll(tournaments, false);
        PointStructure points = this.tournament.PointStructure();


        // find the ones with interesting scores - games in progress or have been played.
        for (Game g: games)
            if (g.Started())
                points.ScoreGame(g, teams_indexed);


        // that's it!
        Arrays.sort(teams);
        for (int i = 0; i < this.teams.length; i ++)
            this.teams[i].AddToGrid(grid, i + 1);
    }

}
