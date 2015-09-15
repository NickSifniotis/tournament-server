package Common.DataModel;

import Common.DBManager;
import Common.Logs.LogManager;
import Common.Logs.LogType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * Data model object for the Game database table.
 *
 * DB schema is as follows
 *
 * id               int prikey
 * round_number     integer
 * game_number      integer
 * tournament_id    integer fk
 * played           boolean
 * in_progress      boolean
 *
 */
public class Game
{
    private int id;
    private int round_number;
    private int game_number;
    private Tournament tournament;
    private boolean played;
    private boolean in_progress;


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Empty constructor for an empty state.
     */
    public Game ()
    {
        this.loadState();
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * An empty constructor, but one that saves itself to the database.
     * Used when we need to get a prikey for a new game.
     *
     * @param create - true if we want to save this new record in the database.
     */
    public Game (boolean create)
    {
        this.loadState();

        if (create)
            this.SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Create this object from the given resultset inputs.
     * Just like the other four datamodel objects...
     *
     * @param input - resultset from the database.
     *
     */
    public Game (ResultSet input)
    {
        try
        {
            this.loadState(input);
        }
        catch (Exception e)
        {
            String error = "Game.constructor (resultset): Error creating from resultset input: " + e;
            LogManager.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Last one I swear. Construct by primary key. Create a blank new object if it fails.
     *
     * @param id - the prikey to find.
     */
    public Game (int id)
    {
        String query;

        if (id > 0)
        {
            query =  "SELECT * FROM game WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                try
                {
                    res.next();
                    this.loadState(res);
                    DBManager.disconnect(res);          // disconnect by result
                }
                catch (Exception e)
                {
                    String error = "Game constructor (game_id) - SQL error retrieving player data. " + e;
                    LogManager.Log(LogType.ERROR, error);

                    this.loadState();
                    DBManager.disconnect(connection);
                }
            }
            else
            {
                this.loadState();
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Returns an array of Game objects.
     *
     * If tournament is provided, it returns every game in the database for that
     * particular tournament. If tournament is null, it returns every game
     * in the database.
     *
     * If playable_only is true, return only those games that can be played now.
     * That test is simply Game.played == false && Game.in_progress == false.
     *
     * @param tournaments - the tournaments to query, or null for all tournaments.
     * @param playable_only - true if we are only interested in playable games.
     *
     * @return an array of Game objects that match the criteria. They are ordered
     *          by round_number so that earlier games are always played before later ones.
     */
    public static Game [] LoadAll (Tournament[] tournaments, boolean playable_only)
    {
        List<Game> res = new ArrayList<>();

        String tournament_clause = "";
        String exclusion_clause = (playable_only) ? " AND g.played = 0 AND g.in_progress = 0" : "";
        String query;

        // if we have been given a set of tournaments to poll from, build the appropriate SQL.
        if (tournaments != null)
        {
            tournament_clause = " AND g.tournament_id IN (0";
            for (Tournament tournament: tournaments)
                tournament_clause += ", " + tournament.PrimaryKey();

            tournament_clause += ")";
        }

        query = "SELECT g.* FROM game g WHERE 1" + tournament_clause + exclusion_clause + " ORDER BY g.round_number";
        Connection connection = DBManager.connect();
        ResultSet records = DBManager.ExecuteQuery(query, connection);

        if (records != null)
        {
            try
            {
                while (records.next())
                {
                    res.add(new Game(records));
                }

                DBManager.disconnect(records);          // disconnect by result
            }
            catch (Exception e)
            {
                String error = "Game.LoadAll (game_id) - SQL error retrieving game data. " + e;
                LogManager.Log(LogType.ERROR, error);
                DBManager.disconnect(connection);
            }
        }
        else
        {
            DBManager.disconnect(connection);   // disconnect by connection
        }

        Game [] temp = new Game[res.size()];
        return res.toArray(temp);
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Default object creation. Set all things to nothing.
     * Note that any method that uses the tournament property will
     * need to check for nulls.
     *
     */
    private void loadState ()
    {
        this.id = 0;
        this.round_number = 0;
        this.game_number = 0;
        this.played = false;
        this.in_progress = false;
        this.tournament = null;
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * This is like the fifth time I have had to implement this method.
     * My head is screaming 'inheritance!'
     *
     * But how do I make the method work with database schemas and
     * method names of indeterminate nature?
     *
     * That's probably a problem to tackle *after* the Blokus tournament.
     *
     * @param input - the ResultSet holding the data blah blah blah. Look at my other four comment
     *              sets in my other four implementations of this ffs.
     */
    private void loadState (ResultSet input) throws SQLException
    {
        this.id = input.getInt("id");
        this.round_number = input.getInt ("round_number");
        this.game_number = input.getInt ("game_number");
        this.played = (input.getInt("played") == 1);
        this.in_progress = (input.getInt("in_progress") == 1);
        this.tournament = new Tournament(input.getInt("tournament_id"));
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Inheritance!!! The lack of it is hurting me. Althogh I can see no reason why I should use it,
     * it's not like I'll be creating collections of random DataModel objects ...
     *
     */
    public void SaveState ()
    {
        // is this submission already in the database?
        boolean exists = false;
        String query;

        if (this.id > 0) {
            query = "SELECT * FROM game WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null)
            {
                exists = true;
                DBManager.disconnect(res);          // disconnect by result
            }
            else
            {
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }

        if (exists)
        {
            query = "UPDATE game SET round_number = " + this.round_number
                    + ", game_number = " + this.game_number
                    + ", tournament_id = " + this.tournament.PrimaryKey()
                    + ", played = " + DBManager.BoolValue(this.played)
                    + ", in_progress = " + DBManager.BoolValue(this.in_progress)
                    + " WHERE id = " + this.id;

            DBManager.Execute(query);
        }
        else
        {
            query = "INSERT INTO game (tournament_id, round_number, game_number, played, in_progress)"
                    + " VALUES ("
                    + this.tournament.PrimaryKey()
                    + ", " + this.round_number
                    + ", " + this.game_number
                    + ", " + DBManager.BoolValue(this.played)
                    + ", " + DBManager.BoolValue(this.in_progress)
                    + ")";

            // we do want to know what the primary key of this new record is.
            this.id = DBManager.ExecuteReturnKey(query);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Various accessor functions.
     *
     * @return - whatever it is that is being asked for.
     */
    public int PrimaryKey () { return this.id; }
    public Tournament Tournament () { return this.tournament; }
    public int RoundNumber () { return this.round_number; }
    public int GameNumber() { return this.game_number; }
    public boolean Started() { return this.played | this.in_progress; }
    public boolean InProgress() { return this.in_progress; }


    /**
     * Nick Sifniotis u5809912
     * 9/9/2015
     *
     * Gets the players that are fixtured to play this game.
     *
     * @return the players' submissions.
     */
    public PlayerSubmission[] GetPlayers()
    {
        List<PlayerSubmission> results = new LinkedList<>();

        String query = "SELECT s.* FROM game g, game_player gp, fixture_slot f, submission s"
                + " WHERE gp.game_id = g.id"
                + " AND f.id = gp.fixture_slot_id"
                + " AND s.id = f.submission_id"
                + " AND g.id = " + this.id
                + " ORDER BY gp.position";

        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);
        boolean error = false;

        if (res != null)
        {
            try
            {
                while (res.next())
                {
                    results.add(new PlayerSubmission(res));
                }
            }
            catch (Exception e)
            {
                String er = "Game.GetPlayers - SQL error retrieving player data. " + e;
                LogManager.Log(LogType.ERROR, er);
                error = true;
                DBManager.disconnect(connection);
            }

            DBManager.disconnect(res);          // disconnect by result
        }
        else
        {
            String er = "Game.GetPlayers - No data error retrieving player data.";
            LogManager.Log(LogType.ERROR, er);

            error = true;
            DBManager.disconnect(connection);   // disconnect by connection
        }

        if (error)
            return null;

        PlayerSubmission[] res_array = new PlayerSubmission[results.size()];
        return results.toArray(res_array);
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Records in the database the fact that this game has been started.
     *
     * @throws Exception - has a sook if the game is not in the correct state.
     */
    public void StartGame() throws Exception
    {
        if (this.in_progress || this.played)
            throw new Exception ("Unable to start a game that has already been started.");

        this.in_progress = true;
        this.SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 10/9/2015
     *
     * Signals that the game has finished being played.
     * If this method is called while in_progress is false,
     * reset the game.
     *
     */
    public void EndGame()
    {
        this.played = this.in_progress;
        this.in_progress = false;

        this.SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * There are a number of reasons why the server may want to kill off a game
     * that is in progress. One of the game players may have been retired, for
     * example, or the operator may have signalled a tournament reset.
     *
     * This method sets the flags to indicate that the game has been terminated
     * abnormally, so that when the game reaches an end it will not be saved as
     * having been 'played'.
     *
     */
    public void Terminate()
    {
        this.in_progress = false;
        this.SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * Hard reset of the game. If it is being played out, nothing will happen when EndGame is called.
     */
    public void Reset()
    {
        this.in_progress = false;
        this.played = false;
        this.SaveState();
    }


    /**
     * Nick Sifniotis u5809912
     * 15/09/2015
     *
     * Go through every game that this player is going to / has played in,
     * and perform a hard reset.
     *
     * This method is called from PlayerMarshall, when a player is being replaced by a newer one.
     *
     * @param fixture_position - the fixture slot that is being reset.
     */
    public static void ResetAll(int fixture_position)
    {
        // get a list of game_ids for which this fixture slot is playing
        String query = "SELECT * FROM game_player WHERE fixture_slot_id = " + fixture_position;

        Connection connection = DBManager.connect();
        ResultSet res = DBManager.ExecuteQuery(query, connection);
        if (res != null)
        {
            try
            {
                while (res.next())
                {
                    Game g = new Game(res.getInt("game_id"));
                    g.Reset();
                }
            }
            catch (Exception e)
            {
                String er = "Game.ResetAll - SQL error retrieving game data. " + e;
                LogManager.Log(LogType.ERROR, er);
                DBManager.disconnect(connection);
            }

            DBManager.disconnect(res);          // disconnect by result
        }
        else
        {
            String er = "Game.ResetAll - No data error retrieving game data.";
            LogManager.Log(LogType.ERROR, er);
            DBManager.disconnect(connection);   // disconnect by connection
        }
    }
}
