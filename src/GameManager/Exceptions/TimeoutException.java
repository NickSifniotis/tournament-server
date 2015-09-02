package GameManager.Exceptions;

/**
 * Created by nsifniotis on 2/09/15.
 */
public class TimeoutException extends PlayerMoveException {

    public TimeoutException()
    {
        super("Computer player timed out.");
    }
}
