package TournamentServer;

import AcademicsInterface.IPlayer;
import Common.LogManager;
import Services.LogService;
import Services.Logs.LogType;


/**
 * Created by nsifniotis on 6/09/15.
 *
 * PlayerChildThread - extends the Thread class
 * and provides a wrapper for accessing the staff
 * implementation of the IPlayer interface.
 *
 */
public class PlayerChildThread extends Thread
{
    private final IPlayer player_link;
    private final Object initialBoardState;
    private boolean finished;
    private boolean move_obtained;
    private Object move;


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * The constructor of the class.
     *
     * @param boardState an object representing the current state of the game.
     * @param player_link an IPlayer implementation that connect the tournament server
     *                    to the student's submission.
     */
    public PlayerChildThread(Object boardState, IPlayer player_link)
    {
        this.player_link = player_link;
        this.initialBoardState = boardState;
        this.finished = false;
        this.move_obtained = false;
    }


    public void run()
    {
        finished = false;
        move_obtained = false;

        LogManager.Log(LogType.TOURNAMENT, "PlayerChildThread.Run - executing.");

        try
        {
            makeMove();
        }
        catch (Exception e)
        {
            String error = "PlayerChildThread.Run - Error obtaining player move: " + e;
            LogManager.Log(LogType.ERROR, error);

            finished = true;
            return;
        }

        LogManager.Log(LogType.TOURNAMENT, "PlayerChildThread.Run - move acquired.");

        finished = true;
        move_obtained = true;
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Wrapper function.
     *
     */
    private void makeMove()
    {
        this.move = this.player_link.GetMove(this.initialBoardState);
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Return the move the player made
     *
     * @return move, or null if none was made.
     */
    public Object getMove()
    {
        if (move_obtained)
            return move;

        return null;
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Accessor functions for determining state of child thread.
     *
     * @return true if processing is finished, false otherwise.
     */
    public boolean Finished()
    {
        return this.finished;
    }

    public boolean GotMove()
    {
        return this.move_obtained;
    }
}
