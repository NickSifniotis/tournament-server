package PlayerMarshall.DataModelInterfaces;



/**
 * Created by nsifniotis on 16/09/15.
 *
 * This class enforces the correct view of the underlying data model onto the PlayerMarshall class.
 *
 * It's the third layer.
 *
 * Database -> Data Model objects -> Data model views -> main application systems.
 *
 * The hope is by creating this third layer of abstraction, and enforcing read/write rules
 * for different fields in the database tables, the number of 'undocumented features' and
 * strange artifacts that these programs produce will be reduced.
 *
 * The PlayerMarshall 'view' of the PlayerSubmission entity is:
 * id                   RW   prikey int auto_inc
 * tournament_id        W    int
 * team_name            W    string
 * team_email           W    string
 * team_avatar          W    string
 * retired              W    boolean def false
 * marshalled_source    R    string
 *
 */
public class PlayerSubmission
{
    private Common.DataModel.PlayerSubmission data_object;


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Basic constructor for this wrapper object.
     *
     * @param source - the data model object that's being wrapped.
     */
    public PlayerSubmission (Common.DataModel.PlayerSubmission source)
    {
        this.data_object = source;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Holy shit, a blank constructor!
     * This literally means that a new object needs to be created and saved into the database.
     */
    public PlayerSubmission ()
    {
        this(new Common.DataModel.PlayerSubmission(true));
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Wrapper method for the entity within the common.datamodel package.
     *
     * @param name - the team name to search for
     * @param t_id - the tournament that the player plays in.
     * @return
     */
    public static PlayerSubmission GetActiveWithTeamName(String name, int t_id)
    {
        Common.DataModel.PlayerSubmission result = Common.DataModel.PlayerSubmission.GetActiveWithTeamName(name, t_id);
        if (result == null)
            return null;

        return new PlayerSubmission(result);
    }


    /**
     * Niick Sifniotis u5809912
     * 16/09/2015
     *
     * Wrapper method for the entity within common.datamodel
     *
     * @return - the fixture slot, if any, assigned to this player.
     */
    public int FixtureSlotAllocation ()
    {
        return this.data_object.FixtureSlotAllocation();
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Another wrapper method.
     *
     */
    public void Retire ()
    {
        this.data_object.Retire();
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Finally a more interesting method.
     * Sets a bunch of data in the DMObject.
     *
     * @param metadata - as extracted by the Verification package
     * @param tournament_id - the tournament to assign this player to.
     */
    public void SetData (AcademicsInterface.SubmissionMetadata metadata, int tournament_id)
    {
        this.data_object.setName(metadata.team_name);
        this.data_object.setEmail(metadata.team_email);
        this.data_object.setTournamentKey(tournament_id);
    }


    /**
     * Nick Sifniotis u5809912
     * 20/09/2015
     *
     * Record the fact that this team has a picture they'd like to associate with their submission.
     */
    public void SetUsesAvatar()
    {
        this.data_object.setAvatar(true);
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * The accessor functions (as defined in the view contract)
     *
     * @return the requested data
     */
    public int PrimaryKey() { return this.data_object.PrimaryKey(); }
    public String MarshalledSource() { return this.data_object.MarshalledSource(); }
    public static int CountRegisteredPlayers (int t_id) { return Common.DataModel.PlayerSubmission.CountRegisteredPlayers(t_id); }
}
