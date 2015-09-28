package GameManager;

import GameManager.Data.GameType;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * Local data repository for the GameManager GUI
 *
 * Allows read/write access to the GameType data model objects
 * and no other thing.
 */
public class Repository
{
    public GameType GetGameType (int id)
    {
        Common.DataModelObject.GameType game = Common.Repository.GetGameType(id);

        if (game == null)
            return null;

        return new GameType(Common.Repository.GetGameType(id));
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Get all the game types!
     *
     * @return them all in a nice array
     */
    public GameType[] GetGameTypes()
    {
        Common.DataModelObject.GameType[] games = Common.Repository.GetGameTypes();
        GameType[] res = new GameType[games.length];

        for (int i = 0; i < games.length; i++)
            res[i] = new GameType(games[i]);

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Create a new row in the database.
     *
     * @return a new game type object that represents a new game ...
     */
    public GameType NewGameType()
    {
        return new GameType(Common.Repository.NewGameType());
    }
}
