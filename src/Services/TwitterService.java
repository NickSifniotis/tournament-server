package Services;

import Services.Messages.Message;
import Services.Twitter.TwitterConfig;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Created by nsifniotis on 28/09/15.
 *
 * TwitterService feed service.
 */
public class TwitterService extends Service
{
    private TwitterConfig config;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * The constructor for this Twitter service.
     *
     * @param config - the configuration object constructed from the
     *               data repository. This object contains the authentication
     *               data needed to connect to and post tweets on twitter.
     */
    public TwitterService(TwitterConfig config)
    {
        super();
        this.config = config;
    }


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
    void handle_message (Message message)
    {

    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Sends a tweet to the twitter account tweeting this tournament.
     *
     * @param tweet - the tweet to tweet
     */
    private void send_tweet (String tweet)
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(config.consumer_key)
                .setOAuthConsumerSecret(config.consumer_secret)
                .setOAuthAccessToken(config.access_token)
                .setOAuthAccessTokenSecret(config.access_token_secret);
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
            e.printStackTrace();
        }
    }
}
