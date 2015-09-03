package GameManager;

import Common.DataModel.PlayerSubmission;
import Common.SystemState;
import GameManager.Exceptions.PlayerMoveException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by nsifniotis on 3/09/15.
 *
 * Interface between the player manager and student Blokus player submissions.
 * 
 */

public class BlokusPlayer implements IPlayer {

    private static final String playerClassName = "comp1140.ass2.BlokGame";
    private static final String methodName = "makeMove";
    private Class playerClass;


    @Override
    public boolean initialise(PlayerSubmission player) {
        String fullFileName = SystemState.marshalling_folder + player.PrimaryKey() + ".sub";

        try
        {
            URL [] classPath = {new URL("jar:file:" + fullFileName + "!/")};
            ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());
            playerClass = playerClassLoader.loadClass(playerClassName);
        }
        catch (Exception e)
        {
            String error = "BlokusPlayer.initialise - error creating classpath " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return false;
        }

        try
        {
            playerClass.getMethod(methodName, String.class).invoke(null, "");
        }
        catch (Exception e)
        {
            String error = "BlokusPlayer.initialise - error accessing makeMove method " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return false;
        }

        return true;
    }

    @Override
    public Object get_move(Object game_state) throws PlayerMoveException
    {
        String initialBoardState = "";
        try
        {
            initialBoardState = (String) game_state;
        }
        catch (Exception e)
        {
            // this should never happen
            String error = "BlokusPlayer.get_move - error casting game state object into string. This should never happen! " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return null;
        }

        final Method makeMove;
        try
        {
            makeMove = playerClass.getMethod(methodName, String.class);
        }
        catch (NoSuchMethodException e)
        {
            String error = "BlokusPlayer.get_move - Could not instantiate player class inside player thread. Should be unreachable";
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return null;
        }

        String move = "";
        try
        {
            move = (String) makeMove.invoke(null, initialBoardState);
        }
        catch (Exception e)
        {
            String error = "BlokusPlayer.get_move - Unknown error calling makeMove method. " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);

            return null;
        }

        return move;
    }
}
