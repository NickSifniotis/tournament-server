package PlayerMarshall.DataModelInterfaces;

import AcademicsInterface.IVerification;
import Common.SystemState;

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
    public boolean Running() { return data_object.GameOn(); }
    public boolean AllowResubmit() { return (data_object.GameOn()) ? data_object.AllowResubmitOn() : data_object.AllowResubmitOff(); }
    public boolean AllowSubmit() { return !data_object.GameOn(); }
    public int AvailableSlot() throws Exception { return data_object.GetNextAvailableSlot(); }
    public IVerification Verification() { return data_object.Verification(); }

    public void AssignSlotToPlayer (int slot_id, int player_id)
    {
        data_object.AddPlayerToFixture(slot_id, player_id);
    }
}
