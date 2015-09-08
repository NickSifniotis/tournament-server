package TournamentServer.Exceptions;

/**
 * Created by nsifniotis on 6/09/15.
 */
public class NoMoveMadeException extends PlayerMoveException {

    public NoMoveMadeException()
    {
        super("Computer player returned no move.");
    }
}
