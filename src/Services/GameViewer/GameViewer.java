package Services.GameViewer;

/**
 * Created by nsifniotis on 12/10/15.
 *
 * Simple viewer GIU for the viewing of games.
 */

import AcademicsInterface.IViewer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameViewer extends Application
{
    private IViewer viewer_controller;
    private Object current_state;


    /**
     * Nick Sifniotis u5809912
     * 12/10/2015
     *
     * Create the scene, link it to the IViewer, and set up the animation.
     *
     * @param primaryStage - the stage to play on
     */
    @Override
    public void start(Stage primaryStage)
    {
        viewer_controller.InitialiseViewer(primaryStage);
        primaryStage.show();

        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                update();
            }
        }.start();
    }


    /**
     * Nick Sifniotis u5809912
     * 12/10/2015
     *
     * @param v - the IViewer implementation to attach to this window.
     */
    public void SetViewer(IViewer v)
    {
        viewer_controller = v;
    }


    public void Update(Object game_state)
    {
        current_state = game_state;
    }


    /**
     * Nick Sifniotis u5809912
     * 12/10/2015
     *
     * Animation event handler. Redraw the screen.
     */
    private void update()
    {
        viewer_controller.Update(current_state);
    }
}
