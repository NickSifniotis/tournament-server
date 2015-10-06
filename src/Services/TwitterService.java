package Services;

import Common.LogManager;
import Services.Logs.LogType;
import Services.Messages.Message;
import Services.Messages.TwitterMessage;
import Services.Twitter.Data.TwitterConfig;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Created by nsifniotis on 28/09/15.
 *
 * TwitterService feed service.
 */
public class TwitterService extends Service
{
    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * The message handler.
     * The only messages that this twitter service is likely to receive
     * are messages asking it to transmit a tweet.
     *
     * @param message - the message that they have to handle.
     */
    @Override
    public void handle_message (Message message)
    {
        if (!(message instanceof TwitterMessage))
            return;

        TwitterMessage twit = (TwitterMessage) message;
        send_tweet(twit.config, twit.tweet);
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Sends a tweet to the twitter account tweeting this tournament.
     *
     * @param tweet - the tweet to tweet
     * @param config - the credentials for the twitter account to post to.
     */
    private void send_tweet (TwitterConfig config, String tweet)
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(config.ConsumerKey())
                .setOAuthConsumerSecret(config.ConsumerSecret())
                .setOAuthAccessToken(config.AccessToken())
                .setOAuthAccessTokenSecret(config.AccessTokenSecret());
                /*
                .setOAuthConsumerKey("VUvpKkoJtGpGTS8tK7qkaX3dT")
                .setOAuthConsumerSecret("jpN4a8JxiGZNjZGyFoy7MaIZlkubH17sUfzUmii45Si53UO8tK")
                .setOAuthAccessToken("3588649154-8XasvhTp1mVLnuWm15AFB67R6UqhkvRY43v4BDw")
                .setOAuthAccessTokenSecret("Y0ba6KAQ49CaWWynFYSIpsBxl9p9iwvddNbu7vZjJVkG2"); */
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try
        {
            twitter.updateStatus(tweet);
        }
        catch (Exception e)
        {
            String error = "Error sending tweet " + tweet + ": " + e;
            LogManager.Log(LogType.ERROR, error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * The twitter service does f-all other than process messages.
     */
    @Override
    public void do_service()
    {
        // do fuck all
    }
}
