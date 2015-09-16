package AcademicsInterface;

/**
 * Created by nsifniotis on 2/09/15.
 *
 * To run a tournament, academics need to provide a game engine that
 * implements this interface.
 *
 * Games need to be able to verify the legitimacy of moves,
 * score current (intermediate) game states, and make moves.
 *
 * Game states are passed too and from the tournament server as Objects.
 * The original Blokus design used strings to describe the game state, but
 * in order to make this as general as possible I have opted to use Objects instead.
 *
 * You are responsible for casting game states correctly!
 */
public interface IGameEngine {

    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * I am aware that Blokus simply uses a string to represent its game state.
     * In future who knows what sort of games will be run through this tournament engine.
     * To keep the future as Alive as possible I remain cognizant of the fact that
     * game states may be things other than strings. They may require initialisation.
     *
     * You get the number of players, because that might be relevant. Whatever
     * your game state is, return a blank / unplayed one.
     *
     * @param number_of_players - the number of players this game will have
     * @return - a blank / new / unplayed game state
     */
    Object InitialiseGame (int number_of_players);


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * Returns true if move represents a legitimate move based on the current state
     * of the game. Returns false otherwise.
     *
     * @param game_state the current state of the game
     * @param move the move being considered
     * @return a boolean that is true if the move is a legal move
     */
    boolean IsLegitimateMove (Object game_state, Object move);


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * Whos turn is it? We need to know. So that we can poll the correct player for their move.
     * This function will accept a game state and based on that will return an integer that lies
     * in the range 0 - (num_players - 1). That integer is whos turn it is.
     *
     * @param game_state - do I need to explain this again?
     * @return whoevers turn it is
     */
    int GetCurrentPlayer (Object game_state);


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * Returns the game scores based on the current game state, even if the game
     * is not complete. The scores are stored in an integer array the order of which
     * corresponds to the order in which the players are playing.
     *
     * @param game_state the current state of the game
     * @return the array of (possibly intermediate) player scores
     */
    int [] ScoreGame (Object game_state);


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * Takes a game state, and a move, and plays the move and advances the game state to reflect that.
     * The move is guaranteed to have passed the legitimate_move() test.
     *
     * @param game_state - the current game state
     * @param move - the move to play
     * @return - the new game state
     */
    Object MakeMove (Object game_state, Object move);


    /**
     * Nick Sifniotis u5809912
     * 2/9/2015
     *
     * This function returns true if the game state indicates that the game is still being
     * played, and false if the game is over.
     *
     * @param game_state - the current state of the game
     * @return whether or not the game is still Alive
     */
    boolean AreYouStillAlive(Object game_state);


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * This function accepts a game state and a move about to be played, and produces a String
     * entry for the game log file, to record it.
     *
     * There are no restrictions on what this log entry can look like. Talk about your mother for all I care.
     *
     * @param game_state - the current state of the game
     * @param move - the move that the player proposes to make
     * @return - an entry for the game logs.
     */
    String LogEntry (Object game_state, Object move);
}
