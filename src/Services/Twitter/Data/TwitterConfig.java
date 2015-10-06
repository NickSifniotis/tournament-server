package Services.Twitter.Data;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * Data holding class that holds a tournament's twitter creds.
 * This might be merged into the data repo in due course. Or
 * at least refactored, fkn public member variables..
 *
 * @TODO does not gracefully handle twitter configs that don't exist
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
    public String ConsumerKey () { return data_source.consumer_key; }
    public String ConsumerSecret () { return data_source.consumer_secret; }
    public String AccessToken () { return data_source.access_token; }
    public String AccessTokenSecret () { return data_source.access_token_secret; }
}
