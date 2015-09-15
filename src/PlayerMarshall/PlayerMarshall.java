package PlayerMarshall;

import AcademicsInterface.IVerification;
import AcademicsInterface.SubmissionMetadata;
import Common.DataModel.Game;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;
import Common.Email.EmailTypes;
import Common.Email.Emailer;
import Common.Logs.LogManager;
import Common.Logs.LogType;
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
        String full_path = SystemState.input_folder + t.PrimaryKey() + "/";

        File folder = new File (full_path);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null)
            LogManager.Log(LogType.ERROR, "PlayerMarshall.GetNewSubmissions - " + full_path + " is not a directory.");

        return listOfFiles;
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Processes a students submission.
     * Wow such commenting.
     *
     * If this submission represents a new player, it will try to add it to the tournament fixture.
     * If it is a resubmit for an existing player, it will process that as per the tournament rules.
     *
     * @param submission - the file that was found in the input folder
     * @param tournament - which tournament the file is assumed to belong to
     */
    private static void ProcessSingleSubmission (File submission, Tournament tournament)
    {
        IVerification verifier = tournament.Verification();
        File extracted_submission = verifier.ExtractSubmission(submission);
        SubmissionMetadata metadata = verifier.ExtractMetaData(submission);

        boolean can_resubmit = (tournament.GameOn()) ? tournament.AllowResubmitOn() : tournament.AllowResubmitOff();
        boolean can_submit = (!tournament.GameOn()) | tournament.AllowResubmitOn();
        boolean is_new = (PlayerSubmission.GetActiveWithTeamName(metadata.team_name, tournament) == null);


        // the zeroth barrier - no metadata, no proceeding.
        if (metadata.team_name.equals(""))
        {
            SubmissionFailure(submission, EmailTypes.NO_METADATA, "");
            return;
        }


        // the first barrier - are we accepting submissions?
        if (is_new)
        {
            if (!can_submit)
            {
                SubmissionFailure(submission, EmailTypes.NO_SUBMIT_ON, metadata.team_email);
                return;
            }
        }
        else
        {
            if (!can_resubmit)
            {
                if (tournament.GameOn())
                {
                    SubmissionFailure(submission, EmailTypes.NO_RESUBMIT_ON, metadata.team_email);
                    return;
                }
                else
                {
                    SubmissionFailure(submission, EmailTypes.NO_RESUBMIT_OFF, metadata.team_email);
                    return;
                }
            }
        }


        // the second barrier - is this submission a valid one?
        if (!verifier.VerifySubmission(extracted_submission))
        {
            SubmissionFailure(submission, EmailTypes.FAILED_VALIDATION, metadata.team_email);
            return;
        }


        int submission_slot = 0;
        // If this is a new player, make sure that there is room in the tournament for them.
        if (is_new)
        {
            try
            {
                submission_slot = tournament.GetNextAvailableSlot();
            }
            catch (Exception e)
            {
                SubmissionFailure(submission, EmailTypes.NO_SLOTS_AVAILABLE, metadata.team_email);
                return;
            }
        }


        // If this is an existing player, and we have made it this far, retire the old player.
        if (!is_new)
        {
            PlayerSubmission old_player = PlayerSubmission.GetActiveWithTeamName(metadata.team_name, tournament);
            submission_slot = old_player.FixtureSlotAllocation();

            Game.ResetAll(submission_slot);
            old_player.Retire();
        }


        // create the new submission
        PlayerSubmission new_submission = new PlayerSubmission(true);
        new_submission.SetMetaData(metadata);
        new_submission.setTournamentKey(tournament.PrimaryKey());


        // move the extracted source to the marshalling folder.
        // copy the submission over to the marshalling folder.
        try
        {
            Files.copy(extracted_submission.toPath(), Paths.get(new_submission.MarshalledSource()));
            Files.deleteIfExists(extracted_submission.toPath());
            Files.deleteIfExists(submission.toPath());
        }
        catch (Exception e)
        {
            String error = "PlayerMarshall.ProcessSingleSubmission - File IO error: " + e;
            LogManager.Log(LogType.ERROR, error);
        }


        // add the player to the tournament
        tournament.AddPlayerToFixture(submission_slot, new_submission);

        //@TODO: Avatar code goes here
        new_submission.setAvatar("");
        // last but not least, go ahead and signal that this player is good to go
        new_submission.GetReady();
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

        for (Tournament tournament: tourneys)
        {
            File [] files = GetNewSubmissions(tournament);
            for (File submission: files)
            {
                LogMessage("Processing " + submission.getName() + " for tournament " + tournament.Name());
                ProcessSingleSubmission(submission, tournament);
            }
        }

        int registered_players = PlayerSubmission.CountRegisteredPlayers(null);

        tourney_label.setText(String.valueOf(tourneys.length));
        player_label.setText(String.valueOf(registered_players));
    }


    /**
     * Nick Sifniotis
     * 9/9/2015
     *
     * Sets up the PlayerMarshall GUI.
     *
     * @param primaryStage - the default stage (the main window?)
     * @throws Exception
     */
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

        primaryStage.setTitle("Player Marshall - TournamentKey Server");
        primaryStage.setScene(new Scene(componentLayout, 400, 575));
        primaryStage.show();

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                ae -> ProcessNewSubmissions()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Handle a failed submission.
     * Shoot a dirty email to the student and delete the offending file.
     *
     * @param submission - the student's submission that failed
     * @param reason - why it failed
     * @param destination_address - who to send the dirty email to.
     */
    private static void SubmissionFailure (File submission, EmailTypes reason, String destination_address)
    {
        LogMessage("Failed to add submission. Reason: " + reason.name());

        if (destination_address == null || destination_address.equals(""))
        {
            destination_address = "u5809912@anu.edu.au";
            reason = EmailTypes.NO_VALID_EMAIL;
        }

        Emailer.SendEmail(reason, destination_address);

        try
        {
            if (!submission.delete())
            {
                String error = "PlayerMarshall.ProcessNewSubmissions - Error deleting file.";
                LogManager.Log(LogType.ERROR, error);
            }
        }
        catch (Exception e)
        {
            String error = "PlayerMarshall.ProcessNewSubmissions - Error deleting file: " + e;
            LogManager.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis
     * 9/9/2015
     *
     * One of the least interesting PSVM methods I've come across.
     *
     * @param args - unused
     */
    public static void main(String[] args)
    {
        launch(args);
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Dump a message to the status log.
     *
     * @param msg - the message to display on the gui console
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
