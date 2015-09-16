package GameManager.DataModelInterfaces;

/**
 * Created by nsifniotis on 16/09/15.
 *
 * This is probably going to be the easiest data model view contract to write,
 * since the game manager is basically a writer for this class.
 *
 * Anyway,
 *
 * id               int primary key
 * name             string                  W
 * engine_class     string                  W
 * viewer_class     string                  W
 * uses_viewer      boolean                 W
 * min_players      int                     W
 * max_players      int                     W
 * source_fname     string                  R
 */
public class GameType
{
    private Common.DataModel.GameType data_object;


    public GameType ()
    {
        data_object = new Common.DataModel.GameType();
    }


    public GameType (Common.DataModel.GameType item)
    {
        data_object = item;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Wrapper class for the LoadAll function
     *
     * @return all the game types - ?
     */
    public static GameType[] LoadAll ()
    {
        Common.DataModel.GameType[] all = Common.DataModel.GameType.LoadAll();
        GameType[] res = new GameType[all.length];

        for (int i = 0; i < all.length; i ++)
            res[i] = new GameType(all[i]);

        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Accessor functions
     *
     * @return data
     */
    public String Name() { return data_object.Name(); }
    public int MinPlayers() { return data_object.MinPlayers(); }
    public int MaxPlayers() { return data_object.MaxPlayers(); }
    public String GameEngineClass() { return data_object.GameEngineClass(); }
    public String ViewerClass() { return data_object.ViewerClass(); }
    public String SourceFilename() { return data_object.SourceFilename(); }


    /**
     * Nick Sifniotis u5809912
     * 16/09/2015
     *
     * Setter methods.
     *
     * @param name - various parameters to set
     */
    public void SetName(String name) { data_object.SetName(name); data_object.SaveState(); }
    public void SetGameEngineClass(String gec) { data_object.SetGameEngineClass(gec); data_object.SaveState(); }
    public void SetViewerClass(String vc) { data_object.SetViewerClass(vc); data_object.SaveState(); }
    public void SetMinPlayers(int n) { data_object.SetMinPlayers(n); data_object.SaveState(); }
    public void SetMaxPlayers(int n) { data_object.SetMaxPlayers(n); data_object.SaveState(); }
    public void SetUsesViewer(boolean b) { data_object.SetUsesViewer(b); data_object.SaveState(); }


    @Override
    public String toString ()
    {
        return data_object.Name();
    }
}
