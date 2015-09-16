package PlayerMarshall.DataModelInterfaces;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * Game data model view for the PlayerMarshall.
 *
 * Contract that this class enforces is as follows:
 *
 * id               int prikey              R
 * round_number     integer                 W PM (T on)
 * game_number      integer                 W PM (T on)
 * tournament_id    integer fk              W PM (T on)
 * superceded       boolean                 W PM
 *
 * T on - this means that access is granted only when the game's tournament is both valid
 * (non null) and currently running. This is used to replicate game records in the database
 * when a player's retirement / replacement sees existing games be superceded.
 */
public class Game
{
    private Common.DataModel.Game data_object;


    public Game (Common.DataModel.Game item)
    {
        data_object = item;
    }


    public int PrimaryKey() { return data_object.PrimaryKey(); }

    public static void SupercedeGames (int fixture_position) { Common.DataModel.Game.ResetAll(fixture_position);}
}
