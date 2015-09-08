package AcademicsInterface;


/**
 * Created by nsifniotis on 8/09/15.
 *
 * This interface allows the instructor to create a GUI display for the games as they are being executed.
 *
 * The tournament manager will pick and choose which games are displayed. All the IViewer has to do
 * is accept the player data and game state, and the window that is acting as the GUI, and make
 * it look pretty.
 *
 * !
 *
 */
public interface IViewer
{
    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Accepts and stores a javafx stage. This stage is where the game will be displayed as it is played out.
     *
     * @param window
     */
    void InitialiseViewer (javafx.stage.Stage window);


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Sets up a new game on the GUI. By 'new' I simply mean 'not the game that has been displayed so far'
     * It may well be halfway through a game. The important thing is that the list of players has changed.
     *
     * Generally you would use this method to change the player information display panel - assuming you
     * implement something like that - and then call update with the game state.
     *
     * @param game_state - the current state of the new game to display
     * @param players - the players playing this new game.
     */
    void NewGame (Object game_state, ViewedPlayers [] players);


    /**
     * Nick Sifniotis u5809912
     * 8/9/2015
     *
     * Update the game display to show the new game state.
     *
     * @param game_state - the current state of the game.
     */
    void Update (Object game_state);
}
