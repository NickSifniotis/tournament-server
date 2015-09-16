package TournamentServer.DataModelInterfaces;

import Common.DataModel.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * The Game entity contract for TournamentServer.
 * This is one half of the tricky concurrency issue - the other one is PlayerSubmission.
 * Get this right and the server will tick over nicely.
 * Get this wrong and you might have to abandon your coding ambitions.
 *
 *
 */
public class Game
{
    private Common.DataModel.Game data_object;


    public Game (Common.DataModel.Game item)
    {
        data_object = item;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Wrapper class for the LoadAll method.
     * Only return playable games (taken care of by the supermethod)
     * that have not been superceded (taken care of here)
     *
     * @param tournaments - the tournaments to load games for
     * @return an array containing all games.
     */
    public static Game[] LoadAll(int[] tournaments)
    {
        Common.DataModel.Game[] all = Common.DataModel.Game.LoadAll(tournaments, true);
        List<Game> holding = new LinkedList<>();
        for (Common.DataModel.Game g: all)
            if (!g.Superceded())
                holding.add (new Game(g));

        Game[] res = new Game[holding.size()];
        return holding.toArray(res);
    }


    /**
     * Nick Sifniotis u5809902
     * 16/09/2015
     *
     * Accessor functions
     *
     * @return data
     */
    public int PrimaryKey() { return data_object.PrimaryKey(); }
    public int TournamentKey() { return data_object.TournamentId(); }
    public void StartGame() { data_object.StartGame(); }
    public void EndGame() { data_object.EndGame(); }
    public void Terminate() { data_object.Terminate(); }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Obtain the player submissions for this game.
     *
     * @return an array containing the playersubmission objects.
     */
    public PlayerSubmission[] Players ()
    {
        Common.DataModel.PlayerSubmission[] all = data_object.GetPlayers();
        PlayerSubmission[] res = new PlayerSubmission[all.length];
        for (int i = 0; i < all.length; i ++)
            res[i] = new PlayerSubmission(all[i]);

        return res;
    }
}
