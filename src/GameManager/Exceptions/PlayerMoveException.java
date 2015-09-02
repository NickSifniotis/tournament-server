package GameManager.Exceptions;

/**
 * Created by nsifniotis on 2/09/15.
 *
 * Basic player move exceptions
 */
public abstract class PlayerMoveException extends Exception {

    public PlayerMoveException(String s)
    {
        super(s);
    }
}
