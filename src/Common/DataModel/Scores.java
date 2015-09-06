package Common.DataModel;

import GameManager.Exceptions.PlayerMoveException;
import GameManager.PlayerManager;

/**
 * Created by nsifniotis on 6/09/15.
 *
 * This class holds information relating to a game's current scores.
 *
 */
public class Scores
{
    private int game_id;
    private PlayerSubmission[] players;
    private int [] raw_scores;
    private int [] score_prikeys;
    private boolean [] disqualified;
    private boolean game_on;


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Constructor for the score object.
     *
     * @param game_id @TODO wtf passing indexes around like candy??
     * @param players
     */
    public Scores (int game_id, PlayerManager[] players)
    {
        this.game_id = game_id;

        int num_players = players.length;
        this.players = new PlayerSubmission[num_players];
        this.raw_scores = new int [num_players];
        this.disqualified = new boolean [num_players];

        for (int i = 0; i < num_players; i ++)
            this.players[i] = players[i].GetDatalink();

        this.game_on = true;

        // @TODO create and execute SQL to INSERT new score data into the table
        // save the prikeys in the private int array.
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Updates the score data based on the output of the game engine's scoring method.
     *
     * @param new_scores - the new scores, fed directly from the game engine.
     */
    public void Update (int [] new_scores)
    {
        for (int i = 0; i < this.raw_scores.length; i ++)
            this.raw_scores[i] = new_scores[i];
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Disqualifies a player no questions asked.
     *
     * @param player_id
     */
    public void Disqualify (int player_id)
    {
        this.disqualified[player_id] = true;
        this.game_on = false;
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Getter function for game_on - a boolean that indicates whether the game has ended
     * due to disqualification or timeout.
     *
     * @return
     */
    public boolean GameOn ()
    {
        return this.game_on;
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * This method saves the current score state into the database.
     * @TODO: Make it do that
     *
     */
    private void save_state ()
    {

    }
}
