package GameManager;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IViewer;
import Common.DataModel.GameType;
import Common.SystemState;
import GameManager.GUI.MainPanel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nsifniotis on 8/09/15.
 *
 * GameManager - responsible for the addition, modification and deletication of
 * game types in the tournament system.
 *
 * A 'game type' is just a type of game that can be tournamented. Blokus, Mastermind and
 * Kalaha immediately jump to mind as examples ...
 *
 */
public class GameManager extends Application
{
    private GameManagerStates curr_state;
    private GameType curr_gametype;

    private MainPanel my_panel;
    private ChoiceBox game_chooser;
    private Label num_games_status;
    private File selected_JAR;
    private Stage my_stage;

    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * This is the more interesting function. It sets up the GUI
     * for the GameManager application.
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        my_panel = new MainPanel();
        num_games_status = new Label ("Num Games: 0");
        game_chooser = new ChoiceBox();

        Button btnNew = new Button ("New Game");

        Separator row0 = new Separator();
        row0.setOrientation(Orientation.VERTICAL);
        Separator row1 = new Separator();
        row1.setOrientation(Orientation.HORIZONTAL);

        HBox [] rows = new HBox [2];

        rows[0] = new HBox ();
        rows[0].getChildren().addAll(btnNew, row0, this.game_chooser);

        rows[1] = new HBox();
        rows[1].getChildren().addAll (row1);

        VBox main_layout = new VBox();
        main_layout.setPadding(new Insets(10,10,10,10));
        main_layout.getChildren().addAll(rows[0], rows[1], my_panel.initialise());

        HBox footer = new HBox ();
        footer.getChildren().add (this.num_games_status);

        BorderPane structural_layout = new BorderPane();
        structural_layout.setPadding(new Insets(10,10,10,10));
        structural_layout.setTop (rows[0]);
        structural_layout.setCenter(main_layout);
        structural_layout.setBottom(footer);

        Scene scene = new Scene (structural_layout, 300, 550);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game Manager");
        primaryStage.show();

        this.setState(GameManagerStates.UNLOADED);

        my_stage = primaryStage;

        // set the event handlers for the buttons and other objects
        btnNew.setOnAction(e -> this.newButtonClicked());
        my_panel.btnChoose.setOnAction(e -> this.selectJARButtonClicked());
        my_panel.btnTest.setOnAction(e -> this.testJARButtonClicked());
        my_panel.btnReset.setOnAction(e-> this.resetButtonClicked());
        my_panel.btnSave.setOnAction(e -> this.saveButtonClicked());
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * JavaFX PSVMs are not very interesting.
     *
     * @param args - we dont use args in this program.
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Refreshes the game drop down box with the list of game types currently
     * available in the database.
     *
     */
    private void update_chooser ()
    {
        // clear the existing entries.
        this.game_chooser.setValue(null);
        this.game_chooser.getItems().clear();

        GameType[] games = GameType.LoadAll();

        // update the status footer
        this.num_games_status.setText("Num Games: " + games.length);

        for (GameType game: games)
        {
            this.game_chooser.getItems().add (game);
        }
    }


    private void setState (GameManagerStates new_State)
    {
        this.curr_state = new_State;
        this.my_panel.updateState(new_State);

        switch (new_State)
        {
            case UNLOADED:

                this.update_chooser();
                break;
            case EDITING:

                break;
            case JAR_TESTED:

                break;
        }
    }

