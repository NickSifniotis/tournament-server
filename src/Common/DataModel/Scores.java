package Common.DataModel;

import Common.DBManager;
import Common.SystemState;
import TournamentServer.PlayerManager;

import java.sql.Connection;
import java.sql.ResultSet;


/**
 * Created by nsifniotis on 6/09/15.
 *
 * This class holds information relating to a game's current scores.
 *
 * Database updates occur whenever the scores themselves are updated, so that there is always
 * a sort of 'live stream' of scores into the database happening.
 *
 * Database schema for Score objects
 * id                   prikey
 * submission_id        int
 * game_id              int
 * score                int
 * no_score             boolean         Used to indicate that a game terminated abnormally
 * disqualified         boolean         Abnormal termination has been caused by this player.
 *
 * // @TODO: This entire model needs to be refactored. Right now the code is asymmetrical and there are
 * // @TODO: two completely separate strands of logic at work
 */
public class Scores
{
    // @TODO: Refactor this code so that there is just one array of Score DMobjects that look after themselves.
    private int [] raw_scores;
    private int [] score_prikeys;
    private boolean [] disqualified;
    private int [] submission_keys;
    private boolean[] no_scores;
    private boolean game_on;


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
        int num_players = players.length;
        this.raw_scores = new int [num_players];
        this.disqualified = new boolean [num_players];
        this.no_scores = new boolean[num_players];
        this.submission_keys = new int[num_players];
        this.game_on = true;


        // remove any old scores that may be attached to this game.
        String query = "DELETE FROM score WHERE game_id = " + game.PrimaryKey();
        DBManager.Execute(query);


        // Create and execute SQL to INSERT new score data into the table
        // save the prikeys in the private int array.
        this.score_prikeys = new int [num_players];
        for (int i = 0; i < num_players; i ++)
        {
            query = "INSERT INTO score (submission_id, game_id, score, no_score, disqualified)"
                    + " VALUES (" + players[i].GetDatalink().PrimaryKey() + ", " + game.PrimaryKey() + ", 0, false, false)";
            this.score_prikeys[i] = DBManager.ExecuteReturnKey(query);
        }
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
     * @param game_id - the game to search for
     */
    public Scores (int game_id, int num_players) throws Exception
    {
        // I am not even going to assert that game_id != 0 here
        // don't get me started on num_players
        this.disqualified = new boolean[num_players];
        this.raw_scores = new int[num_players];
        this.score_prikeys = new int[num_players];
        this.no_scores = new boolean[num_players];
        this.submission_keys = new int[num_players];


        String query = "SELECT * FROM score WHERE game_id = " + game_id;
        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);

        if (res != null)
        {
            try
            {
                int counter = 0;
                while (res.next())
                {
                    this.score_prikeys[counter] = res.getInt("id");
                    this.raw_scores[counter] = res.getInt("score");
                    this.disqualified[counter] = (res.getInt("disqualified") == 1);
                    this.no_scores[counter] = (res.getInt("no_score") == 1);
                    this.submission_keys[counter] = (res.getInt("submission_key"));

                    counter++;
                }

                DBManager.disconnect(res);          // disconnect by result
            }
            catch (Exception e)
            {
                String error = "Score constructor (game_id) - SQL error retrieving score data. " + e;
                SystemState.Log(error);

                if (SystemState.DEBUG)
                    System.out.println (error);

                throw new Exception ("Scores for game " + game_id + " not found. Incorrect scores constructor called at this location.");
            }
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
            throw new Exception ("Scores for game " + game_id + " not found. Incorrect scores constructor called at this location.");
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
                    + ", no_score = " + DBManager.BoolValue(no_score)
                    + ", disqualified = " + DBManager.BoolValue(this.disqualified[i])
                    + " WHERE id = " + this.score_prikeys[i];
            DBManager.Execute(query);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Various accessor functions
     * @return - requested data
     */
    public boolean Disqualified (int player_position) { return this.disqualified[player_position]; }


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


    /**
     * Nick Sifniotis u5809912
     * 12/09/2015
     *
     * Accessor functions for the LiveLadder class
     * Throws an exception if the player that is being searched for doesn't exist.
     *
     * @param player_id @TODO: these things
     * @return
     * @throws Exception
     */
    public int ScoreFor (int player_id) throws Exception
    {
        return 0;
    }

    public int ScoreAgainst (int player_id) throws Exception
    {
        return 0;
    }
}
