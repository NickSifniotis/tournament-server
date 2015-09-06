package GameManager;


import AcademicsInterface.IPlayer;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;
import Common.SystemState;
import GameManager.Exceptions.NoMoveMadeException;
import GameManager.Exceptions.PlayerMoveException;
import GameManager.Exceptions.TimeoutException;


/**
 * Created by nsifniotis on 26/08/15.
 * Modified quite heavily on the 6th September 2015
 *
 * With thanks to Benjamin Roberts for the first version of this class.
 *
 */
public class PlayerManager
{
    private Tournament tournament;
    private IPlayer my_player;
    private PlayerSubmission data_link;


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Constructor for this player manager.
     *
     * @param tourney - the tournament that we are playing in
     * @param player - the player that I am responsible for
     */
    public PlayerManager(Tournament tourney, PlayerSubmission player)
    {
        this.tournament = tourney;
        this.data_link = player;

        // create the IPlayer player interface for the player that this manager manages.
        try
        {
            this.my_player = (IPlayer) tournament.PlayerInterfaceClass().newInstance();
            this.my_player.InitialisePlayer(player, tourney);
        }
        catch (Exception e)
        {
            String error = "PlayerManager.constructor - error creating IPlayer object: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Spawns a child thread and executes a submitted player's getmove function within it.
     * If the thread times out, kill it and return nothing.
     *
     * @param boardState - the current state of the game.
     * @return an object that represents the computer player's move.
     */
    public Object nextMove(Object boardState) throws PlayerMoveException
    {
        //@TODO implement the tournament rule 'playertimeout'
        //@TODO also implement 'if zero timeout then keep going' condition
        int player_timeout = 10 * 1000;        // measured in milliseconds

        PlayerChildThread playerThread = new PlayerChildThread(boardState, this.my_player);
        playerThread.start();
        long turnStartTime = System.currentTimeMillis();
        do
        {
            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            { /* Ignore as we will try sleep again */}
        }
        while (System.currentTimeMillis() - turnStartTime < player_timeout && !playerThread.Finished());

        if (playerThread.isAlive())
        {
            playerThread.interrupt();
            throw new TimeoutException();
        }

        if (!playerThread.GotMove())
            throw new NoMoveMadeException();

        return playerThread.getMove();
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Returns the PlayerSubmission database record object.
     * It's held so that the end of game scores can be attached to this player.
     *
     * @return the record.
     */
    public PlayerSubmission GetDatalink()
    {
        return this.data_link;
    }
}