package Services.Twitter.Data;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * Data holding class that holds a tournament's twitter creds.
 * This might be merged into the data repo in due course. Or
 * at least refactored, fkn public member variables..
 *
 */
public class TwitterConfig
{
    private Common.DataModelObject.TwitterConfig data_source;


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Simple constructor.
     *
     * @param inputs - the data object to connect to.
     */
    public TwitterConfig (Common.DataModelObject.TwitterConfig inputs)
    {
        this.data_source = inputs;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * The accessor (read only) methods.
     */
    public int ID () { return data_source.id; }
    public String AccountName () { return data_source.account_name; }
    public String ConsumerKey () { return data_source.consumer_key; }
    public String ConsumerSecret () { return data_source.consumer_secret; }
    public String AccessToken () { return data_source.access_token; }
    public String AccessTokenSecret () { return data_source.access_token_secret; }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * The setter methods.
     *
     * @param s - the thing to set.
     */
    public void SetAccountName (String s) { this.data_source.account_name = s; }
    public void SetConsumerKey (String s) { this.data_source.consumer_key = s; }
    public void SetConsumerSecret (String s) { this.data_source.consumer_secret = s; }
    public void SetAccessToken (String s) { this.data_source.access_token = s; }
    public void SetAccessTokenSecret (String s) { this.data_source.access_token_secret = s; }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * @return a string representation of this thing.
     */
    @Override
    public String toString () { return this.data_source.account_name; }
}
