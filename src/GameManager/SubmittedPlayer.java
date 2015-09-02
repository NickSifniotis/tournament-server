package GameManager;


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
public class SubmittedPlayer
{
    private static final String playerClassName = "comp1140.ass2.BlokGame";
    private final Class playerClass;


    /**
     * Construct a new player thread using the submitted jarfile
     * @param submissionJarFile URL of JAR file containing player submission
     */
    public SubmittedPlayer(URL submissionJarFile) throws ClassNotFoundException
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
        private final String initialBoardState;
        public boolean finished;
        private boolean moveWasMade = false,
                moveWasLegal = false;
        private String  newBoardState,
                move;

        public PlayerMoveThread(String boardState) {
            this.initialBoardState = boardState;
        }

        public void run() {
            finished = false;
            makeMove();
            finished = true;
        }

        private void makeMove()
        {
            final Method makeMove;
            try {
                makeMove = SubmittedPlayer.this.playerClass.getMethod("makeMove", String.class);
            } catch (NoSuchMethodException e) {
                throw new NoSuchMethodError("Could not instantiate player class inside player thread. Should be unreachable");
            }

            try {
                newBoardState = (String) makeMove.invoke(null, initialBoardState);
                moveWasMade = true;
            } catch (InvocationTargetException | IllegalAccessException e) {
                moveWasMade = false;
                return;
            }

            if (newBoardState.length() != 4 && newBoardState.length() != 1)
            {
                moveWasLegal = false;
                return;
            }

            moveWasLegal = true;
            move = newBoardState;
        }

        /**
         * Move the player made
         * @return move string
         */
        private String getMove() {
            if(!(moveWasLegal && moveWasMade))
                return null;
            return move;
        }

        /**
         * Player actually made a move. Doesn't mean move was legal
         * @return move was made
         */
        private boolean moveWasMade() {
            return this.moveWasMade;
        }

        /**
         * Player manipulated the board string in a legal way. This doesn't mean the placement of the piece was legal
         * @return
         */
        private boolean moveWasLegal() {
            return this.moveWasLegal;
        }

    }
}