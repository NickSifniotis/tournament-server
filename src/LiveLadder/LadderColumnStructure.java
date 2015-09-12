package LiveLadder;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Created by nsifniotis on 12/09/15.
 *
 * Enumerate the different columns that can appear on the live ladder.
 */
public enum LadderColumnStructure
{
    POSITION, PIC, NAME, POINTS, SCORE_FOR, SCORE_AGAINST, PERCENTAGE;


    public static void SetupHeaders (GridPane grid)
    {
        int position = 0;
        for (LadderColumnStructure l: LadderColumnStructure.values())
        {
            Label label = new Label(l.name());
            grid.add (label, position, 0);
            position ++;
        }

    }
}
