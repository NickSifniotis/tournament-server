package Services.Messages;


import Services.Twitter.Data.TwitterConfig;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * The start of something that could be a message framework or something.
 */
public class TwitterMessage extends Message
{
    public TwitterConfig config;
    public String tweet;


    public TwitterMessage (TwitterConfig c, String t)
    {
        this.config = c;
        this.tweet = t;
    }
}
