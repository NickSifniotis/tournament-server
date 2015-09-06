package GameManager;


import AcademicsInterface.IPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * Created by nsifniotis on 26/08/15.
 *
 * With thanks to Benjamin Roberts for the first version of this class.
 *
 */
public class PlayerManager
{
    private static final String playerClassName = "comp1140.ass2.BlokGame";
    private final Class playerClass;

    private IPlayer player;


    /**
     * Construct a new player thread using the submitted jarfile
     * @param submissionJarFile URL of JAR file containing player submission
     */
    public PlayerManager(URL submissionJarFile) throws ClassNotFoundException
    {
        final URL[] classPath = {submissionJarFile};
        ClassLoader playerClassLoader = new URLClassLoader(classPath, this.getClass().getClassLoader());

        playerClass = playerClassLoader.loadClass(playerClassName);

        try {
            playerClass.getMethod("makeMove", String.class).invoke(null, "");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new ClassNotFoundException(
                    String.format("Unable to makeMove player class for ", submissionJarFile.getFile()),
                    e);
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
        } while(System.currentTimeMillis() - turnStartTime < 10000 && !playerThread.finished);

        if(playerThread.isAlive())
            playerThread.interrupt();

        if(playerThread.moveWasLegal() && playerThread.moveWasMade())
            return playerThread.getMove();
        else
            return null;
    }

    private class PlayerMoveThread extends Thread {


    }
}