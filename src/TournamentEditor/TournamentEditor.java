package TournamentEditor;

/**
 * Created by nsifniotis on 7/10/15.
 *
 * The GUI that allows you to create, edit, start and stop tournaments.
 *
 */

import TournamentEditor.Data.TwitterConfig;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TournamentEditor extends Application
{
    //private Tournament current_tournament;

    //private ChoiceBox<Tournament> tournament_selector;
    private ChoiceBox<TwitterConfig> twitter_selector;
    private ChoiceBox game_type_selector;
    private CheckBox accept_sub;
    private CheckBox accept_resub;
    private CheckBox use_null_moves;
    private CheckBox twitter_feed;
    private CheckBox email_feed;
    private TextField iplayer_class;
    private TextField iverification_class;
    private TextField name;
    private TextField num_players;
    private TextField timeout;

    /**
     * Nick Sifniotis u5809912
     * 07/10/2015
     *
     * Draws the GUI.
     *
     * @param primaryStage - the place to draw it on to
     */
    @Override
    public void start(Stage primaryStage)
    {
        twitter_feed = new CheckBox("Stream to Twitter account:");
        HBox row7 = new HBox();
        twitter_selector = new ChoiceBox<>();
        row7.setSpacing(10);
        row7.getChildren().add(email_feed);

        email_feed = new CheckBox("Use emails");
        HBox row8 = new HBox();
        row8.setSpacing(10);
        row8.getChildren().add(email_feed);

        HBox bottom_row = new HBox();
        bottom_row.setSpacing (20);
        Button save_btn = new Button("Save");
        Button points_btn = new Button("Edit Point Structure");
        Button delete_btn = new Button("Delete");
        bottom_row.getChildren().addAll(save_btn, points_btn, delete_btn);

        VBox main_layout = new VBox();
        main_layout.setSpacing(20);
        main_layout.getChildren().addAll(bottom_row);

        Scene scene = new Scene(main_layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tournament Editor");

        primaryStage.show();

        update_twitter_dropdown();
    }


    /**
     * Nick Sifniotis u5809912
     * 07/10/2015
     *
     * Populates the twitterconfiguration selector with data.
     */
    private void update_twitter_dropdown()
    {
        this.twitter_selector.getItems().clear();
        for (TwitterConfig tc: Repository.GetTwitterConfigs())
            this.twitter_selector.getItems().add(tc);

        //@TODO code to update the selected config to the currently select one goes here
    }
}
