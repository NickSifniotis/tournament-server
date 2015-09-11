package LiveLadder;


/**
 * Created by nsifniotis on 11/09/15.
 *
 * Live ladder - watch your tournament live!
 *
 */

import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LiveLadder extends Application {

    private TeamDetails [] teams;
    private TournamentSelector selector_widget;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage)
    {
        BorderPane main_layout = new BorderPane();
        HBox top_row = new HBox ();
        selector_widget = new TournamentSelector(this, top_row);

        main_layout.setTop(top_row);
        main_layout.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene (main_layout, 600, 550);
        primaryStage.setScene(scene);
        primaryStage.setTitle("LIVE LADDER");
        primaryStage.show();
    }


    public void set_tournament (Tournament t)
    {
        PlayerSubmission[] players = PlayerSubmission.LoadAll(t);
        this.teams = new TeamDetails[players.length];

        for (int i = 0; i < players.length; i ++)
            this.teams[i] = new TeamDetails(players[i]);
    }
}
