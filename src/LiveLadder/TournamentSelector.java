package LiveLadder;


import Common.SystemState;
import LiveLadder.DataModelInterfaces.Tournament;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by nsifniotis on 11/09/15.
 *
 * TournamentKey selector dropdown widget and controller.
 *
 */
public class TournamentSelector
{
    private Scene scene;
    private LiveLadder parent;
    private ChoiceBox selector;


    /**
     * Nick Sifniotis u5809912
     * 11/09/2015
     *
     * Constructor for the widget. Creates itself and adds itself to the LiveLadder form.
     *
     * @param parent - the LiveLadder that owns this widget.
     *
     */
    public TournamentSelector (LiveLadder parent)
    {
        Button select_button = new Button ("Select");
        Button cancel_button = new Button ("Cancel");
        select_button.setOnAction(e -> handleTournamentSelection());
        cancel_button.setOnAction(e -> handleCancelButton());

        this.selector = new ChoiceBox();

        HBox row = new HBox();
        row.getStyleClass().add("selector");
        row.getChildren().addAll(this.selector, select_button, cancel_button);

        this.parent = parent;
        this.refresh_list();

        this.scene = new Scene(row);
        File f = new File(SystemState.Resources.StyleSheets.LiveLadder);
        this.scene.getStylesheets().clear();
        this.scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

        Stage stage = new Stage();
        stage.setTitle("Choose tournament");
        stage.setScene(this.scene);
        stage.show();
    }


    /**
     * Nick Sifniotis u5809912
     * 14/09/2015
     *
     * Populate the choice control with the tournaments in the database.
     */
    private void refresh_list ()
    {
        Tournament [] tournaments = Tournament.LoadAll();

        this.selector.getItems().clear();
        for (Tournament t: tournaments)
            this.selector.getItems().add(t);
    }


    /**
     * Nick Sifniotis u5809912
     * 14/09/2015
     *
     * Callback function for the 'select tournament' button.
     * Set the new tournament in the LiveLadder parent, then try to
     * close this window.
     *
     */
    private void handleTournamentSelection ()
    {
        if (this.selector.getSelectionModel().getSelectedItem() == null)
            return;

        Tournament new_tourney = (Tournament) this.selector.getSelectionModel().getSelectedItem();

        this.parent.set_tournament(new_tourney);
        this.scene.getWindow().hide();
    }


    /**
     * Nick Sifniotis u5809912
     * 14/09/2015
     *
     * Handles the 'cancel' button press by closing this window.
     *
     */
    private void handleCancelButton()
    {
        this.scene.getWindow().hide();
    }
}
