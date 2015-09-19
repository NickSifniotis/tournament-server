package LiveLadder;

import javafx.scene.image.Image;

/**
 * Created by nsifniotis on 18/09/15.
 *
 * Possibly a class for holding static resources such as images.
 *
 */
public class Resources
{
    public static Image play_image;
    public static Image disq_image;


    public static void initialise()
    {
        play_image = new Image("file:images/play");
        disq_image = new Image("file:images/stop.gif");
    }
}
