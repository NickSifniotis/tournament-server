package PlayerMarshall.DataModel;

import PlayerMarshall.DBManager;
import PlayerMarshall.SystemState;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * This class represents one player submission for a tournament.
 * As well as holding the data, this class interfaces with the database layer
 * to load and save its own state (as directed ..)
 */
public class PlayerSubmission {
    private int id;
    private int tournament_id;

    private String name;
    private String email;
    private String motto;
    private String orig_file;
    private String avatar_file;

    private boolean retired;
    private int disqualified_count;
    private boolean ready;


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Default blank constructor.
     *
     */
    public PlayerSubmission()
    {
        id = 0;
        tournament_id = 0;
        name = "";
        email = "";
        motto = "";
        avatar_file = "";
        orig_file = "";
        retired = false;
        disqualified_count = 0;
        ready = false;
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Constructor - the flag specifies whether to create a blank entry in the DB and
     * extract the new prikey, or, whether to simply create a blank object.
     */
    public PlayerSubmission (boolean create_database)
    {
        this();

        if (create_database)
            saveState();
    }

    
    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Dump the current state into the database.
     * The exact method used will depend on whether or not the record already exists
     * in the database ..
     */
    public void saveState ()
    {
        SystemState.Log("Saving state for player submission " + this.id);


        // is this submission already in the database?
        boolean exists = false;
        String query;

        if (this.id != 0) {
            query = "SELECT * FROM submission WHERE id = " + id;
            Connection connection = DBManager.connect();
            ResultSet res = DBManager.ExecuteQuery(query, connection);

            if (res != null) {
                exists = true;
                DBManager.disconnect(res);          // disconnect by result
            } else {
                DBManager.disconnect(connection);   // disconnect by connection
            }
        }

        if (exists)
        {
            query = "UPDATE submission SET team_name = '" + this.name
                    + "', team_email = '" + this.email
                    + "', team_avatar = '" + this.avatar_file
                    + "', team_motto = '" + this.motto
                    + "', original_filename = '" + this.orig_file
                    + "', retired = " + DBManager.BoolValue(this.retired)
                    + ", disqualified = , " + this.disqualified_count
                    + ", ready = " + DBManager.BoolValue(this.ready)
                    + ", tournament_id = " + this.tournament_id
                    + " WHERE id = " + this.id;

            if (SystemState.DEBUG) System.out.println (query); else SystemState.Log(query);

            DBManager.Execute(query);
        }
        else
        {
            query = "INSERT INTO submission (team_name, team_email, team_motto, team_avatar"
                    + ", original_filename, retired, disqualified, ready, tournament_id) VALUES ("
                    + "'" + this.name + "'"
                    + ", '" + this.email + "'"
                    + ", '" + this.motto + "'"
                    + ", '" + this.avatar_file + "'"
                    + ", '" + this.orig_file + "'"
                    + ", " + DBManager.BoolValue(this.retired)
                    + ", " + this.disqualified_count
                    + ", " + DBManager.BoolValue(this.ready)
                    + ", " + this.tournament_id + ")";

            if (SystemState.DEBUG) System.out.println (query); else SystemState.Log(query);

            // we do want to know what the primary key of this new record is.
            this.id = DBManager.ExecuteReturnKey(query);
        }
    }
}
