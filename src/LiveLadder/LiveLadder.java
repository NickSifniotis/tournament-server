package LiveLadder;

import Common.SystemState;
import LiveLadder.DataModelInterfaces.Game;
import LiveLadder.DataModelInterfaces.TeamDetails;
import LiveLadder.DataModelInterfaces.Tournament;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by nsifniotis on 11/09/15.
 *
 * Live ladder - watch your tournament live!
 *
 */
public class LiveLadder extends Application
{
    private int tournament_id = 0;
    private Label tournament_name;
    private ScrollPane scroller;


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
        LadderColumnStructure.DIFFERENTIAL.Disable();

        BorderPane main_layout = new BorderPane();
        main_layout.getStyleClass().add("main_view");

        HBox top_row = new HBox();
        top_row.getStyleClass().add("tournament_header_row");
        this.tournament_name = new Label ("No tournament selected");
        this.tournament_name.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.tournament_name.getStyleClass().add("tournament_header_text");
        this.tournament_name.setOnMouseClicked(e -> handleSelectButton());
        top_row.getChildren().add(tournament_name);

        scroller = new ScrollPane();
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("main_view");
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        main_layout.setTop(tournament_name);
        main_layout.setCenter(scroller);

        Scene scene = new Scene (main_layout, 800, 600);

        File f = new File(SystemState.Resources.StyleSheets.LiveLadder);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

        primaryStage.setScene(scene);
        primaryStage.setTitle("LIVE LADDER");
        primaryStage.show();


        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
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
        TournamentSelector window = new TournamentSelector(this);
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
        scroller.setContent(new_grid);
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
        this.tournament_id = t.PrimaryKey();
        this.tournament_name.setText(t.Name());
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
        if (tournament_id < 1)
                return;

        Tournament tournament = new Tournament(tournament_id);

        TeamDetails[] teams = TeamDetails.LoadAll(tournament.PrimaryKey());
        HashMap<Integer, TeamDetails> teams_indexed = new HashMap<>();

        for (TeamDetails team: teams)
            teams_indexed.putIfAbsent(team.PrimaryKey(), team);


        // get the games for this tournament.
        Game[] games = Game.LoadAll(tournament.PrimaryKey());

        for (Game g: games)
            tournament.ScoreGame(g, teams_indexed);


        // that's it!
        Arrays.sort(teams);
        int position = 1;
        String style;
        for (TeamDetails team: teams)
        {
            if (!tournament.IsOn())
                style = "#42474E";
            else
                if (position % 2 == 0)
                    style = "#5D636A";
                else
                    style = "#7C8188";

            team.AddToGrid(grid, position++, style);
        }
    }

}
