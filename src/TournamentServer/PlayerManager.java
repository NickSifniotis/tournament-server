package TournamentServer;


import AcademicsInterface.IPlayer;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import Common.SystemState;
import TournamentServer.Exceptions.NoMoveMadeException;
import TournamentServer.Exceptions.PlayerMoveException;
import TournamentServer.Exceptions.TimeoutException;


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
            this.my_player = tournament.PlayerInterface();
            this.my_player.InitialisePlayer(player.MarshalledSource());
        }
        catch (Exception e)
        {
            String error = "PlayerManager.constructor - error creating IPlayer object: " + e;
            LogManager.Log(LogType.ERROR, error);
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
        // If no timeout has been specified for this tournament, switch to the default value
        int player_timeout = ((tournament.Timeout() == 0) ? SystemState.DEFAULT_TIMEOUT : tournament.Timeout()) * 1000;

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
            if (System.currentTimeMillis() - turnStartTime >= player_timeout)
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
    public int PrimaryKey () { return this.data_link.PrimaryKey(); }
    public void EndingGame (boolean value) throws Exception { this.data_link.EndingGame(value); }
}