package GameManager.GUI;

import Common.DataModel.GameType;
import GameManager.GameManagerStates;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


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

    public Button btnSave;
    public Button btnReset;
    public Button btnTest;
    public Button btnChoose;

    public VBox my_panel;

    public VBox initialise ()
    {
        Label lPlayers = new Label("Players:");
        Label lSrc = new Label("Source JAR:");

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

        HBox [] rows = new HBox [8];
        rows[0] = new HBox();
        rows[0].getChildren().addAll (tName);

        rows[1] = new HBox();
        rows[1].getChildren().addAll (lPlayers, tMin, tMax);

        rows[2] = new HBox();
        rows[2].getChildren().addAll (row4);

        rows[3] = new HBox();
        rows[3].getChildren().addAll (lSrc, btnChoose);

        rows[4] = new HBox();
        rows[4].getChildren().addAll (tGE);

        rows[5] = new HBox();
        rows[5].getChildren().addAll (tV);

        rows[6] = new HBox();
        rows[6].getChildren().addAll (cbV);

        rows[7] = new HBox();
        rows[7].getChildren().addAll (btnTest, btnSave, btnReset);

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
                my_panel.setDisable(true);
                break;
            case EDITING:
                my_panel.setDisable(false);
                btnSave.setDisable(true);
                break;
            case JAR_TESTED:
                btnSave.setDisable(false);
                break;
        }
    }

    public void updateFields (GameType game)
    {
        tName.setText (game.Name());
        tMin.setText ("" + game.MinPlayers());
        tMax.setText ("" + game.MaxPlayers());

    }
}
