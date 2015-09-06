package GameManager;


import AcademicsInterface.IPlayer;
import Common.DataModel.PlayerSubmission;
import Common.DataModel.Tournament;
import Common.SystemState;


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

        // create the IPlayer player interface for the player that this manager manages.
        try
        {
            this.my_player = (IPlayer) tournament.PlayerInterfaceClass().newInstance();
            this.my_player.initialise(player, tourney);
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
     * Waits 10s for player to make move, will return null if move results in illegal game string or wasn't made.
     * Move is not guarenteed to be legal when compared against Blokus rules
     * @param boardState current board state string
     * @return newest move (ie "AAAA")
     */
    public String nextMove(String boardState)
    {
        PlayerMoveThread playerThread = new PlayerMoveThread(boardState);
        playerThread.start();
        long turnStartTime = System.currentTimeMillis();
        do {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) { /* Ignore as we will try sleep again */}
        } while (System.currentTimeMillis() - turnStartTime < 10000 && !playerThread.finished);

        if (playerThread.isAlive())
            playerThread.interrupt();

        if (playerThread.moveWasLegal() && playerThread.moveWasMade())
            return playerThread.getMove();
        else
            return null;
    }
}