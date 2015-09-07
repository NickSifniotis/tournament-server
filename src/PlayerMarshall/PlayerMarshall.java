package PlayerMarshall;

import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;
import Common.SystemState;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created by nsifniotis on 31/08/15.
 *
 * Main PlayerMarshall class
 * Functionality and GUI - not great design, Nick
 */
public class PlayerMarshall extends Application {

    private static String last_log_message;
    private static Label log_label;
    private static Label tourney_label;
    private static Label player_label;



    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Checks the player submissions input folder for the given tournament.
     * If any new players have been uploaded, return the submissions as an array of files
     *
     * @param t - the tournament who's players we seek
     * @return - the much-sought-after players
     */
    public static File [] GetNewSubmissions (Tournament t)
    {
        String full_path = SystemState.input_folder + t.SubmissionsPath() + "/";
        SystemState.Log("PlayerMarshall.GetNewSubmissions - checking directory " + full_path + " for tourney " + t.Name());

        File folder = new File (full_path);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null)
            SystemState.Log("PlayerMarshall.GetNewSubmissions - returning " + listOfFiles.length + " files found.");
        else
            SystemState.Log("PlayerMarshall.GetNewSubmissions - " + full_path + " is not a directory.");

        return listOfFiles;
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Searches the input folders for new submissions, verifies
     * and processes them.
     *
     * Called every few seconds by the main program loop.
     *
     */
    public static void ProcessNewSubmissions ()
    {
        LogMessage("Waiting for new submissions ..");

        Tournament[] tourneys = Tournament.LoadAll();

        for (Tournament t: tourneys)
        {
            File [] files = GetNewSubmissions(t);
            for (File f: files)
            {
                LogMessage ("Processing " + f.getName() + " for tournament " + t.Name());
                if (t.VerifySubmission(f))
                {
                    // this submission is good, so lets move it to marshalling and get ready to rumble
                    // has this player been submitted before? We will know because submissions are identified
                    // by filenames.

                    String original = f.getName();
                    PlayerSubmission oldie = PlayerSubmission.GetActiveWithOriginalFilename(original, t);

                    if (oldie != null)
                    {
                        // @TODO: More retirement code here. The games and the logs!
                        oldie.Retire();
                    }

                    PlayerSubmission new_submission = new PlayerSubmission(true);
                    new_submission.setName("Default Name");
                    new_submission.setEmail("Default Email");
                    new_submission.setTournament(1);


                    // copy the submission over to the marshalling folder.
                    String destination = SystemState.marshalling_folder + new_submission.PrimaryKey() + ".sub";

                    try
                    {
                        Files.copy(f.toPath(), Paths.get(destination));
                        f.delete();
                    }
                    catch (Exception e)
                    {
                        String error = "PlayerMarshall.ProcessNewSubmissions - Error copying player file to marshalling: " + e;
                        SystemState.Log(error);

                        if (SystemState.DEBUG)
                            System.out.println (error);
                    }

                    // last but not least, go ahead and signal that this player is good to go
                    new_submission.Ready();

                    // @TODO: Add the player to the games fixture
                }
                else
                {
                    // failed the verification test. Fuck. Now I have to send a dirty email
                    // @TODO: Figure out how to do emails

                    // erase the offending submission
                    try
                    {
                        SystemState.Log("PlayerMarshall.ProcessNewSubmissions - file " + f.getName() + " failed verification. Attempting to delete it.");

                        f.delete();

                        SystemState.Log("PlayerMarshall.ProcessNewSubmissions - delete successful.");
                    }
                    catch (Exception e)
                    {
                        String error = "PlayerMarshall.ProcessNewSubmissions - Error deleting file: " + e;
                        SystemState.Log(error);

                        if (SystemState.DEBUG)
                            System.out.println (error);
                    }
                }
            }
        }

        tourney_label.setText(String.valueOf(tourneys.length));
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        BorderPane componentLayout = new BorderPane();

        final HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(12, 15, 12, 15));
        statusBar.setSpacing(10);

        Label tourneyLbl = new Label("Tournaments:");
        statusBar.getChildren().add(tourneyLbl);
        tourney_label = new Label ("0");
        statusBar.getChildren().add(tourney_label);
        Label playersLbl = new Label ("Registered Players:");
        statusBar.getChildren().add(playersLbl);
        player_label = new Label ("0");
        statusBar.getChildren().add (player_label);

        FlowPane centrePane = new FlowPane();
        log_label = new Label("Player Marshall 1.0\nSystem initialised.");
        log_label.setWrapText(true);

        centrePane.getChildren().add (log_label);

        componentLayout.setBottom(statusBar);
        componentLayout.setTop(centrePane);

        primaryStage.setTitle("Player Marshall - Tournament Server");
        primaryStage.setScene(new Scene(componentLayout, 400, 575));
        primaryStage.show();

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                ae -> ProcessNewSubmissions()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }


    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Dump a message to the status log.
     *
     * @param msg
     */
    public static void LogMessage (String msg)
    {
        if (msg.equals (last_log_message))
            return;

        String text = log_label.getText() + "\n" + msg;
        log_label.setText(text);

        last_log_message = msg;
    }
}
