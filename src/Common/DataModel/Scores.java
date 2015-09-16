package Common.DataModel;

import Common.DBManager;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import Common.SystemState;
import TournamentServer.PlayerManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;


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
     * @param game - the Game object for which these scores are being recorded.
     * @param players - the players of this game.
     */
    public Scores (Game game, PlayerManager[] players)
    {
        this.scores = new Score[players.length];
        this.game_on = true;

        // remove any old scores that may be attached to this game.
        String query = "DELETE FROM score WHERE game_id = " + game.PrimaryKey();
        DBManager.Execute(query);

        for (int i = 0; i < players.length; i ++)
            this.scores[i] = new Score(game.PrimaryKey(), players[i].PrimaryKey());
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Attempt to load the scores for a game (in progress or complete)
     * If no records are found in the database for this game, have a whinge.
     *
     * This constructor is highly specialised (far too specialised for my taste)
     * and should only ever be called when it is known that there are scores
     * in the database for game_id.
     *
     * [I spent two hours searching for a bug that doesn't exist because I forgot that]
     *
     * @param game_id - the game to search for
     */
    public Scores (int game_id) throws Exception
    {
        // I am not even going to assert that game_id != 0 here
        // don't get me started on num_players
        List<Score> list_of_holding = new LinkedList<>();

        String query = "SELECT * FROM score WHERE game_id = " + game_id;
        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);

        if (res != null)
        {
            try
            {
                while (res.next())
                    list_of_holding.add(new Score(res));

                DBManager.disconnect(res);          // disconnect by result
            }
            catch (Exception e)
            {
                String error = "Score constructor (game_id) - SQL error retrieving score data. " + e;
                LogManager.Log(LogType.ERROR, error);

                DBManager.disconnect(connection);

                throw new Exception ("Scores for game " + game_id + " not found. Incorrect scores constructor called at this location.");
            }
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
            throw new Exception ("Scores for game " + game_id + " not found. Incorrect scores constructor called at this location.");
        }

        this.scores = new Score[list_of_holding.size()];
        list_of_holding.toArray(this.scores);
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
        LogManager.Log(LogType.TOURNAMENT, "Disqualifying player " + player_id);

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
            for (int i = 0; i < this.scores.length; i ++)
            {
                if (this.scores[i].Disqualified())
                    res += "Player " + (i + 1) + ": DISQUALIFIED\n";
                else
                    res += "Player " + (i + 1) + ": No score recorded\n";
            }
        }
        else
        {
            res += "Game Status: Legal\n";
            for (int i = 0; i < this.scores.length; i ++)
                    res += "Player " + (i + 1) + ": " + this.scores[i].Score() + "\n";
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Accessor functions for the LiveLadder class
     * Throws an exception if the player that is being searched for doesn't exist.
     *
     * @param player_id - the player whos scores we are after
     * @return - the requested scores
     * @throws Exception - if the provided player did not play in this game.
     */
    public int ScoreFor (int player_id) throws Exception
    {
        boolean found = false;
        int score = 0;

        try
        {
            for (Score s: this.scores)
                if (s.SubmissionKey() == player_id)
                {
                    found = true;
                    score += (s.NoScore()) ? 0 : s.Score();
                }
        }
        catch (Exception e)
        {
            String error = "Scores.ScoreAgainst - error in code: " + e + e.getStackTrace().toString();
            LogManager.Log(LogType.ERROR, error);
        }


        if (!found)
            throw new Exception ("Player " + player_id + " did not play in this game.");

        return score;
    }

    public int ScoreAgainst (int player_id) throws Exception
    {
        boolean found = false;
        int score = 0;

        try
        {
            for (Score s : this.scores)
                if (s.SubmissionKey() == player_id)
                    found = true;
                else
                    score += (s.NoScore()) ? 0 : s.Score();
        }
        catch (Exception e)
        {
            String error = "Scores.ScoreAgainst - error in code: " + e + e.getStackTrace().toString();
            LogManager.Log(LogType.ERROR, error);
        }

        if (!found)
            throw new Exception ("Player " + player_id + " did not play in this game.");

        return score;
    }
}
