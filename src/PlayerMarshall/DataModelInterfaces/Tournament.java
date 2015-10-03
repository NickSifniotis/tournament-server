package PlayerMarshall.DataModelInterfaces;

import AcademicsInterface.IVerification;
import Common.SystemState;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * The Tournament data model view
 *
 * id               R       primarykey autoincrement
 * name             R       string
 * input_folder     R       string
 * running          R       boolean
 * allow_resubmit   R       boolean
 * allow_submit     R       boolean
 * available_slot   R       int
 * verification     R       IVerification instance
 *
 * and methods
 *
 * AssignSlotToPlayer (int slot_id, int player_id)
 *
 */
public class Tournament
{
    private Common.DataModel.Tournament data_object;

    public Tournament (Common.DataModel.Tournament item)
    {
        this.data_object = item;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * The accessor functions
     *
     * @return the data requested.
     */
    public int PrimaryKey() { return data_object.PrimaryKey(); }
    public String Name() { return data_object.Name(); }
    public String InputFolder() { return SystemState.input_folder + data_object.PrimaryKey() + "/"; }
    public boolean AllowResubmit() { return data_object.AllowResubmit(); }
    public boolean AllowSubmit() { return data_object.AllowSubmit(); }
    public int AvailableSlot() throws Exception { return data_object.GetNextAvailableSlot(); }
    public IVerification Verification() { return data_object.Verification(); }

    public void AssignSlotToPlayer (int slot_id, int player_id)
    {
        data_object.AddPlayerToFixture(slot_id, player_id);
    }

    public static Tournament[] LoadAll()
    {
        Common.DataModel.Tournament[] tourneys = Common.DataModel.Tournament.LoadAll();
        List<Tournament> results = new LinkedList<>();

        for (int i = 0; i < tourneys.length; i ++)
            if (!tourneys[i].GameOn())
                results.add (new Tournament(tourneys[i]));

        Tournament[] holding = new Tournament[results.size()];
        return results.toArray(holding);
    }
}
