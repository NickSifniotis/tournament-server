package PlayerMarshall;

import AcademicsInterface.IVerification;
import AcademicsInterface.SubmissionMetadata;
import Common.Email.EmailTypes;
import Common.Emailer;
import Common.LogManager;
import Services.Logs.LogType;
import Common.SystemState;
import PlayerMarshall.DataModelInterfaces.PlayerSubmission;
import PlayerMarshall.DataModelInterfaces.Tournament;
import Services.Messages.Message;
import javafx.scene.image.Image;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * Created by nsifniotis on 31/08/15.
 *
 * Refactored as a service on the 5th October 2015
 */

public class PlayerMarshallService extends Services.Service
{
    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Eventually, this function will handle a whole variety of different messages relating to
     * player submissions.
     *
     * Right now, it handles nothing at all since the only message worth waiting for (terminate)
     * is handled by the base class.
     * @param message - the message that they have to handle.
     */
    @Override
    public void handle_message (Message message)
    {

    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Check for new submissions, process them and such.
     *
     */
    @Override
    public void do_service()
    {
        this.ProcessNewSubmissions();
    }


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
    public File [] GetNewSubmissions (Tournament t)
    {
        File folder = new File (t.InputFolder());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null)
            LogManager.Log(LogType.ERROR, "PlayerMarshallService.GetNewSubmissions - " + t.InputFolder() + " is not a directory.");

        return listOfFiles;
    }


    /**
     * Nick Sifniotis u5809912
     * Prior to the 5th October 2015
     *
     * Downloads and saves the player's picture, if they have asked for one in the metadata.
     *
     * @param player - the player for whom to set the picture
     * @param metadata - the player's metadata that contains the location of the picture to download.
     */
    private void SetAvatar (PlayerSubmission player, SubmissionMetadata metadata)
    {
        Path destination = Paths.get(SystemState.pictures_folder + player.PrimaryKey() +".pic");

        try
        {
            URL website = new URL(metadata.team_picture);
            Files.copy(website.openStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e)
        {
            String error = "PlayerMarshallService - load avatar - File / Internet IO error loading file " + metadata.team_picture + ": " + e;
            LogManager.Log(LogType.ERROR, error);
            return;
        }

        // try loading the image file, to make sure that it is an image and not some shitty thing.
        try
        {
            Image test = new Image("file:" + destination);
        }
        catch (Exception e)
        {
            String error = "PlayerMarshallService - load avatar - some sort of error creating the image. " + e;
            LogManager.Log(LogType.ERROR, error);

            try
            {
                Files.deleteIfExists(destination);
            }
            catch (Exception e2)
            {
                error = "PLayerMarshall - and I can't even delete the damned thing. File: " + destination + ": " + e2;
                LogManager.Log(LogType.ERROR, error);
            }

            return;
        }

        // if we've made it this far, the picture has been downloaded, saved and tested correctly.
        player.SetUsesAvatar();
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
     * Note that this function assumes it will never be called whilst the tournament is running.
     *
     * @param submission - the file that was found in the input folder
     * @param tournament - which tournament the file is assumed to belong to
     */
    private void ProcessSingleSubmission (File submission, Tournament tournament)
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
                SubmissionFailure(submission, EmailTypes.NO_SUBMIT, metadata.team_email, tournament);
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
                SubmissionFailure(submission, EmailTypes.NO_RESUBMIT, metadata.team_email, tournament);
                return;
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


        // attempt to load up the team's picture
        if (!metadata.team_picture.equals(""))
        {
            SetAvatar(new_submission, metadata);
        }

        // move the extracted source to the marshalling folder.
        // copy the submission over to the marshalling folder.
        try
        {
            LogManager.Log(LogType.TOURNAMENT, "Marshalling submission for player " + new_submission.PrimaryKey());

            Files.copy(extracted_submission.toPath(), Paths.get(new_submission.MarshalledSource()));
            Files.deleteIfExists(extracted_submission.toPath());
            Files.deleteIfExists(submission.toPath());
        }
        catch (Exception e)
        {
            String error = "PlayerMarshallService.ProcessSingleSubmission - File IO error: " + e;
            LogManager.Log(LogType.ERROR, error);
        }

        // If this is an existing player, and we have made it this far, retire the old player.
        if (old_player != null)
        {
            LogManager.Log(LogType.TOURNAMENT, "Attempting to retire player " + old_player.PrimaryKey());
            old_player.Retire();
            submission_slot = old_player.FixtureSlotAllocation();
        }

        // add the player to the tournament
        tournament.AssignSlotToPlayer(submission_slot, new_submission.PrimaryKey());
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
    public void ProcessNewSubmissions ()
    {
        Tournament[] tourneys = Tournament.LoadAll();

        for (Tournament tournament: tourneys)
        {
            File [] files = GetNewSubmissions(tournament);
            for (File submission: files)
            {
                LogManager.Log(LogType.TOURNAMENT, "Processing " + submission.getName() + " for tournament " + tournament.Name());
                ProcessSingleSubmission(submission, tournament);
            }
        }
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
    private void SubmissionFailure (File submission, EmailTypes reason, String destination_address, Tournament t)
    {
        LogManager.Log(LogType.TOURNAMENT, "Failed to add submission. Reason: " + reason.name());

        if (reason.AttachSubmission())
            Emailer.SendEmail(reason, destination_address, t.PrimaryKey(), submission.getAbsolutePath());
        else
            Emailer.SendEmail(reason, destination_address, t.PrimaryKey());

        try
        {
            if (!submission.delete())
            {
                String error = "PlayerMarshallService.ProcessNewSubmissions - Error deleting file.";
                LogManager.Log(LogType.ERROR, error);
            }
        }
        catch (Exception e)
        {
            String error = "PlayerMarshallService.ProcessNewSubmissions - Error deleting file: " + e;
            LogManager.Log(LogType.ERROR, error);
        }
    }
}
