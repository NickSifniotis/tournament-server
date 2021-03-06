package Common;

import Services.Messages.TwitterMessage;
import Services.ServiceManager;
import Services.Twitter.*;
import Services.Twitter.Data.TwitterConfig;
import Services.TwitterService;

/**
 * Created by nsifniotis on 6/10/15.
 *
 * Manager class for the twitter service.
 *
 */
public class TwitterManager
{
    private static ServiceManager service = new ServiceManager(TwitterService.class);


    public static void StartService() { service.StartService(); }

    public static void StopService() { service.StopService(); }

    public static boolean Alive() { return service.Alive(); }

    public static void SendTweet (String tweet)
    {
        TwitterConfig credentials = Services.Twitter.Repository.GetTwitterConfig(1);
        service.SendMessage(new TwitterMessage(credentials, tweet));
    }
}
