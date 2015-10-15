package TournamentServer.DataModelInterfaces;

import Common.DataModel.Score;
import Services.LogService;
import Services.Logs.LogType;
import TournamentServer.PlayerManager;


/**
 * Created by nsifniotis on 6/09/15.
 *
 * This class holds information relating to a game's current scores.
 *
 * Database updates occur whenever the scores themselves are updated, so that there is always
 * a sort of 'live stream' of scores into the database happening.
 *
 * It also functions as the contract to the Score data entity.
 */
public class Scores
{
    private boolean game_on;
    private Score[] scores;


    /**
     * Nick Sifniotis u5809912
     * 6/9/2015
     *
     * Constructor for the score object.
     * As well as creating the score object, this constructor also
     * creates the score records for this game in the database.
     *
     * It also removes from the database any score records that may already exist
     * for this game. That sort of thing happens when you have multiple submissions
     * during tournament play, or shitty testing routines that keep creating
     * new score records over and over for the same game.
     *
     * @param game_id - the Game object for which these scores are being recorded.
     * @param players - the players of this game.
     */
    public Scores (int game_id, PlayerManager[] players)
    {
        this.scores = new Score[players.length];
        this.game_on = true;

        for (int i = 0; i < players.length; i ++)
            this.scores[i] = new Score(game_id, players[i].PrimaryKey());
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
        for (int i = 0; i < new_scores.length; i ++)
            this.scores[i].Update(new_scores[i]);
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
        for (int i = 0; i < this.scores.length; i ++)
            this.scores[i].AbnormalTermination(i == player_id);

        this.game_on = false;
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
     * 10/9/2015
     *
     * Various accessor functions
     * @return - requested data
     */
    public boolean Disqualified (int player_position) { return this.scores[player_position].Disqualified(); }

    public int Score (int index) { return this.scores[index].Score(); }

}
