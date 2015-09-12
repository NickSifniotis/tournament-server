package LiveLadder;


import Common.DataModel.Tournament;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Created by nsifniotis on 11/09/15.
 *
 * TournamentKey selector dropdown widget and controller.
 *
 */
public class TournamentSelector
{
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
    public TournamentSelector (LiveLadder parent, Pane layout_thing)
    {
        Button select_button = new Button ("Select ..");
        select_button.setOnAction(e -> handleTournamentSelection());

        Button refresh_list = new Button ("Refresh List");
        refresh_list.setOnAction(e -> refresh_list());

        this.selector = new ChoiceBox();

        HBox row = new HBox();
        row.setSpacing(10);
        row.getChildren().addAll(select_button, this.selector, refresh_list);

        this.parent = parent;

        layout_thing.getChildren().add(row);

        this.refresh_list();
    }


    private void refresh_list ()
    {
        Tournament [] tournaments = Tournament.LoadAll();

        if (tournaments != null)
        {
            this.selector.getItems().clear();

            for (Tournament t: tournaments)
                this.selector.getItems().add(t);
        }
    }

    private void handleTournamentSelection ()
    {
        if (this.selector.getSelectionModel().getSelectedItem() == null)
            return;

        Tournament new_tourney = (Tournament) this.selector.getSelectionModel().getSelectedItem();

        this.parent.set_tournament(new_tourney);
    }
}
