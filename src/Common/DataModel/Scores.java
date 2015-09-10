package Common.DataModel;

import Common.DBManager;
import TournamentServer.PlayerManager;


/**
 * Created by nsifniotis on 6/09/15.
 *
 * This class holds information relating to a game's current scores.
 *
 * Database updates occur whenever the scores themselves are updated, so that there is always
 * a sort of 'live stream' of scores into the database happening.
 *
 */
public class Scores
{
    private int [] raw_scores;
    private int [] score_prikeys;
    private boolean [] disqualified;
    private boolean game_on;


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Constructor for the score object.
     * As well as creating the score object, this constructor also
     * creates the score records for this game in the database.
     *
     * @param game - the Game object for which these scores are being recorded.
     * @param players - the players of this game.
     */
    public Scores (Game game, PlayerManager[] players)
    {
        int num_players = players.length;
        this.raw_scores = new int [num_players];
        this.disqualified = new boolean [num_players];
        this.game_on = true;

        // Create and execute SQL to INSERT new score data into the table
        // save the prikeys in the private int array.
        this.score_prikeys = new int [num_players];
        for (int i = 0; i < num_players; i ++)
        {
            String query = "INSERT INTO score (submission_id, game_id, score, no_score, disqualified)"
                    + " VALUES (" + players[i].GetDatalink().PrimaryKey() + ", " + game.PrimaryKey() + ", 0, false, false)";
            this.score_prikeys[i] = DBManager.ExecuteReturnKey(query);
        }
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
        System.arraycopy(new_scores, 0, this.raw_scores, 0, new_scores.length);
        this.SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Disqualifies a player no questions asked.
     *
     * @param player_id - the player to disqualify
     */
    public void Disqualify (int player_id)
    {
        this.disqualified[player_id] = true;
        this.game_on = false;
        this.SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Getter function for game_on - a boolean that indicates whether the game has ended
     * due to disqualification or timeout.
     *
     * @return whether, from the scoring point of view, the game is still current or has ended.
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
     * It assumes that the constructor has done its job and created the records
     *
     */
    public void SaveState()
    {
        boolean no_score = !this.game_on;

        for (int i = 0; i < this.raw_scores.length; i ++)
        {
            String query = "UPDATE score SET"
                    + " score = " + this.raw_scores[i]
                    + ", no_score = " + no_score
                    + ", disqualified = " + this.disqualified[i]
                    + " WHERE id = " + this.score_prikeys[i];
            DBManager.Execute(query);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 7/9/2015
     *
     * Simple toString function to allow for game and player testing
     * on the console.
     *
     * @return a string containing the current game state.
     */
    @Override
    public String toString ()
    {
        String res = "";

        if (!this.game_on)
        {
            res += "Game Status: Disqualification\n";
            for (int i = 0; i < this.raw_scores.length; i ++)
            {
                if (this.disqualified[i])
                    res += "Player " + (i + 1) + ": DISQUALIFIED\n";
                else
                    res += "Player " + (i + 1) + ": No score recorded\n";
            }
        }
        else
        {
            res += "Game Status: Legal\n";
            for (int i = 0; i < this.raw_scores.length; i ++)
                    res += "Player " + (i + 1) + ": " + this.raw_scores[i] + "\n";
        }

        return res;
    }
}
