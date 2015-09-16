package GameManager.GUI;

import GameManager.DataModelInterfaces.GameType;
import GameManager.GameManagerStates;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;


/**
 * Created by nsifniotis on 8/09/15.
 *
 * Controller class for the main data entry panel on the user interface.
 *
 */
public class MainPanel
{
    public CheckBox cbV;
    public TextField tName;
    public TextField tMin;
    public TextField tMax;
    public TextField tGE;
    public TextField tV;
    public Label lJARstate;

    public Button btnSave;
    public Button btnReset;
    public Button btnTest;
    public Button btnChoose;

    public VBox my_panel;

    public VBox initialise ()
    {
        Label lPlayers = new Label("Players:");
        Label lSrc = new Label("Source JAR:");
        lJARstate = new Label("");

        cbV = new CheckBox();
        cbV.setText("Uses IViewer:");

        tName = new TextField();
        tName.setPromptText("Game Name: ");
        tMin = new TextField();
        tMin.setPromptText("Min");
        tMin.setMaxWidth(50);
        tMax = new TextField();
        tMax.setPromptText("Max");
        tMax.setMaxWidth(50);
        tGE = new TextField();
        tGE.setPromptText("IGameEngine class:");
        tV = new TextField();
        tV.setPromptText("IViewer class:");

        btnTest = new Button("Test JAR");
        btnSave = new Button("Save");
        btnReset = new Button("Reset");
        btnChoose = new Button("Select JAR ..");

        Separator row4 = new Separator();
        row4.setOrientation(Orientation.HORIZONTAL);

        HBox [] rows = new HBox [9];
        rows[0] = new HBox();
        rows[0].getChildren().addAll (tName);

        rows[1] = new HBox();
        rows[1].getChildren().addAll (lPlayers, tMin, tMax);

        rows[2] = new HBox();
        rows[2].getChildren().addAll (row4);

        rows[3] = new HBox();
        rows[3].getChildren().addAll (lSrc, btnChoose);

        rows[4] = new HBox();
        rows[4].getChildren().addAll (lJARstate);

        rows[5] = new HBox();
        rows[5].getChildren().addAll (tGE);

        rows[6] = new HBox();
        rows[6].getChildren().addAll (tV);

        rows[7] = new HBox();
        rows[7].getChildren().addAll (cbV);

        rows[8] = new HBox();
        rows[8].getChildren().addAll (btnTest, btnSave, btnReset);

        my_panel = new VBox();
        my_panel.setSpacing(5);
        for (HBox row: rows)
            my_panel.getChildren().add(row);

        return my_panel;
    }

    public void updateState (GameManagerStates state)
    {
        switch (state)
        {
            case UNLOADED:
                btnChoose.setDisable(false);
                btnTest.setDisable(false);
                tGE.setDisable(false);
                tV.setDisable(false);
                my_panel.setDisable(true);
                break;
            case EDITING:
                btnChoose.setDisable(false);
                my_panel.setDisable(false);
                btnTest.setDisable(false);
                tGE.setDisable(false);
                tV.setDisable(false);
                btnSave.setDisable(true);
                break;
            case JAR_TESTED:
                btnChoose.setDisable(true);
                btnTest.setDisable(true);
                btnSave.setDisable(false);

                // lock these mofos in particular
                // once successfully tested, eleminate the possibililty that the user will change them!
                tGE.setDisable(true);
                tV.setDisable(true);
                break;
        }
    }

    public void updateFields (GameType game, File curr_jar)
    {
        tName.setText (game.Name());
        tMin.setText ("" + game.MinPlayers());
        tMax.setText ("" + game.MaxPlayers());
        tGE.setText (game.GameEngineClass());
        tV.setText (game.ViewerClass());

        if (curr_jar != null)
            lJARstate.setText ("JAR file loaded - click the button to change it.");
        else
            lJARstate.setText ("No JAR file attached to this game.");
    }
}
