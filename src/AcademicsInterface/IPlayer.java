package AcademicsInterface;

/**
 * Created by nsifniotis on 2/09/15.
 *
 * An interface for the player manager code.
 *
 * This layer needs to be provided by the academic / instructor.
 * It is the final connection between the tournament server and the student's codes.
 *
 * The tourney server works with Objects of undeclared type. Students probably won't be expected
 * to deal with Objects. They will have Strings or maybe more complex GameState objects. Or maybe they
 * will not even be working in Java, it might be Haskell or assembler or brainfuck. Who am I to judge
 * them based on their choice of programming language.
 *
 * The point is that the tourney simulator does use Java and so there needs to be some sort of plugin
 * interfacey type thing that connects the player submissions to the tourney system.
 *
 */
public interface IPlayer {

    /**
     * Nick Sifniotis u5809912
     * 3/9/2015
     *
     * Create whatever is necessary to link the player submission to the tournament.
     *
     * @param path_name  - the path/file name of the code, the classname, and the method within the class
     * @return  true if the player has loaded successfully, false if there was a problem connecting
     * to the submission
     */
    boolean InitialisePlayer (String path_name);


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * You are given the current state of the game. Make your next move son.
     * Note that you (the academic / tutor) are responsible for properly casting
     * these objects into whatever data structures your assignment specs
     * have indicated need to be used
     *
     * @param game_state - the current state of the game
     * @return the move that the player wishes to make
     */
    Object GetMove (Object game_state);


    /**
     * Nick Sifniotis u5809912
     * 18/09/2015
     *
     * The student submission failed to return a valid move.
     * But we don't want to abort the game and ruin things for everyone else.
     * So instead, we skip the players turn. This method returns the
     * 'null / skip move' turn for this game.
     *
     * The use of this method is strictly optional and is a tournament rule that
     * can be configured in the tournament manager. In games like Kalaha where there
     * is no skip turn move, just return a null.
     *
     * @return an object that represents a 'no move' move.
     */
    Object NullMove ();
}
