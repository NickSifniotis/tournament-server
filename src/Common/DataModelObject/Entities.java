package Common.DataModelObject;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * Enumerate the different database entity types.
 * This will make the code in the repos seem cleaner.
 *
 */
public enum Entities
{
    GAME_TYPE, TOURNAMENT, GAME, SUBMISSION, SCORE, TWITTER_CONFIG;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * @return the database table name for this entity
     */
    public String TableName()
    {
        String res = "";
        switch (this)
        {
            case GAME:
                res = "game";
                break;
            case GAME_TYPE:
                res = "game_type";
                break;
            case TOURNAMENT:
                res = "tournament";
                break;
            case SUBMISSION:
                res = "submission";
                break;
            case SCORE:
                res = "score";
                break;
            case TWITTER_CONFIG:
                res = "twitter_config";
                break;
        }
        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * @return the abstract class info corresponding to the entity
     */
    public Class Class()
    {
        Class res = null;

        switch (this)
        {
            case GAME:
              //  res = "game";
                break;
            case GAME_TYPE:
                res = GameType.class;
                break;
            case TOURNAMENT:
              //  res = "tournament";
                break;
            case SUBMISSION:
              //  res = "submission";
                break;
            case SCORE:
             //   res = "score";
                break;
            case TWITTER_CONFIG:
                res = TwitterConfig.class;
                break;
        }

        return res;
    }
}
