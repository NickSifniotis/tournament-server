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

import Common.Emailer;
import Common.LogManager;
import Common.SystemState;
import Common.TwitterManager;
import GameManager.GameManager;
import LiveLadder.LiveLadder;
import PlayerMarshall.PlayerMarshallManager;
import Services.GameViewer.GameViewer;
import Services.Logs.LogType;
import Services.Twitter.TwitterConfigurator;
import TournamentServer.*;
import TournamentEditor.*;
import TournamentServer.DataModelInterfaces.Tournament;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class TournamentManager extends Application
{
    private Button tournament_service_btn;
    private Button marshalling_btn;
    private Button viewer_button;
    private ChoiceBox<Tournament> tournament_list;
    private GameViewer viewer;


    public static void main(String[] args)
    {
        if (args.length > 0)
            if (args[0].equals("-i") || args[0].equals("--install"))
            {
                Setup.Initialiser.CreateFileSystem();
                Setup.Initialiser.CreateTables();

                System.exit(0);
            }

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
        SystemState.Initialise();
        LogManager.StartService();
        Common.Repository.Initialise();
        Emailer.StartService();
        TwitterManager.StartService();

        primaryStage.setOnCloseRequest(e -> shutdown_request());

        Button game_manager_btn = new Button("Open Game Manager");
        //game_manager_btn.setDisable(true);
        game_manager_btn.setOnAction(e -> launch_game_manager());
        Button twitter_btn = new Button ("Open Twitter Config");
        twitter_btn.setDisable(true);
        twitter_btn.setOnAction(e -> launch_twitter());
        Button ladder_btn = new Button ("Open Ladder");
        ladder_btn.setOnAction(e -> launch_ladder());
        Button editor_btn = new Button ("Open Tournament Editor");
        editor_btn.setDisable(true);
        editor_btn.setOnAction(e -> launch_tournament_editor());

        this.tournament_service_btn = new Button("Start Tournament Server");
        this.tournament_service_btn.setOnAction(e -> toggle_server());
        this.marshalling_btn = new Button("Start Player Marshall");
        this.marshalling_btn.setOnAction(e -> toggle_marshalling_service());
        this.viewer_button = new Button("Open Viewer");
       // this.viewer_button.setDisable(true);
        this.viewer_button.setOnAction(e -> add_viewer_handler());

        HBox top_row = new HBox();
        top_row.setSpacing(10);
        top_row.getChildren().addAll(game_manager_btn, twitter_btn, editor_btn);
        HBox second_row = new HBox();
        second_row.setSpacing(10);
        second_row.getChildren().addAll(this.marshalling_btn, this.viewer_button, ladder_btn);

        Button start_tourney = new Button ("Start");
        start_tourney.setGraphic(SystemState.Resources.server_start);
        start_tourney.setOnAction(e -> start_tournament());
        Button stop_tourney = new Button ("Stop");
        stop_tourney.setGraphic(SystemState.Resources.server_stop);
        stop_tourney.setOnAction(e -> stop_tournament());
        Button reset_tourney = new Button ("Reset");
        reset_tourney.setGraphic(SystemState.Resources.server_reset);
        reset_tourney.setOnAction(e -> reset_tournament());
        tournament_list = new ChoiceBox<>();
        update_tournament_list();
        HBox third_row = new HBox();
        third_row.setSpacing(20);
        third_row.getChildren().addAll(start_tourney, stop_tourney, reset_tourney, tournament_list);

        VBox main_layout = new VBox();
        main_layout.setSpacing(10);
        main_layout.getChildren().addAll(top_row, second_row, third_row);

        Scene scene = new Scene(main_layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tournament Server 2015");
        primaryStage.show();
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * I'm getting tired of commenting ...
     */
    public void launch_ladder()
    {
        LiveLadder new_window = new LiveLadder();
        try
        {
            new_window.start(new Stage());
        }
        catch (Exception e)
        {
            // wasting lines of code catching errors that will never happen
            String error = "Error launching the live ladder: " + e;
            LogManager.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Event handler for the twitter configuration gui
     */
    public void launch_twitter()
    {
        TwitterConfigurator new_window = new TwitterConfigurator();
        try
        {
            new_window.start(new Stage());
        }
        catch (Exception e)
        {
            // what could possibly have gone wrong?
            String error = "Error launching Twitter Config window! " + e;
            LogManager.Log(LogType.ERROR, error);
        }
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
            String error = "Error launching game manager window! " + e;
            LogManager.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 07/10/2015
     *
     * Launches the tournament editor window.
     */
    private void launch_tournament_editor()
    {
        TournamentEditor new_window = new TournamentEditor();
        try
        {
            new_window.start(new Stage());
        }
        catch (Exception e)
        {
            // fuck off
            String error = "Error launching tournament editor window! " + e;
            LogManager.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Toggles the state of the tournament server.
     *
     * If the player marshalling service is currently active, whinge and return nothing.
     */
    public void toggle_server()
    {
        // cannot start the tournament server while the PlayerMarshall service is running.
        if (PlayerMarshallManager.Alive())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable to comply");
            alert.setContentText ("Cannot start tournament server while player marshall service is running.");
            alert.showAndWait();

            return;
        }

        if (!TournamentServer.Alive())
        {
            TournamentServer.StartService();

            this.tournament_service_btn.setText("Stop Tournament Server");
            this.marshalling_btn.setDisable(true);
         //   this.viewer_button.setDisable(false);
        }
        else
        {
            TournamentServer.StopService();

            this.tournament_service_btn.setText("Start Tournament Server");
            this.marshalling_btn.setDisable(false);
          //  this.viewer_button.setDisable(true);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Event handler for the 'launch marshalling service' button.
     *
     * Blocks attempts to launch if the tournament service is running.
     */
    public void toggle_marshalling_service()
    {
        if (TournamentServer.Alive())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable to comply");
            alert.setContentText ("Cannot start player marshall while tournament service is running.");
            alert.showAndWait();

            return;
        }

        if (PlayerMarshallManager.Alive())
        {
            // it's on, so turn it off.
            PlayerMarshallManager.StopService();

            this.marshalling_btn.setText("Start Player Marshall");
            this.tournament_service_btn.setDisable(false);
        }
        else
        {
            this.tournament_service_btn.setDisable(true);
            this.marshalling_btn.setText("Stop Player Marshall");

            PlayerMarshallManager.StartService();
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * User wants to terminate the program.
     * Attempt to shut down all services gracefully.
     */
    private void shutdown_request()
    {
        if (PlayerMarshallManager.Alive())
            PlayerMarshallManager.StopService();
        if (TournamentServer.Alive())
            TournamentServer.StopService();

        Emailer.StopService();
        TwitterManager.StopService();
        LogManager.StopService();
    }


    /**
     * Nick Sifniotis u5809912
     * 08/10/2015
     *
     * These two functions are event handlers that start and stop tournaments.
     */
    private void start_tournament()
    {
        if (this.tournament_list.getSelectionModel().getSelectedItem() == null)
            return;

        Tournament new_tourney = this.tournament_list.getSelectionModel().getSelectedItem();
        new_tourney.StartTournament();
    }

    private void stop_tournament()
    {
        if (this.tournament_list.getSelectionModel().getSelectedItem() == null)
            return;

        Tournament new_tourney = this.tournament_list.getSelectionModel().getSelectedItem();
        new_tourney.StopTournament();
    }

    private void reset_tournament()
    {
        if (this.tournament_list.getSelectionModel().getSelectedItem() == null)
            return;

        Tournament new_tourney = this.tournament_list.getSelectionModel().getSelectedItem();
        new_tourney.ResetTournament();
    }


    /**
     * Nick Sifniotis u5809912
     * 13/10/2015
     *
     * Handles the 'add viewer' button click event.
     * Sends a message to the tournament server, which will handle the request.
     */
    private void add_viewer_handler()
    {
        String error_message = "";
     //   if (!TournamentServer.Alive())
       //     error_message = "Can't start the Viewer unless the tournament server is running.";

        if (this.tournament_list.getSelectionModel().getSelectedItem() == null)
            error_message = "Select the tournament to watch from the drop down list.";

        if (this.viewer != null)
            error_message = "you can only have one viewer open at a time.";

        if (!error_message.equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(error_message);
            alert.showAndWait();
            return;
        }


        Tournament tournament = this.tournament_list.getSelectionModel().getSelectedItem();
        this.viewer = new GameViewer();
        this.viewer.SetViewer(tournament.Viewer());
        this.viewer.start(new Stage());

        //int tournament_id = tournament.PrimaryKey();
        //TournamentServer.OpenWindow(this.viewer, tournament_id);
    }


    /**
     * Nick Sifniotis u5809912
     * 08/10/2015
     *
     * Updates the tournament list dropdown box.
     *
     */
    private void update_tournament_list()
    {
        this.tournament_list.getItems().clear();
        for (Tournament t: Tournament.LoadAll(false))
            this.tournament_list.getItems().add(t);
    }
}
