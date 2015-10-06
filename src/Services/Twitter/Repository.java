package Services.Twitter;

import Common.DataModelObject.Entities;
import Common.LogManager;
import Services.Logs.LogType;
import Services.Twitter.Data.TwitterConfig;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * The interface to the main data repository.
 * The twitter service only needs read access to one data object -
 * twitter_configuration.
 */
public class Repository
{
    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Returns a specific configuration, by ID
     *
     * @param id - the thing to return
     * @return - the returned thing
     */
    public static TwitterConfig GetTwitterConfig (int id)
    {
        try
        {
            return new TwitterConfig(Common.Repository.GetTwitterConfig (id));
        }
        catch (Exception e)
        {
            String error = "TwitterConfig Repo error - id = " + id + ": " + e;
            LogManager.Log(LogType.ERROR, error);
        }

        return null;
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * @return all of the things!
     */
    public static TwitterConfig[] GetTwitterConfigs ()
    {
        Common.DataModelObject.TwitterConfig[] holding = Common.Repository.GetTwitterConfigs();
        TwitterConfig[] res = new TwitterConfig[holding.length];

        try
        {
            for (int i = 0; i < holding.length; i++)
                res[i] = new TwitterConfig(holding[i]);
        }
        catch (Exception e)
        {
            String error = "Twitter repo - error in the getAllConfigs. This error is literally impossible to trigger. " + e;
            LogManager.Log(LogType.ERROR, error);
        }

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * @return and create a new twitter configuration object.
     */
    public static TwitterConfig NewConfig()
    {
        Common.DataModelObject.TwitterConfig new_conf = (Common.DataModelObject.TwitterConfig) Common.Repository.NewEntity(Entities.TWITTER_CONFIG);
        return new TwitterConfig(new_conf);
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Saves the configuration entity to the database.
     *
     * @param conf - the entity to save....
     */
    public static void SaveConfig(TwitterConfig conf)
    {
        Common.Repository.SaveTwitterConfig(conf.ID());
    }
}
