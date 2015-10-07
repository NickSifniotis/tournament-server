package TournamentEditor;

import TournamentEditor.Data.TwitterConfig;

/**
 * Created by nsifniotis on 7/10/15.
 *
 * Data repo for the tournament editor interface.
 */
public class Repository
{

    public static TwitterConfig[] GetTwitterConfigs()
    {
        Common.DataModelObject.TwitterConfig[] holding = Common.Repository.GetTwitterConfigs();
        TwitterConfig[] res = new TwitterConfig[holding.length];

        for (int i = 0; i < res.length; i ++)
            res[i] = new TwitterConfig(holding[i]);

        return res;
    }
}
