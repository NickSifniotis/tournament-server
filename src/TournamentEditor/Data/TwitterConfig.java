package TournamentEditor.Data;

/**
 * Created by nsifniotis on 07/10/15.
 *
 * Stores the view that the Tournament Editor needs. Which is reduced read only access to the data.
 *
 */
public class TwitterConfig
{
    private Common.DataModelObject.TwitterConfig data_source;


    /**
     * Nick Sifniotis u5809912
     * 07/10/15
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
     * 07/10/15
     *
     * The accessor (read only) methods.
     */
    public int ID () { return data_source.id; }

    /**
     * Nick Sifniotis u5809912
     * 07/10/15
     *
     * @return a string representation of this thing.
     */
    @Override
    public String toString () { return this.data_source.account_name; }
}
