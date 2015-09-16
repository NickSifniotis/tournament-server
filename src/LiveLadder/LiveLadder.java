package LiveLadder;


/**
 * Created by nsifniotis on 11/09/15.
 *
 * Live ladder - watch your tournament live!
 *
 */

import Common.DataModel.Game;
import Common.DataModel.PointStructure;
import LiveLadder.DataModelInterfaces.TeamDetails;
import LiveLadder.DataModelInterfaces.Tournament;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class LiveLadder extends Application
{
    private Tournament tournament;
    private BorderPane main_layout;
    private Label tournament_name;


    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Nick Sifniotis u5809912
     * 14/09/2015
     *
     * Creates the GUI for the LiveLadder.
     *
     * @param primaryStage - the place where the liveladder will be drawn.
     */
    @Override
    public void start(Stage primaryStage)
    {
        LadderColumnStructure.Initialise();

        main_layout = new BorderPane();
        HBox bottom_row = new HBox ();
        Button select_button = new Button ("Select Tournament");
        select_button.setOnAction(e -> handleSelectButton());
        bottom_row.getChildren().add(select_button);

        HBox top_row = new HBox();
        top_row.getStyleClass().add("tournament_header_row");
        this.tournament_name = new Label ("No tournament selected");
        this.tournament_name.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.tournament_name.getStyleClass().add("tournament_header_text");
        top_row.getChildren().add(tournament_name);

        main_layout.setTop(tournament_name);
        main_layout.setBottom(bottom_row);

        Scene scene = new Scene (main_layout, 800, 600);

        File f = new File("src/LiveLadder/liveladder.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

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
     * 14/09/2015
     *
     * Handles the 'select tournament' button press by opening up a new window.
     * Hopefully.
     *
     */
    private void handleSelectButton()
    {
        TournamentSelector window = new TournamentSelector(this, this.tournament);
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
        new_grid.getStyleClass().add("grid");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        new_grid.getColumnConstraints().add(columnConstraints);
        columnConstraints = new ColumnConstraints();
        new_grid.getColumnConstraints().add(columnConstraints);

        columnConstraints = new ColumnConstraints();
        columnConstraints.setFillWidth(true);
        columnConstraints.setHgrow(Priority.ALWAYS);
        new_grid.getColumnConstraints().add(columnConstraints);

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
        this.tournament_name.setText(this.tournament.Name());
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

        TeamDetails[] teams = TeamDetails.LoadAll(this.tournament.PrimaryKey());
        HashMap<Integer, TeamDetails> teams_indexed = new HashMap<>();

        for (TeamDetails team: teams)
            teams_indexed.putIfAbsent(team.PrimaryKey(), team);


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
        for (int i = 0; i < teams.length; i ++)
            teams[i].AddToGrid(grid, i + 1);
    }

}
