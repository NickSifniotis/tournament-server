package Common;

import Common.Email.EmailTypes;
import Common.Email.Emailer;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * hahahahaha omg twitter!
 */
public class testeer
{
    public static void main(String[] args)
    {
        Emailer.SendEmail(EmailTypes.NO_SLOTS_AVAILABLE, "u5809912@anu.edu.au", 1);
/*
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("VUvpKkoJtGpGTS8tK7qkaX3dT")
                .setOAuthConsumerSecret("jpN4a8JxiGZNjZGyFoy7MaIZlkubH17sUfzUmii45Si53UO8tK")
                .setOAuthAccessToken("3588649154-8XasvhTp1mVLnuWm15AFB67R6UqhkvRY43v4BDw")
                .setOAuthAccessTokenSecret("Y0ba6KAQ49CaWWynFYSIpsBxl9p9iwvddNbu7vZjJVkG2");
        TwitterFactory tf = new TwitterFactory(cb.build());
        TwitterService twitter = tf.getInstance();

        try
        {
            Status status = twitter.updateStatus("Tournament Server again. Testing to see whether the feed to Nick's facebook is working.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }
}
