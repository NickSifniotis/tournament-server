/**
 * Created by nsifniotis on 28/09/15.
 *
 * The master controller class
 *
 * This class controls the rest, opening up the LL, GM, PM and TS on demand
 * Eradicating concurrency issues by managing messages between the different programs,
 * and providing one point of access to the database.
 * Runs the services - resources, emails, logs, databases, twitter feed etc etc
 *
 */

import GameManager.GameManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class TournamentManager extends Application
{

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Build the main controller GUI.
     *
     * Right now, it's just going to be a bunch of buttons.
     *
     * @param primaryStage - THE STAGE IS SET FOR THIS MIGHTIEST OF TOURNAMENTS
     */
    @Override
    public void start(Stage primaryStage)
    {
        Button game_manager_btn = new Button("Open Game Manager");
        game_manager_btn.setOnAction(e -> launch_game_manager());

        HBox main_row = new HBox();
        main_row.setSpacing(10);
        main_row.getChildren().add(game_manager_btn);

        Scene scene = new Scene(main_row);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tournament Server 2015");
        primaryStage.show();
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Button onClick event handlers.
     */
    private void launch_game_manager ()
    {
        GameManager new_window = new GameManager();
        try
        {
            new_window.start(new Stage());
        }
        catch (Exception e)
        {
            // fuck off
        }
    }
}
