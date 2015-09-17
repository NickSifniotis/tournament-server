package PlayerMarshall;

import AcademicsInterface.IVerification;
import AcademicsInterface.SubmissionMetadata;
import Common.Email.EmailTypes;
import Common.Email.Emailer;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import PlayerMarshall.DataModelInterfaces.Game;
import PlayerMarshall.DataModelInterfaces.PlayerSubmission;
import PlayerMarshall.DataModelInterfaces.Tournament;
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
public class PlayerMarshall extends Application
{
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
        File folder = new File (t.InputFolder());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null)
            LogManager.Log(LogType.ERROR, "PlayerMarshall.GetNewSubmissions - " + t.InputFolder() + " is not a directory.");

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

        PlayerSubmission old_player = PlayerSubmission.GetActiveWithTeamName(metadata.team_name, tournament.PrimaryKey());


        // the zeroth barrier - no metadata, no proceeding.
        if (metadata.team_name.equals(""))
        {
            SubmissionFailure(submission, EmailTypes.NO_METADATA, "", tournament);
            return;
        }


        // the first barrier - are we accepting submissions?
        int submission_slot = 0;

        if (old_player == null)         // this is a new team submission
        {
            if (!tournament.AllowSubmit())
            {
                SubmissionFailure(submission, EmailTypes.NO_SUBMIT_ON, metadata.team_email, tournament);
                return;
            }

            try
            {
                submission_slot = tournament.AvailableSlot();
            }
            catch (Exception e)
            {
                SubmissionFailure(submission, EmailTypes.NO_SLOTS_AVAILABLE, metadata.team_email, tournament);
                return;
            }
        }
        else
        {
            if (!tournament.AllowResubmit())
            {
                if (tournament.Running())
                {
                    SubmissionFailure(submission, EmailTypes.NO_RESUBMIT_ON, metadata.team_email, tournament);
                    return;
                }
                else
                {
                    SubmissionFailure(submission, EmailTypes.NO_RESUBMIT_OFF, metadata.team_email, tournament);
                    return;
                }
            }
        }


        // the second barrier - is this submission a valid one?
        if (!verifier.VerifySubmission(extracted_submission))
        {
            SubmissionFailure(submission, EmailTypes.FAILED_VALIDATION, metadata.team_email, tournament);
            return;
        }


        // create the new submission
        PlayerSubmission new_submission = new PlayerSubmission();
        new_submission.SetData(metadata, tournament.PrimaryKey());


        // move the extracted source to the marshalling folder.
        // copy the submission over to the marshalling folder.
        try
        {
            LogManager.Log (LogType.TOURNAMENT, "Marshalling submission for player " + new_submission.PrimaryKey());

            Files.copy(extracted_submission.toPath(), Paths.get(new_submission.MarshalledSource()));
            Files.deleteIfExists(extracted_submission.toPath());
            Files.deleteIfExists(submission.toPath());
        }
        catch (Exception e)
        {
            String error = "PlayerMarshall.ProcessSingleSubmission - File IO error: " + e;
            LogManager.Log(LogType.ERROR, error);
        }

        // If this is an existing player, and we have made it this far, retire the old player.
        if (old_player != null)
        {
            LogManager.Log (LogType.TOURNAMENT, "Attempting to retire player " + old_player.PrimaryKey());
            old_player.Retire();
            submission_slot = old_player.FixtureSlotAllocation();
            Game.SupercedeGames(submission_slot);
        }

        // add the player to the tournament
        tournament.AssignSlotToPlayer(submission_slot, new_submission.PrimaryKey());

        //@TODO: Avatar code goes here
        //new_submission.setAvatar("");
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

        int registered_players = PlayerSubmission.CountRegisteredPlayers(0);

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

        primaryStage.setTitle("Player Marshall");
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
    private static void SubmissionFailure (File submission, EmailTypes reason, String destination_address, Tournament t)
    {
        LogMessage("Failed to add submission. Reason: " + reason.name());

        if (destination_address == null || destination_address.equals(""))
        {
            destination_address = "u5809912@anu.edu.au";
            reason = EmailTypes.NO_VALID_EMAIL;
        }

        if (reason.AttachSubmission())
            Emailer.SendEmail(reason, destination_address, t.PrimaryKey(), submission.getAbsolutePath());
        else
            Emailer.SendEmail(reason, destination_address, t.PrimaryKey());

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

        LogManager.Log(LogType.TOURNAMENT, msg);
    }
}
