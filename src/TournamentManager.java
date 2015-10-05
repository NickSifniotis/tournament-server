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
import PlayerMarshall.PlayerMarshall;
import Services.LogService;
import Services.Messages.TerminateMessage;
import Services.Service;
import TournamentServer.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.concurrent.BlockingQueue;


public class TournamentManager extends Application
{
    private Button tournament_service_btn;
    private TournamentThread tourney_service = null;
    private BlockingQueue <Hermes> tourney_messager = null;
    private int tournament_status = 0;
    private PlayerMarshall marshalling_service = null;
    private boolean marshalling_status = false;
    private Button marshalling_btn;
    private BlockingQueue <String> holder;
    private LogService log_service;


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
        Common.Repository.Initialise();
        this.log_service = new LogService();

        primaryStage.setOnCloseRequest(e -> shutdown_request());

        Button game_manager_btn = new Button("Open Game Manager");
        game_manager_btn.setOnAction(e -> launch_game_manager());
        tournament_service_btn = new Button("Start Tournament Server");
        tournament_service_btn.setOnAction(e -> toggle_server());

        this.marshalling_btn = new Button("Start Player Marshall");
        this.marshalling_btn.setOnAction(e -> toggle_marshalling_service());

        HBox main_row = new HBox();
        main_row.setSpacing(10);
        main_row.getChildren().add(game_manager_btn);
        main_row.getChildren().add(tournament_service_btn);
        main_row.getChildren().add(marshalling_btn);

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
        // cannot start the tournament server while the PlayerMarshall is running.
        if (marshalling_status)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable to comply");
            alert.setContentText ("Cannot start tournament server while player marshall service is running.");
            alert.showAndWait();

            return;
        }


        if (tournament_status == 0)
        {
            // there is no service. So create one.
            this.tourney_service = new TournamentThread();
            this.tourney_messager = this.tourney_service.GetHermes();
            this.tourney_service.start();

            this.tournament_service_btn.setText("Stop Tournament Server");
            tournament_status = 1;          // service is on

            // disable the marshalling button.
            this.marshalling_btn.setDisable(true);
        }
        else if (tournament_status == 1)
        {
            // switch it off
            Hermes diediedie = new Hermes();
            diediedie.message = Caduceus.END;
            this.tourney_messager.add(diediedie);

            // wait for the child to shut down.
            this.tournament_service_btn.setText("Shutting down ..");
            this.tournament_service_btn.setDisable(true);
            while (!this.tourney_service.Finished())
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (Exception e)
                {
                    // nah, still sleeping.
                }
            }

            this.tourney_service = null;
            this.tourney_messager = null;
            this.tournament_service_btn.setText("Start Tournament Server");
            this.tournament_service_btn.setDisable(false);
            tournament_status = 0;

            // re-enable the marshalling button
            this.marshalling_btn.setDisable(false);
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
        if (this.tournament_status == 1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable to comply");
            alert.setContentText ("Cannot start player marshall while tournament service is running.");
            alert.showAndWait();

            return;
        }

        if (this.marshalling_status)
        {
            // it's on, so turn it off.
            this.marshalling_service.MessageQueue().add(new TerminateMessage());
            while (this.marshalling_service.Alive())
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (Exception e)
                {
                    // nah, still sleeping.
                }
            }
            this.marshalling_service = null;
            this.marshalling_status = false;

            this.marshalling_btn.setText ("Start Player Marshall");
            this.tournament_service_btn.setDisable(false);
        }
        else
        {
            this.tournament_service_btn.setDisable(true);
            this.marshalling_btn.setText("Stop Player Marshall");

            this.marshalling_service = new PlayerMarshall(this.log_service.MessageQueue());
            this.marshalling_service.start();
            this.marshalling_status = true;
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
        if (this.marshalling_status)
            this.shutdown_service(this.marshalling_service);

        this.shutdown_service(this.log_service);
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Gracefully shut down this service.
     *
     * @param service - the thing to stop
     */
    private void shutdown_service (Service service)
    {
        service.MessageQueue().add(new TerminateMessage());
        while (service.Alive())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                // nah, still sleeping.
            }
        }
    }
}
