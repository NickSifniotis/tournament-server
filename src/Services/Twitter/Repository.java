package Services.Twitter;

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
    public static TwitterConfig GetTwitterConfig (int id)
    {
        TwitterConfig res = new TwitterConfig(Common.Repository.GetTwitterConfig (id));
        return res;
    }
}
