package LiveLadder;


/**
 * Created by nsifniotis on 11/09/15.
 *
 * Live ladder - watch your tournament live!
 *
 */

import Common.DataModel.Game;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.HashMap;

public class LiveLadder extends Application {

    private TeamDetails [] teams;
    private HashMap<Integer, TeamDetails> teams_indexed;
    private TournamentSelector selector_widget;
    private Tournament tournament;
    private GridPane main_grid;


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage)
    {
        BorderPane main_layout = new BorderPane();
        HBox top_row = new HBox ();
        selector_widget = new TournamentSelector(this, top_row);

        main_grid = new GridPane();
        LadderColumnStructure.SetupHeaders(main_grid);

        main_layout.setBottom(top_row);             // @TODO: This would work better as a solo button that opens popup
        main_layout.setCenter(main_grid);
        main_layout.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene (main_layout, 600, 550);
        primaryStage.setScene(scene);
        primaryStage.setTitle("LIVE LADDER");
        primaryStage.show();
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

        Arrays.sort(teams);

        for (int i = 0; i < players.length; i ++)
            this.teams[i].AddToGrid(this.main_grid, i + 1);
    }


    private void refresh_scores()
    {
        // get the games for this tournament.
        Tournament [] tournaments = { this.tournament };
        Game[] games = Game.LoadAll(tournaments, false);

        // find the ones with interesting scores - games in progress or have been played.
        for (Game g: games)
        {
            if (g.Started())
            {

            }
        }
    }

}
