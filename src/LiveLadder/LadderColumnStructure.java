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
    POSITION, PIC, NAME, POINTS, SCORE_FOR, SCORE_AGAINST, PERCENTAGE, DIFFERENTIAL;

    private static boolean [] enabled;
    private static String [] names;


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Set the default values for the column data.
     */
    public static void Initialise ()
    {
        enabled = new boolean[LadderColumnStructure.values().length];
        for (int i = 0; i < enabled.length; i ++)
            enabled[i] = true;

        names = new String [] { "#", "", "Team", "Points", "Score For", "Score Against", "Percentage", "Differential" };
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Add the column names to the GridPane.
     *
     * @param grid - the construct that the LiveLadder is being built on.
     */
    public static void SetupHeaders (GridPane grid)
    {
        for (int i = 0; i < values().length; i ++)
        {
            if (enabled[i])
            {
                Label label = new Label(names[i]);
                grid.add(label, i, 0);
            }
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Switch on and off the different columns.
     */
    public void Enable ()
    {
        enabled[this.ordinal()] = true;
    }

    public void Disable ()
    {
        enabled[this.ordinal()] = true;
    }


    /**
     * Nick Sifniotis u5809912
     * 13/09/2015
     *
     * Accessor functions for this enumeration.
     *
     * @return - various bits and bobs
     */
    public String Name() { return names[this.ordinal()]; }
    public boolean Enabled () { return enabled[this.ordinal()]; }
    public static boolean Enabled (int index) { return enabled[index]; }
}