    public void newButtonClicked ()
    {
        this.curr_gametype = new GameType();
        this.setState(GameManagerStates.EDITING);
        this.my_panel.updateFields(this.curr_gametype, this.selected_JAR);
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Allows the user to select a JAR file to upload to the server.
     *
     */
    public void selectJARButtonClicked ()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JAR file ..");
        this.selected_JAR = fileChooser.showOpenDialog(my_stage);
        this.my_panel.btnChoose.setDisable(true);
    }


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * This is an interesting one. It tests the JAR file that the user linked to to make sure that
     * it implements IGameEngine and (if selected) IViewer
     */
    public void testJARButtonClicked ()
    {
        boolean error = false;
        String error_message = "";

        if (this.selected_JAR == null)
        {
            error = true;
            error_message = "No JAR file selected.";
        }

        String viewer_class = "";
        String engine_class = "";
        boolean viewer_state = false;

        if (!error)
        {
            viewer_state = this.my_panel.cbV.isSelected();
            engine_class = this.my_panel.tGE.getText();
            viewer_class = this.my_panel.tV.getText();

            if (engine_class.equals("") || (viewer_class.equals("") && viewer_state)) {
                error = true;
                error_message = "Class names missing.";
            }
        }

        // attempt to loaded the viewer class first.
        if (!error && viewer_state)
        {
            String fullFileName = this.selected_JAR.getName();

            try
            {
                URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
                ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
                Class source_class = playerClassLoader.loadClass(viewer_class);

                if (!IViewer.class.isAssignableFrom(source_class))
                    throw new ClassNotFoundException("The class does not correctly implement IViewer");
            }
            catch (Exception e)
            {
                // fuck e. We don't care about e.
                error = true;
                error_message = "Unable to locate class " + viewer_class + " within specified JAR.";
            }

        }

        // then try to find the game engine class
        if (!error)
        {
            String fullFileName = this.selected_JAR.getAbsolutePath();

            try
            {
                URL[] classPath = {new URL("jar:file:" + fullFileName + "!/")};
                ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
                Class source_class = playerClassLoader.loadClass(engine_class);

                if (!IGameEngine.class.isAssignableFrom(source_class))
                    throw new ClassNotFoundException("The class does not correctly implement IGameEngine");
            }
            catch (Exception e)
            {
                // fuck e. We don't care about e.
                error = true;
                error_message = "Unable to locate class " + engine_class + " within specified JAR.";
            }
        }

        if (error)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("JAR file failed the test.");
            alert.setContentText(error_message);

            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Much success");
            alert.setHeaderText("Tests passed.");
            alert.setContentText("JAR file successfully passes the tests.");

            alert.showAndWait();

            this.setState(GameManagerStates.JAR_TESTED);
        }
    }

    public void resetButtonClicked ()
    {
        curr_gametype = new GameType();
        selected_JAR = null;

        setState(GameManagerStates.EDITING);
        this.my_panel.updateFields(curr_gametype, null);
    }

    public void saveButtonClicked ()
    {
        // conduct some rudimentary tests before adding this thing to the database ..
        boolean error = false;
        String error_message = "";

        if (this.selected_JAR == null)
        {
            error = true;
            error_message = "No JAR file selected.";
        }

        if (!error && this.curr_state != GameManagerStates.JAR_TESTED)
        {
            error = true;
            error_message = "JAR file has not been validated yet.";
        }


        if (!error)
        {
            if (this.my_panel.tName.getText().equals(""))
            {
                error = true;
                error_message = "No name supplied.";
            }
            else
                this.curr_gametype.SetName(this.my_panel.tName.getText());
        }

        if (!error)
        {
            int x = Integer.parseInt(this.my_panel.tMin.getText());
            if (x <= 0)
            {
                error = true;
                error_message = "Bad value supplied in minimum players.";
            }
            else
                this.curr_gametype.SetMinPlayers(x);
        }

        if (!error)
        {
            int x = Integer.parseInt(this.my_panel.tMax.getText());
            if (x <= 0)
            {
                error = true;
                error_message = "Bad value supplied in maximum players.";
            }
            else
                this.curr_gametype.SetMaxPlayers(x);
        }

        if (!error)
        {
            if (this.curr_gametype.MinPlayers() > this.curr_gametype.MaxPlayers())
            {
                error = true;
                error_message = "Min players cannot be greater than max.";
            }
        }

        if (!error)
        {
            this.curr_gametype.SetGameEngineClass(this.my_panel.tGE.getText());
            this.curr_gametype.SetViewerClass(this.my_panel.tV.getText());
            this.curr_gametype.SetUsesViewer(this.my_panel.cbV.isSelected());
        }

        if (error)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to save.");
            alert.setContentText(error_message);

            alert.showAndWait();

            return;
        }

        // holy shit. We are saving this game!
        this.curr_gametype.SaveState();
        try
        {
            Files.deleteIfExists(Paths.get(curr_gametype.SourceFilename()));
            Files.copy(this.selected_JAR.toPath(), Paths.get(curr_gametype.SourceFilename()));
        }
        catch (Exception e)
        {
            // you know you're getting tired when you solve variable name clashes by adding an
            // extra letter to the name
            String eerror = "GameManager.saveButtonClicked - Error copying JAR file to sources: " + e;
            SystemState.Log(eerror);

            if (SystemState.DEBUG)
                System.out.println (eerror);
        }

        this.setState(GameManagerStates.UNLOADED);
    }
}
