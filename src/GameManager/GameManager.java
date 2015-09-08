package GameManager;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        // lets create some labels and other fancy things
        Label lPlayers = new Label ("Players:");
        Label lsrc = new Label ("Source JAR:");

        CheckBox cbV = new CheckBox();
        cbV.setText("Uses IViewer:");

        TextField tName = new TextField();
        tName.setPromptText("Game Name: ");
        TextField tMin = new TextField();
        tMin.setPromptText("Min");
        TextField tMax = new TextField();
        tMax.setPromptText("Max");
        TextField tGE = new TextField();
        tGE.setPromptText("IGameEngine class:");
        TextField tV = new TextField();
        tV.setPromptText("IViewer class:");

        Button btnNew = new Button ("New Game");
        Button btnTest = new Button ("Test JAR");
        Button btnSave = new Button ("Save");
        Button btnReset = new Button ("Reset");

        ChoiceBox chExisting = new ChoiceBox();

        FileChooser fileJAR = new FileChooser();
        fileJAR.setTitle("Select JAR ..");
        //fileChooser.showOpenDialog(stage); for reference only
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

}
